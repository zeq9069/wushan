package com.sankuai.canyin.r.wushan.server.worker;

import java.util.UUID;

/**
 * task id生成工具
 * 
 * TODO 目前ID没有顺序，后期改用Snowflake算法
 * @author kyrin
 *
 */
public class TaskIdUtils {
	
	public static String generateTaskId(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
