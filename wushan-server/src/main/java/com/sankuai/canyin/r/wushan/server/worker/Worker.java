package com.sankuai.canyin.r.wushan.server.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.sankuai.canyin.r.wushan.server.datanode.store.LoadDBDataService;
import com.sankuai.canyin.r.wushan.thread.WushanThreadFactory;

/**
 * 
 * Task 在dn中运行的独立的进程，负责Task的运行和状态的上报（上报到DN）
 * 
 * @author kyrin
 *
 */
public class Worker {
	
	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
	
	private Task targetTask;//等待运行的task
	
	private int port;
	
	private long startTimestamp;
	
	private LoadDBDataService loadDBDataService;
	
	private volatile Queue<String> queue = new ConcurrentLinkedQueue<String>();
	
	private String storePath;
	
	private WorkerSyncStatusService workerSyncStatusService;
	
	private ExecutorService runner = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()
			,new WushanThreadFactory("Task-runner"));
	
	public Worker(Task task , int port , String storePath) {
		this.targetTask = task;
		this.port = port;
		this.storePath = storePath;
		startTimestamp = System.currentTimeMillis();
		init();
	}
	
	public void init(){
		loadDBDataService = new LoadDBDataService(storePath, targetTask.getDbs(), queue);
		workerSyncStatusService = new WorkerSyncStatusService("loalhost", port);
	}
	
	public void run(){
		loadDBDataService.load();
		workerSyncStatusService.start();
		process();
	}
	
	public void process(){
		while(!loadDBDataService.isOver() || !queue.isEmpty()){
			List<Task> tasks = new ArrayList<Task>();
			for(int i = 0 ; i < 100 && !queue.isEmpty() ;i++ ){
				String params = queue.poll();
				Map<String,Object> context = JSON.parseObject(params, HashMap.class);
				context.putAll(targetTask.getParams());
				Task task = new Task(targetTask.getId() , targetTask.getExpression(), targetTask.getDbs(),context);
				tasks.add(task);
			}
			runner.execute(new TaskRunner(tasks));
			LOG.info("Queue 剩余数量 : "+queue.size());
		}
		LOG.info("Task runner over! {}",targetTask);
	}
	
	public static void main(String[] args) {
		if(args == null || args.length < 5){
			System.exit(0);
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
		worker.run();
	}
}
