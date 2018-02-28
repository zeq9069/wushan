package com.sankuai.canyin.r.wushan.service;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.sankuai.canyin.r.wushan.server.exception.ConnectionCloseExeception;
import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.server.worker.WorkerStatus;

import io.netty.channel.Channel;

/**
 * datanode 与 namenode通信的实现类
 * @author kyrin
 *
 */
public class DataNodeServiceImpl implements DataNodeService{

	private Channel channel;
	
	private ReentrantLock lock = new ReentrantLock();//多线程情况下，使用lock避免数据包被拆分
	
	public DataNodeServiceImpl(Channel channel){
		refreshCon(channel);
	}
	
	public void commitDBInfo(Set<DBInfo> dbInfos) throws ConnectionCloseExeception {
		checkConn();
		lock.lock();
		try{
			for(DBInfo info : dbInfos){
				channel.writeAndFlush(info);
			}
		}finally{
			lock.unlock();
		}
	}
	
	public void refreshCon(Channel channel){
		this.channel = channel;
	}
	
	private void checkConn() throws ConnectionCloseExeception{
		if(channel == null || !channel.isOpen()){
			throw new ConnectionCloseExeception("The connection of datanode and namenode closed.");
		}
	}

	public void uploadTask(Task task) throws ConnectionCloseExeception{
		checkConn();
		lock.lock();
		try{
			channel.writeAndFlush(task);
		}finally{
			lock.unlock();
		}
	}

	public void updateTaskStatus(WorkerStatus status) throws ConnectionCloseExeception {
		checkConn();
		lock.lock();
		try{
			channel.writeAndFlush(status);
		}finally{
			lock.unlock();
		}
	}
	
}
