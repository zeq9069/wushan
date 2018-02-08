package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.ArrayList;
import java.util.List;

import com.sankuai.canyin.r.wushan.server.utils.ByteUtils;

import io.netty.util.CharsetUtil;

public class DataFile extends Node{
	
	private String key;
	
	List<Location> offsets = new ArrayList<Location>();
	
	public DataFile(String key ) {
		super(null);
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Location> getOffsets() {
		return offsets;
	}

	public void setOffsets(List<Location> offsets) {
		this.offsets = offsets;
	}
	
	public byte[] array(){
		byte[] array = new byte[4 + key.getBytes(CharsetUtil.UTF_8).length + 4 + 2 * 8 * offsets.size() ];
		ByteUtils.putInt(array, key.getBytes(CharsetUtil.UTF_8).length , 0);
		System.arraycopy(key.getBytes(CharsetUtil.UTF_8), 0, array , 4 ,  key.getBytes(CharsetUtil.UTF_8).length );
		ByteUtils.putInt(array, offsets.size() , 4 + key.getBytes(CharsetUtil.UTF_8).length);
		for(int i = 0 ; i < offsets.size() ; i++){
			ByteUtils.putLong(array, offsets.get(i).getStart(), 4 + key.getBytes(CharsetUtil.UTF_8).length + 4 + 16 * i);
			ByteUtils.putLong(array, offsets.get(i).getOffset(), 4 + key.getBytes(CharsetUtil.UTF_8).length + 4 + 16 * i + 8);
		}
		return array;
	}
	
	public static void main(String[] args) {
		DataFile data = new DataFile("18310700192");
		data.getOffsets().add(new Location(1, 8, "/dev/null"));
		data.getOffsets().add(new Location(9, 11, "/dev/null"));
		byte[] array = data.array();
		System.out.println(array.length);
		
	}
	
}
