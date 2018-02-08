package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.List;

public interface Storage {
	

	public void put(String key , String value);
	
	public List<String> find(String key);
	

}
