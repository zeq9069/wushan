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
import com.sankuai.canyin.r.wushan.server.utils.StreamUtils;
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
	
	public void run(final Task task){
		if(workers.containsKey(task.getId())){
			throw new TaskExsitException("The task already exists. taskId = "+task.getId());
		}
		workers.put(task.getId(), task);
		final String user_dir = System.getProperty("user.dir");

		//TODO woker 进程被杀死，线程结束，可以提取一层对worker的包裹
		new Thread(new Runnable() {
			
			public void run() {
				ProcessBuilder proc = new ProcessBuilder(buildCommand(user_dir , task));
				try {
					proc.redirectErrorStream(true);//ERROR AND input输出合并
					Process pro = proc.start();
					LOG.info("Worker output : "+StreamUtils.getOut(pro.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	private List<String> buildCommand(String user_dir , Task task){
		String gc_args = "-Xms2G -Xmx2G -XX:PermSize=256m -XX:MaxPermSize=256m"
				+ " -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=3 -XX:SurvivorRatio=8 -XX:NewRatio=3 -XX:MaxGCPauseMillis=9"
				+ " -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=72 -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC"
				+ " -XX:+PrintTenuringDistribution -XX:+PrintGCCause -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintCommandLineFlags ";
		List<String> command = new ArrayList<String>();
		command.add("nohup");
		command.add("java");
		for(String s : gc_args.split(" ")){
			if(!"".equals(s.trim())){
				command.add(s);
			}
		}
		command.add("-cp");
		command.add(".:"+user_dir+"/lib/*:"+user_dir+"/conf/*");
		//command.add(".:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/lib/*:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/conf/*");
		command.add(Worker.class.getName());
		command.add(""+port);
		command.add(storePath);
		command.add(task.getId());
		command.add(task.getExpression());
		command.add(StringUtils.join(task.getDbs(),","));
		command.add(JSON.toJSONString(task.getParams()));
		//command.add(" & ");
		return command;
	}
	
	public void updateStatus(String ip , int port ,WorkerStatus status){
		taskId2WorkerStatus.put(status.getTaskId(), status);
		taskId2IP.put(status.getTaskId()  , ip+":"+port);
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
	
	public Channel getWorkerChannelByTaskId(String taskId){
		String IPAndPort = taskId2IP.get(taskId);
		if(IPAndPort == null){
			return null;
		}
		Channel channel = IP2conns.get(IPAndPort);
		if(channel != null){
			return channel;
		}
		return null;
	}
	
	public static void main(String[] args) {
		String gc_args = "-Xms1G -Xmx1G "
				+ " -XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=512M -XX:+UseG1GC -XX:SurvivorRatio=8 -XX:NewRatio=3 -XX:MaxGCPauseMillis=9"
				+ " -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC"
				+ " -XX:+PrintTenuringDistribution -XX:+PrintGCCause -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintCommandLineFlags";
		List<String> command = new ArrayList<String>();
		command.add("nohup");
		command.add("java");
		command.add("-server");
		for(String s : gc_args.split(" ")){
			if(!s.trim().equals("")){
				command.add(s);
			}
		}
		command.add("-cp");
		command.add(".:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/lib/*:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/conf/*");
		command.add(Worker.class.getName());
		command.add("8416");
		command.add("/tmp/wushan-data");
		command.add("123456789");
		command.add("wwww==3 && eeee == 6");
		command.add("1-0-null-7");
		command.add("{\"wwww\":3,\"eeee\":6}");

		ProcessBuilder proc = new ProcessBuilder(command);
		try {
			Process pro = proc.start();
			LOG.info("Worker input : "+StreamUtils.getOut(pro.getInputStream()));
			LOG.error("Worker ERROR : "+StreamUtils.getOut(pro.getErrorStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
