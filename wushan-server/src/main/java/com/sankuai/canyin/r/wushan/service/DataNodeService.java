package com.sankuai.canyin.r.wushan.service;

import java.util.Set;

import com.sankuai.canyin.r.wushan.server.exception.ConnectionCloseExeception;
import com.sankuai.canyin.r.wushan.server.worker.Task;

public interface DataNodeService {
	
	public void commitDBInfo(Set<DBInfo> dbInfos) throws ConnectionCloseExeception;
	
	public void uploadTask(Task task) throws ConnectionCloseExeception;
}
