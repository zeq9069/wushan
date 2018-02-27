package com.sankuai.canyin.r.wushan.server.datanode.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sankuai.canyin.r.wushan.server.exception.TaskExsitException;
import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.server.worker.Worker;

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
		ProcessBuilder proc = new ProcessBuilder("nohup","java","-cp",".:"+user_dir+"/lib/*",Worker.class.getName(), port+"" , storePath , task.getId() ,task.getExpression() , StringUtils.join(task.getDbs(),",") , JSON.toJSONString(task.getParams()));
		try {
			proc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		ProcessBuilder proc = new ProcessBuilder("nohup","java","-cp",".:/Users/kyrin/workspace/learningworkspace/wushan/wushan-server/target/app/lib/*",Worker.class.getName(),"8416", "/tmp/wushan-data", "123456789", "wwww==3 && eeee == 6" , "1-0-null-7","{\"wwww\":3,\"eeee\":6}");
		try {
			proc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
