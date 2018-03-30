package com.sankuai.canyin.r.wushan.server.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.server.datanode.store.LoadDBDataService;
import com.sankuai.canyin.r.wushan.thread.WushanThreadFactory;

/**
 * 
 * Task 在dn中运行的独立的进程，负责Task的运行和状态的上报（上报到DN）
 * 
 * 优化：
 * 	TaskRunner线程池需要优化，对容量限制会出问题，不限制的话，堆内存经常打满，当然可以执行下去，当但是影响性能
 * 
 * @author kyrin
 *
 */
public class Worker implements Service{
	
	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
	
	private Task targetTask;//等待运行的task
	
	private int port;
	
	private long startTimestamp;
	
	private long endTimestamp;
	
	private LoadDBDataService loadDBDataService;
	
	private volatile BlockingQueue<String> queue = new LinkedBlockingQueue<String>(128000);
	
	private String storePath;
	
	private WorkerSyncStatusService workerSyncStatusService;
	
	private ExecutorService runner = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new WushanThreadFactory("Task-runner"));
	
	private volatile boolean isOver = false;
	
	private Thread start;
	
	public Worker(Task task , int port , String storePath) {
		this.targetTask = task;
		this.port = port;
		this.storePath = storePath;
		startTimestamp = System.currentTimeMillis();
		init();
	}
	
	public void init(){
		loadDBDataService = new LoadDBDataService(storePath, targetTask.getDbs(), queue);
		workerSyncStatusService = new WorkerSyncStatusService("localhost", port , this);
	}
	
	public void start(){
		loadDBDataService.load();
		workerSyncStatusService.start();
		start = new Thread(new Runnable() {
			public void run() {
				process();//处理加载的数据
			}
		},"PROCESS-THREAD");
		start.start();
	}
	
	public void process(){
		LOG.info("start process data...");
		isOver = false;
		long count = 0;
		while(!loadDBDataService.isOver() || !queue.isEmpty()){
			List<Task> tasks = new ArrayList<Task>();
			for(int i = 0 ; i < 1000 && !queue.isEmpty() ;i++ ){
				String params = "{}";
				try {
					params = queue.poll(100,TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					LOG.error("BlockingQueue poll element failed.",e);
				}
				Map<String,Object> context = JSON.parseObject(params, HashMap.class);
				context.putAll(targetTask.getParams());
				Task task = new Task(targetTask.getId() , targetTask.getExpression(), targetTask.getDbs(),context);
				tasks.add(task);
				count++;
			}
			if(!tasks.isEmpty()){
				runner.execute(new TaskRunner(tasks));
			}
			LOG.info("queue size ： "+queue.size()+" , isOver : "+loadDBDataService.isOver()+" , 处理数量：count = "+count);
		}
		LOG.info("任务结束,剩余数量："+queue.size()+"\n");
		isOver = true;
		endTimestamp = System.currentTimeMillis();
		LOG.info("Task runner over! {}",targetTask);
	}
	
	public WorkerStatus getStatus(){
		WorkerStatus status = new WorkerStatus(targetTask.getId(), loadDBDataService.getDbs(),
				loadDBDataService.getAlreadyLoadedDbs() , isOver, startTimestamp);
		return status;
	}
	
	public static void main(String[] args) {
		if(args == null || args.length < 5){
			System.exit(-1);
		}
		String port = args[0];
		String storePath = args[1];
		String id = args[2];
		String expression = args[3];
		String dbs = args[4];
		String params = null;
		if(args.length == 6){
			params = args[5];
		}
		Task targetTask = new Task(id,expression, Sets.newHashSet(dbs.split(",")), params == null?null:JSON.parseObject(params, HashMap.class));
		Worker worker = new Worker(targetTask , Integer.parseInt(port) , storePath);
		worker.start();
	}

	public void destroy() {
		System.exit(0);
//		if(start != null && start.isAlive()){
//			try {
//				start.join(10);
//			} catch (InterruptedException e) {
//			}
//		}
//		loadDBDataService.shutdown();
//		if(!runner.isShutdown()){
//			runner.shutdownNow();
//		}
//		workerSyncStatusService.destroy();
	}
	
}
