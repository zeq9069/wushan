package com.snakuai.canyin.r.wushan.client.message;

import java.io.Serializable;

import io.netty.util.CharsetUtil;

/**
 * 
 * @author kyrin
 *
 */
public class DataPacket implements Serializable{
	
	private static final long serialVersionUID = 8811136895127093013L;

	byte[] db;
	
	byte[] key;
	
	byte[] data;
	

	public DataPacket(byte[] db , byte[] key , byte[] data) {
		this.key = key;
		this.data = data;
		this.db = db;
	}
	
	public DataPacket(byte[] key , byte[] data) {
		this(null, key, data);
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] getData() {
		return data;
	}
	
	public byte[] getDb(){
		return db;
	}
	
	public String getDbString(){
		return new String(db,CharsetUtil.UTF_8);
	}

}
