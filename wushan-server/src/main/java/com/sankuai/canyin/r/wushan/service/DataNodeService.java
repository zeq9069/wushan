package com.sankuai.canyin.r.wushan.service;

import java.util.Set;

import com.sankuai.canyin.r.wushan.server.datanode.exception.ConnectionCloseExeception;

public interface DataNodeService {
	
	public void commitDBInfo(Set<DBInfo> dbInfos) throws ConnectionCloseExeception;
	
}