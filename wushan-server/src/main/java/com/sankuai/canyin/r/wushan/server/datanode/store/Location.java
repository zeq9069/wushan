package com.sankuai.canyin.r.wushan.server.datanode.store;

//TODO 待优化改造
public class Location {
	
	private String path;
	
	private long start;
	
	private long offset;
	
	public Location(long start , long offset , String path) {
		this.start = start;
		this.offset = offset;
		this.path = path;
	}
	
	public long getStart() {
		return start;
	}

	public long getOffset() {
		return offset;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Location [path=" + path + ", start=" + start + ", offset=" + offset + "]";
	}
}
