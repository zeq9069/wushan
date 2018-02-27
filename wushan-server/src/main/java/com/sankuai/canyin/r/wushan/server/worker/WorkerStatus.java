package com.sankuai.canyin.r.wushan.server.worker;

import java.util.Set;

/**
 * 工作状态
 * @author kyrin
 *
 */
public class WorkerStatus {
	
	String taskId;//任务ID
	
	Set<String> allDBs;//等待加载的DB
	
	Set<String> overDB;//加载完毕的DB
	
	boolean isOver;//是否运行完毕
	
	long runTimestamp;//运行时长，毫秒
	
	public WorkerStatus(String taskId , Set<String> allDBs , Set<String> overDB , boolean isOver , long runTimestamp) {
		this.taskId = taskId;
		this.allDBs = allDBs;
		this.overDB = overDB;
		this.isOver = isOver;
		this.runTimestamp = runTimestamp;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Set<String> getAllDBs() {
		return allDBs;
	}

	public void setAllDBs(Set<String> allDBs) {
		this.allDBs = allDBs;
	}

	public Set<String> getOverDB() {
		return overDB;
	}

	public void setOverDB(Set<String> overDB) {
		this.overDB = overDB;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public long getRunTimestamp() {
		return runTimestamp;
	}

	public void setRunTimestamp(long runTimestamp) {
		this.runTimestamp = runTimestamp;
	}

	@Override
	public String toString() {
		return "WorkerStatus [taskId=" + taskId + ", allDBs=" + allDBs + ", overDB=" + overDB
				 + ", isOver=" + isOver + ", runTimestamp=" + runTimestamp + "]";
	}
}
