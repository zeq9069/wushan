package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.thread.WushanThreadFactory;

/**
 * 加载对应的db数据
 * 
 * TODO
 * 1.优化：生产者消费者模式，降低生产者的速度，使其和消费速度匹配， 避免速度过快，打满堆内存
 * 			采用阻塞队列进行优化
 * 
 * @author kyrin
 *
 */
public class LoadDBDataService {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoadDBDataService.class);
	
	private BlockingQueue<String> queue ;//从db加载的数据，一直被消费
	
	private volatile Set<String> dbs = new ConcurrentSkipListSet<String>();
	
	private volatile Set<String> alreadyLoadedDbs = new ConcurrentSkipListSet<String>();
	
	private String storePath;
	
	private volatile boolean isOver = false;
	
	private ExecutorService load = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			new WushanThreadFactory("worker-load-db"));
	
	public LoadDBDataService(String storePath , Set<String> dbs , BlockingQueue<String> queue) {
		this.storePath = storePath;
		if(dbs == null){
			throw new IllegalArgumentException("DB is NULL.");
		}
		this.dbs.addAll(dbs);
		this.queue = queue;
	}
	
	public synchronized void load(){
		if(dbs.isEmpty() || isOver){
			return;
		}
		
		final CountDownLatch latch = new CountDownLatch(dbs.size());
		List<Future<Long>> futures = new ArrayList<Future<Long>>(dbs.size());
		for(String db : dbs){
			Future<Long> task = load.submit(new LoadData(db , storePath , latch));
			futures.add(task);
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					latch.await();
					isOver = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} , "MONITOR").start();
	}
	
	public boolean isOver(){
		return isOver;
	}
	
	class LoadData implements Callable<Long>{
		
		private String db;
		
		private Queue<File> pendingReloadDirs = new LinkedList<File>();
		
		CountDownLatch latch;
		
		public LoadData(String db , String storePath , CountDownLatch latch) {
			this.db = db;
			pendingReloadDirs.add(new File(storePath+"/"+db));
			this.latch = latch;
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
					RandomAccessFile dataFile = null;
					try {
						if(metaFile.length() == 0){
							LOG.error("meta file is empty. maybe the file is error.{}",metaFile.getAbsolutePath());
							continue;
						}
						Set<Location> locations = new TreeSet<Location>(new Comparator<Location>() {
							public int compare(Location o1, Location o2) {
								return (int) (o1.getStart() - o2.getStart());
							}
						});
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
								locations.add(new Location(start, offset, metaFile.getAbsolutePath()));
								allSize+=offset;
							}
						}
						
						//TODO 加载数据
						String name = metaFile.getName().substring(0, metaFile.getName().lastIndexOf("."));
						String parentPath = metaFile.getParent();
						dataFile = new RandomAccessFile(new File(parentPath+"/"+name+".data"), "rws");
						FileChannel dataChannel = dataFile.getChannel();
						for(Location location : locations){
							ByteBuffer buffer = ByteBuffer.allocate((int)location.getOffset());
							dataChannel.read(buffer, (int)location.getStart());
							try {
								queue.put(new String(buffer.array(),Charset.forName("UTF-8")));
							} catch (InterruptedException e) {
								LOG.error("BlockingQueue put element failed.",e);
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
						if(dataFile != null){
							try {
								dataFile.close();
							} catch (IOException e) {
							}
						}
					}
				}
			}
			//移除db
			alreadyLoadedDbs.add(db);
			dbs.remove(db);
			LOG.info("DB {} （ {} bytes）reload over.",db,allSize);
			latch.countDown();
			return allSize;
		}
	}
	
	public Set<String> getAlreadyLoadedDbs(){
		return alreadyLoadedDbs;
	}
	
	public Set<String> getDbs(){
		return dbs;
	}
	
	public void shutdown(){
		if(load != null && !load.isShutdown()){
			load.shutdownNow();
		}
	}
}
