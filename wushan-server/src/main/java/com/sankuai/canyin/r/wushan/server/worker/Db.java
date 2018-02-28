package com.sankuai.canyin.r.wushan.server.worker;

import com.sankuai.canyin.r.wushan.server.worker.TaskInfo.DBHandleStatus;

//TODO 记录已经处理的数据文件和待处理的数据文件，这样当系统宕机重启之后可以接着上次的继续运行
public class Db {

	String db;

	DBHandleStatus status = DBHandleStatus.WAITING; // 处理中，处理完毕，等待处理

	private long lastTimestamp;// 最后更新时间

	public Db(String db, DBHandleStatus status) {
		this.db = db;
		this.status = status;
		this.lastTimestamp = System.currentTimeMillis();
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public DBHandleStatus getStatus() {
		return status;
	}

	public void setStatus(DBHandleStatus status) {
		this.status = status;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}

	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
}