package com.sankuai.canyin.r.wushan.server.namenode.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.namenode.DataInfo;

/**
 * 默认使用hash
 * @author kyrin
 *
 */
public class DefaultDispatcherStrategy implements Strategy{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultDispatcherStrategy.class);
	
	public DataInfo choose(Object target) {
		DataInfo[] dataInfoArray = ClientInfosManager.toArrayForTransferClient();
		int hashcode = target == null ? 0:target.hashCode();
		int mod = hashcode%dataInfoArray.length;
		return dataInfoArray[mod];
	}
}
