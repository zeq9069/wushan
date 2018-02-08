package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.io.RandomAccessFile;

public class FileDesc {
	
	private RandomAccessFile data;
	
	private RandomAccessFile index;

	public FileDesc(RandomAccessFile data, RandomAccessFile index) {
		this.data = data;
		this.index = index;
	}

	public RandomAccessFile getData() {
		return data;
	}

	public RandomAccessFile getIndex() {
		return index;
	}
}
