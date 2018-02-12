package com.sankuai.canyin.r.wushan.server.message;

import java.io.Serializable;

import com.google.common.base.Charsets;


/**
 * 
 * @author kyrin
 *
 */
public class DataPacket implements Serializable{
	
	private static final long serialVersionUID = -8168592822451034102L;

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

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getDb(){
		return db;
	}
	
	public String getDbString() {
		return new String(db,Charsets.UTF_8);
	}

	public String getKeyString(){
		return new String(key,Charsets.UTF_8);
	}

	@Override
	public String toString() {
		return "DataPacket [db=" + new String(db,Charsets.UTF_8) + ", key=" + new String(key,Charsets.UTF_8)  + ", data="
				+ new String(data,Charsets.UTF_8) + "]";
	}
	
}
