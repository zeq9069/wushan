package com.sankuai.canyin.r.wushan.server.worker;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TaskInfo implements Serializable{
	
	private static final long serialVersionUID = -5115191310535777315L;

	private Task task;
	
	private DBHandleStatus status;
	
	private Map<String , Set<Db>> handleDb;//datanode ip与分发的数据库的映射
	
	private long timestamp;//创建时间
	
	public TaskInfo( Task task) {
		this(task, DBHandleStatus.WAITING, null);
	}
	
	public TaskInfo(Task task , DBHandleStatus status , Map<String, Set<Db>> handleDb) {
		this.task = task;
		this.status = status;
		this.handleDb = handleDb;
		this.timestamp = System.currentTimeMillis();
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	public DBHandleStatus getStatus() {
		return status;
	}

	public void setStatus(DBHandleStatus status) {
		this.status = status;
	}

	public Map<String, Set<Db>> getHandleDb() {
		return handleDb;
	}

	public void setHandleDb(Map<String, Set<Db>> handleDb) {
		this.handleDb = handleDb;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	enum DBHandleStatus{
		WAITING(1,"待处理"),PROCESSING(2,"处理中"),FINISHED(3,"处理完毕");
		
		int code;
		
		String msg;
		
		private DBHandleStatus(int code , String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}
	}
}
