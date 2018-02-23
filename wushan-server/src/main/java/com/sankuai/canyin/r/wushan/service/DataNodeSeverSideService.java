package com.sankuai.canyin.r.wushan.service;

import java.util.Set;

import com.sankuai.canyin.r.wushan.server.exception.ConnectionCloseExeception;
import com.sankuai.canyin.r.wushan.server.worker.Task;

public class DataNodeSeverSideService implements DataNodeService{

	public void commitDBInfo(Set<DBInfo> dbInfos) throws ConnectionCloseExeception {
		// TODO 
	}

	public void uploadBatchTask(Set<Task> tasks) throws ConnectionCloseExeception {
		// TODO
		
	}

	public void uploadTask(Task task) throws ConnectionCloseExeception {
		// TODO 
		
	}
}
