package com.sankuai.canyin.r.wushan.server.datanode.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.exception.TaskExsitException;
import com.sankuai.canyin.r.wushan.server.worker.Task;

import io.netty.channel.Channel;

/**
 * dn侧，负责处理和worker交互和信息的保存
 * 
 * TODO 目前是每一个Task分到对应的dn之后，仅仅是一个worker，后期是否可以优化
 * 
 * @author kyrin
 *
 */
public class WorkerManager {

	private static final Logger LOG = LoggerFactory.getLogger(WorkerManager.class);
	
	private Map<String,Task> workers = new ConcurrentHashMap<String, Task>();//任务id和任务的映射
	
	private Map<Integer,String> processToTask = new ConcurrentHashMap<Integer, String>();//进程id与任务id的映射
	
	private Map<String , Integer> ipToProcess = new ConcurrentHashMap<String, Integer>();//ip+port与进程id的映射
	
	private Map<Integer,Channel> conns = new ConcurrentHashMap<Integer, Channel>();//进程和进程连接的映射
	
	
	public WorkerManager() {
	}
	
	public void run(Task task){
		if(workers.containsKey(task.getId())){
			throw new TaskExsitException("The task already exists. taskId = "+task.getId());
		}
		workers.put(task.getId(), task);
		
		
		
		
	}
	
	
}
