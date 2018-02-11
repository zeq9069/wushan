package com.sankuai.canyin.r.wushan.server.datanode;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.datanode.exception.ConnectionCloseExeception;
import com.sankuai.canyin.r.wushan.service.DBInfo;
import com.sankuai.canyin.r.wushan.service.DataNodeProtocol;
import com.sankuai.canyin.r.wushan.service.DataNodeProtocolImpl;

public class DataNodeClientSideService implements DataNodeProtocol{

	private static final Logger LOG = LoggerFactory.getLogger(DataNodeClientSideService.class);
	
	private DataNodeProtocolImpl impl;
	
	public DataNodeClientSideService(DataNodeProtocolImpl impl) {
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
