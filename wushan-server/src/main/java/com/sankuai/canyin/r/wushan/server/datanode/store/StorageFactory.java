package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.service.DBInfo;
import com.sankuai.canyin.r.wushan.service.DataNodeClientSideService;
import com.sankuai.canyin.r.wushan.thread.WushanThreadFactory;

/**
 * data : data后缀的文件保存原始的数据内容
 * meta : meta后缀文件保存原始数据的key和对应的偏移量 key.len(int) + key(var-len) + num(int) + start(long) + offset(long)
 * 
 * 
 * @author kyrin
 *
 */

public class StorageFactory {

	private static final Logger LOG = LoggerFactory.getLogger(StorageFactory.class);
	
	private Map<String , DataFile> cache = new ConcurrentHashMap<String, DataFile>();//索引偏移量缓存

	private volatile Map<String , Map<String, DataFile>> offset = new ConcurrentHashMap<String , Map<String, DataFile>>();

	private Map<String,Queue<DataPacket>> db = new ConcurrentHashMap<String, Queue<DataPacket>>();
	
	private ExecutorService exec = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 
			Runtime.getRuntime().availableProcessors(),0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new WushanThreadFactory("exec"));
	
	private ExecutorService sync = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 
			Runtime.getRuntime().availableProcessors(),0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new WushanThreadFactory("sync"));
	
	private ExecutorService reload = new ThreadPoolExecutor(0,Runtime.getRuntime().availableProcessors()
			, 2000l , TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1024) , new WushanThreadFactory("reload"));
	
	private static final int INTERVAL = 1;// 毫秒
	
	private static final int LEVEL_SIZE = 64;

	private ReentrantLock immutableLock = new ReentrantLock();
	
	private ReentrantLock offsetLock = new ReentrantLock();
	
	private String storePath;
	
	private static final String DEFAULT_DB_NAME = "default";
	
	private Map<String,Counter> current_db_size = new ConcurrentHashMap<String, Counter>();
	
	private static final int MAX_SIZE = 2 << 23;//byte
	
	private static final int DATA_FILE_MAX_SZIE = 2 << 26;//byte,128M
	
	private static final int SCHEDULE_INTERVAL = 30 * 1000;
	
	private Map<String , FileDesc> current_open_files = new HashMap<String, FileDesc>();
	
	private volatile boolean isSchedule = false;
	
	private int tmp_size = 0;
	
	private Configuration config;
	
	private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
	
	private DataNodeClientSideService protocolImpl;

	public StorageFactory(Configuration config , DataNodeClientSideService protocolImpl) {
		this.config = config;
		storePath = config.getDataNodeStorePath();
		this.protocolImpl = protocolImpl;
	}
	
	public void init(){
		schedule.scheduleAtFixedRate(new Runnable() {
			public void run() {
				LOG.info(">>> Timer 开始调度 <<< 目前总大小："+tmp_size+" ， key的数量 ： "+cache.size()+" , db count = "+current_db_size.size());
				schedule();
				
				//uploaad dbinfo
				protocolImpl.commitDBInfo(getDBInfos());
			}
		}, 10, 5, TimeUnit.SECONDS);
		
		reload();
	}
	
	public void put(String db , String key, byte[] value) {
		final String dbName = db == null || db.length() == 0 ? DEFAULT_DB_NAME:db;
		Counter counter = current_db_size.get(dbName);
		if(counter == null){
			counter = new Counter(0);
			current_db_size.put(dbName, counter);
		}
		if(counter.getValue() >= MAX_SIZE && isSchedule){
			try {
				TimeUnit.MILLISECONDS.sleep(INTERVAL);
			} catch (InterruptedException e) {
			}
		}
		immutableLock.lock();
		try {
			tmp_size+= value.length;
			counter.getAndAdd(key.length() + value.length);
			Queue<DataPacket> queue = this.db.get(dbName);
			if(queue == null){
				queue = new LinkedBlockingQueue<DataPacket>();
				this.db.put(dbName, queue);
			}
			queue.add(new DataPacket(null,key.getBytes(), value));
			if(counter.getValue() >= MAX_SIZE && !isSchedule){
				LOG.info("DB {} start flushing. current_size={} bytes",dbName,counter.getValue());
				exec.submit(new Runnable() {
					public void run() {
						flush(false,dbName);
					}
				});
			}
		} finally {
			immutableLock.unlock();
		}
	}
	
	private void saveOffset(String key, Location value , Map<String , DataFile> map){
		offsetLock.lock();
		try {
			if (map.containsKey(key) && map.get(key) != null) {
				map.get(key).getOffsets().add(value);
			} else {
				DataFile dataFile = new DataFile(key);
				dataFile.getOffsets().add(value);
				map.put(key, dataFile);
			}
		} finally {
			offsetLock.unlock();
		}
	}
	
	private String generateCurrentParentPath(String path){
		File file = new File(path);
		if(file.isDirectory()){
			if(file.list().length < LEVEL_SIZE){
				return file.getAbsolutePath();
			}
			int hc = Math.abs(new Random().nextInt(LEVEL_SIZE));
			return generateCurrentParentPath(path+"/"+hc);
		}else{
			file.mkdir();
			return file.getAbsolutePath();
		}
	}
	
	private void flush(boolean force , String db) {
		if (isSchedule) {
			return;
		}
		isSchedule = true;
		try {
			// 1.替换
			immutableLock.lock();
			Queue<DataPacket> target = this.db.remove(db);
			try {
				LinkedBlockingQueue<DataPacket> tmpQueue = new LinkedBlockingQueue<DataPacket>();
				this.db.put(db, tmpQueue);
				this.current_db_size.get(db).clear();
			} finally {
				immutableLock.unlock();
			}
			if (target == null || target.isEmpty()) {
				return;
			}
			checkFile(db, false);
			//同步数据
			Iterator<DataPacket> it = target.iterator();
			RandomAccessFile dataFile = current_open_files.get(db).getData();
			while (it.hasNext()) {
				write(dataFile,it.next(),db);
			}
			// 2.判断持久化文件是否达到最大值
			checkFile(db,force);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isSchedule = false;
		}
	}
	
	private synchronized void schedule(){
		try {
			String db = null;
			for(String key : this.current_db_size.keySet()){
				if(this.current_db_size.get(key) != null && this.db.containsKey(key) && this.db.get(key).size() > 0 ){
					db = key;
				}
			}
			if(!isSchedule && db != null && System.currentTimeMillis() - this.current_db_size.get(db).getLastTimestamp() >= SCHEDULE_INTERVAL ){
				flush(true,db);
			}
		} catch (Exception e) {
			LOG.error("schedule thread failed.",e);
		}
	}
	
	private void checkFile(final String db , boolean force) throws IOException{
		LOG.info("check file status for db. db = {} , force = {}",db,force);
		FileDesc fileDesc = current_open_files.get(db);
		if(fileDesc == null){
			createNewFile(db);
			return;
		}
		if(fileDesc.getData().length() >= DATA_FILE_MAX_SZIE || force){
			fileDesc.getData().getFD().sync();
			fileDesc.getData().close();
			
			final RandomAccessFile target = fileDesc.getIndex();
			
			if(target != null && target.getChannel().isOpen()){
				sync.execute(new Runnable() {
					public void run() {
						try {
							syncIndex(target , db);
						} catch (IOException e) {
							LOG.info("persistent index failed. db = {} , indexFile = {}",db,target,e);
						}
					}
				});
			}
			createNewFile(db);
		}
	}
	
	private void createNewFile(String db) throws FileNotFoundException{
		long datetime = System.nanoTime();
		String parentPath = generateCurrentParentPath(storePath+"/"+db); 
		String currentDataPath = parentPath+"/"+datetime+".data";
		String currentMetaPath = parentPath+"/"+datetime+".meta";
		RandomAccessFile currentDataFile = new RandomAccessFile(new File(currentDataPath), "rws");
		RandomAccessFile currentMetaFile = new RandomAccessFile(new File(currentMetaPath), "rws");
		FileDesc fileDesc = new FileDesc(currentDataFile, currentMetaFile);
		current_open_files.put(db, fileDesc);
		LOG.info("create new file : data = {} , meta = {}",currentDataPath,currentMetaPath);
	}
	
	private void syncIndex(final RandomAccessFile idx , final String db) throws IOException {
		LOG.info("start sync index.indexFile = {}",idx);
		offsetLock.lock();
		Map<String, DataFile> target = null;
		try {
			target = offset.remove(db);
			offset.put(db, new ConcurrentHashMap<String, DataFile>());
		} finally {
			offsetLock.unlock();
		}
		try{
			if (target == null || target.isEmpty()) {
				return;
			}
		for (String key : target.keySet()) {
			idx.write(target.get(key).array());
		}
		}finally{
			if(idx != null && idx.getChannel().isOpen()){
				idx.getFD().sync();
				idx.close();
			}
		}
	}
	
	private void write(RandomAccessFile dataFile,DataPacket packet , String dbName){
		if(packet == null){
			return;
		}
		try {
			byte[] val = packet.getData();
				//TODO path参数为空？
				Location value = new Location(dataFile.length(),val.length , null);
				dataFile.write(val);
				
				//TODO 待优化
				String keyString = packet.getKeyString();
				if(!offset.containsKey(dbName)){
					offset.put(dbName, new ConcurrentHashMap<String, DataFile>());
				}
				saveOffset(keyString , value , offset.get(dbName));
				saveOffset(keyString , value , cache);
		} catch (FileNotFoundException e) {
			LOG.error("data file not found. dataFile = {} , packet = {}",dataFile,packet,e);
		}catch(IOException e){
			LOG.error("data file write data failed. dataFile = {} , packet = {}",dataFile,packet,e);
		}
	}
	
	private void reload(){
		LOG.info("starting reload index from disk...");
		File file = new File(storePath);
		if(!file.exists()){
			return;
		}
		File[] dbFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.isDirectory()){
					return true;
				}
				return false;
			}
		});
		
		Map<String,FutureTask<Long>> futures = new HashMap<String,FutureTask<Long>>(dbFiles.length);
		
		for(final File dbDir : dbFiles){
			FutureTask<Long> f = (FutureTask<Long>) reload.submit(new ReloadRunnable(dbDir));
			futures.put(dbDir.getName(), f);
		}
		
		//TODO 并发问题
		for(String db : futures.keySet()){
			Counter counter = current_db_size.get(db);
			try {
				if(counter != null){
					counter.clearAll();
					counter.getAndAdd(futures.get(db).get());
				}else{
					counter = new Counter(futures.get(db).get());
					current_db_size.put(db, counter);
				}
			} catch (Exception e) {
				LOG.error("reload thread fialed. db = {}",db,e);
			} 
		}
	}
	
	public Set<DBInfo> getDBInfos(){
		Set<DBInfo> dbinfos = new HashSet<DBInfo>();
		for(String db : current_db_size.keySet()){
			Counter counter = current_db_size.get(db);
			dbinfos.add(new DBInfo(db.getBytes(), counter.getAllSize(),0l));
		}
		return dbinfos;
	} 
	
	class ReloadRunnable implements Callable<Long>{
		
		String db;
		
		private Queue<File> pendingReloadDirs = new LinkedList<File>();
		
		public ReloadRunnable(File dbDir) {
			db = dbDir.getName();
			pendingReloadDirs.add(dbDir);
		}

		public Long call() {
			long allSize = 0;
			while(!pendingReloadDirs.isEmpty()){
				File file = pendingReloadDirs.poll();
				File[] metaFiles = file.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						if(pathname.isDirectory()){
							pendingReloadDirs.add(pathname);
							return false;
						}
						if(pathname != null && pathname.getName().endsWith(".meta")){
							return true;
						}
						return false;
					}
				});
				
				//TODO 根据meta文件的存储协议获取数据到内存,每个db下一个线程去加载，效率不高，待优化
				for(File metaFile : metaFiles){
					RandomAccessFile meta = null;
					try {
						if(metaFile.length() == 0){
							LOG.error("meta file is empty. maybe the file is error.{}",metaFile.getAbsolutePath());
							continue;
						}
						meta = new RandomAccessFile(metaFile, "rws");
						FileChannel channel = meta.getChannel();
						while(channel.position() < meta.length() - 1){
							ByteBuffer integer = ByteBuffer.allocate(4);
							channel.read(integer);
							integer.position(0);
							int keyLen = integer.getInt();
							ByteBuffer keyBuf = ByteBuffer.allocate(keyLen);
							byte[] key = new byte[keyLen];
							channel.read(keyBuf);
							keyBuf.position(0);
							keyBuf.get(key);

							String keyString = new String(key,Charsets.UTF_8);
							integer.flip();
							channel.read(integer);
							
							integer.position(0);
							int locationNum = integer.getInt();
							
							for(int i = 0 ; i < locationNum ; i++){
								ByteBuffer locationBuf = ByteBuffer.allocate(16);
								channel.read(locationBuf);
								locationBuf.position(0);
								long start = locationBuf.getLong();
								locationBuf.position(8);
								long offset = locationBuf.getLong();
								saveOffset(keyString, new Location(start, offset, metaFile.getAbsolutePath()), cache);
								allSize+=offset;
							}
						}
					} catch (FileNotFoundException e) {
						LOG.error(" reload meta file not found. metaFile = {}",metaFile.getAbsolutePath(),e);
					} catch (IOException e) {
						LOG.error(" reload meta file failed. metaFile = {}",metaFile.getAbsolutePath(),e);
					}finally{
						if(meta != null){
							try {
								meta.close();
							} catch (IOException e) {
							}
						}
					}
				}
			}
			LOG.info("DB {} （ {} bytes）reload over.",db,allSize);
			return allSize;
		}
	}
	
}
