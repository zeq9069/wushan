package com.sankuai.canyin.r.wushan.server.datanode.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sankuai.canyin.r.wushan.server.exception.TaskExsitException;
import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.server.worker.Worker;
import com.sankuai.canyin.r.wushan.server.worker.WorkerStatus;

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

	private Map<String,Channel> IP2conns = new ConcurrentHashMap<String, Channel>();//IP:port 与 channel 的映射
	
	private Map<String,String> taskId2IP = new ConcurrentHashMap<String, String>();//
	
	private Map<String,WorkerStatus> taskId2WorkerStatus = new ConcurrentHashMap<String, WorkerStatus>();
	
	private int port;//worker进程与dn通信的端口
	
	private String storePath;//dn的存储目录
	
	public WorkerManager(int port , String storePath) {
		this.port = port;
		this.storePath = storePath;
	}
	
	public void run(Task task){
		if(workers.containsKey(task.getId())){
			throw new TaskExsitException("The task already exists. taskId = "+task.getId());
		}
		workers.put(task.getId(), task);
		String user_dir = System.getProperty("user.dir");
		ProcessBuilder proc = new ProcessBuilder(buildCommand(user_dir , task));
		try {
			proc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> buildCommand(String user_dir , Task task){
		String gc_args = "-Xms1G -Xmx1G -XX:PermSize=128m -XX:MaxPermSize=256m"
				+ " -XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=512M -XX:+UseG1GC -XX:SurvivorRatio=8 -XX:NewRatio=3 -XX:MaxGCPauseMillis=9"
				+ " -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC"
				+ " -XX:+PrintTenuringDistribution -XX:+PrintGCCause -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintCommandLineFlags";
		String[] array = gc_args.split(" ");
		List<String> command = new ArrayList<String>();
		command.add("nohup");
		command.add("java");
		for(String s : gc_args.split(" ")){
			command.add(s);
		}
		command.add("-cp");
		command.add(".:"+user_dir+"/lib/*:"+user_dir+"/conf/*");
		command.add(Worker.class.getName());
		command.add(""+port);
		command.add(storePath);
		command.add(task.getId());
		command.add(task.getExpression());
		command.add(StringUtils.join(task.getDbs(),","));
		command.add(JSON.toJSONString(task.getParams()));
		return command;
	}
	
	public void updateStatus(WorkerStatus status){
		taskId2WorkerStatus.put(status.getTaskId(), status);
	}
	
	public void registChannel(String ip , int port , Channel channel){
		String key = ip+":"+port;
		if(IP2conns.containsKey(key)){
			return;
		}
		IP2conns.put(key,channel);
	}
	
	public void unregistChannel(String ip , int port , Channel channel){
		String key = ip+":"+port;
		if(IP2conns.containsKey(key)){
			IP2conns.remove(key);
			for(Iterator<String> it = taskId2IP.keySet().iterator();it.hasNext();){
				String kk = it.next();
				if(key.equals(taskId2IP.get(kk))){
					taskId2IP.remove(kk);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String gc_args = "-Xms1G -Xmx1G -XX:PermSize=128m -XX:MaxPermSize=256m"
				+ " -XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=512M -XX:+UseG1GC -XX:SurvivorRatio=8 -XX:NewRatio=3 -XX:MaxGCPauseMillis=9"
				+ " -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC"
				+ " -XX:+PrintTenuringDistribution -XX:+PrintGCCause -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintCommandLineFlags";
		List<String> command = new ArrayList<String>();
		command.add("nohup");
		command.add("java");
		for(String s : gc_args.split(" ")){
			command.add(s);
		}
		command.add("-cp");
		command.add(".:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/lib/*");
		command.add(Worker.class.getName());
		command.add("8416");
		command.add("/tmp/wushan-data");
		command.add("123456789");
		command.add("wwww==3 && eeee == 6");
		command.add("1-0-null-7");
		command.add("{\"wwww\":3,\"eeee\":6}");

		ProcessBuilder proc = new ProcessBuilder(command);
		try {
			proc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
