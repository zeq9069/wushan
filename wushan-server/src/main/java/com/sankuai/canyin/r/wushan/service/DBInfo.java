package com.sankuai.canyin.r.wushan.service;

import java.util.Arrays;

/**
 * DB信息
 * @author kyrin
 *
 */
public class DBInfo {

	private byte[] db;
	
	private long allSize;
	
	private long keyCount;
	
	private long readCount;
	
	private long lastUpdatetimestamp;

	public DBInfo(byte[] db ){
		this(db, 0, 0, 0, 0);
	}
	
	public DBInfo(byte[] db , long allSize , long keyCount ){
		this(db, allSize, keyCount, 0, 0);
	}
	
	public DBInfo(byte[] db , long allSize , long keyCount , long readCount){
		this(db, allSize, keyCount, readCount, 0);
	}
	
	public DBInfo(byte[] db , long allSize , long keyCount , long readCount , long lastUpdatetimestamp){
		this.db = db;
		this.allSize = allSize;
		this.keyCount = keyCount;
		this.readCount = readCount;
		this.lastUpdatetimestamp = lastUpdatetimestamp;
	}
	
	public byte[] getDb() {
		return db;
	}

	public void setDb(byte[] db) {
		this.db = db;
	}

	public long getAllSize() {
		return allSize;
	}

	public void setAllSize(long allSize) {
		this.allSize = allSize;
	}

	public long getKeyCount() {
		return keyCount;
	}

	public void setKeyCount(long keyCount) {
		this.keyCount = keyCount;
	}

	public long getReadCount() {
		return readCount;
	}

	public void setReadCount(long readCount) {
		this.readCount = readCount;
	}

	public long getLastUpdatetimestamp() {
		return lastUpdatetimestamp;
	}

	public void setLastUpdatetimestamp(long lastUpdatetimestamp) {
		this.lastUpdatetimestamp = lastUpdatetimestamp;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(db);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBInfo other = (DBInfo) obj;
		if (!Arrays.equals(db, other.db))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DBInfo [db=" + Arrays.toString(db) + ", allSize=" + allSize + ", keyCount=" + keyCount + ", readCount="
				+ readCount + ", lastUpdatetimestamp=" + lastUpdatetimestamp + "]";
	}
	
}
