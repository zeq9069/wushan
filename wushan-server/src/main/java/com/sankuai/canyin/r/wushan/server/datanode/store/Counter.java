package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
	
	private AtomicLong value ;
	
	private long allSize = 0;
	
	private long lastTimestamp; //毫秒
	
	public Counter(long value) {
		this.value = new AtomicLong(value);
		allSize += value;
		updateLastUpdateTimestamp();
	}
	
	public long getValue() {
		return value.get();
	}

	public void getAndAdd(long delta) {
		this.value.getAndAdd(delta);
		allSize+=delta;
		updateLastUpdateTimestamp();
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}
	
	public long getAllSize(){
		return allSize;
	}

	public void clear(){
		this.value = new AtomicLong(0);
		updateLastUpdateTimestamp();
	}
	
	public void clearAll(){
		this.value = new AtomicLong(0);
		this.allSize = 0;
		updateLastUpdateTimestamp();
	}
	
	private void updateLastUpdateTimestamp(){
		this.lastTimestamp = System.currentTimeMillis();
	}
}
