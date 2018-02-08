package com.sankuai.canyin.r.wushan.server.namenode.dispatcher;

import com.sankuai.canyin.r.wushan.server.namenode.DataInfo;

/**
 * 分发策略接口
 * @author kyrin
 *
 */
public interface Strategy {
	
	public DataInfo choose(Object target);

}
