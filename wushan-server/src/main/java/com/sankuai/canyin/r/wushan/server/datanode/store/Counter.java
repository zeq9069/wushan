package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	
	private AtomicInteger value;
	
	private long lastTimestamp; //毫秒
	
	
	public Counter(int value) {
		this.value = new AtomicInteger(value);
		this.lastTimestamp = System.currentTimeMillis();
	}
	
	public int getValue() {
		return value.get();
	}

	public void getAndAdd(int delta) {
		this.value.getAndAdd(delta);
		this.lastTimestamp = System.currentTimeMillis();
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}
}
