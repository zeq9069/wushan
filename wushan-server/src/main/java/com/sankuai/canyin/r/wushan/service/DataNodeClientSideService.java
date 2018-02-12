package com.sankuai.canyin.r.wushan.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.datanode.exception.ConnectionCloseExeception;

public class DataNodeClientSideService implements DataNodeService{

	private static final Logger LOG = LoggerFactory.getLogger(DataNodeClientSideService.class);
	
	private DataNodeServiceImpl impl;
	
	public DataNodeClientSideService(DataNodeServiceImpl impl) {
		this.impl = impl;
	}
	
	public void commitDBInfo(Set<DBInfo> dbInfos) {
		try {
			impl.commitDBInfo(dbInfos);
		} catch (ConnectionCloseExeception e) {
			LOG.error("commit DB info to namenode failed.",e);
		}
	}

}
