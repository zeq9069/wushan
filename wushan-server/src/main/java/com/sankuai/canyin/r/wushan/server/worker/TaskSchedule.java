package com.sankuai.canyin.r.wushan.server.worker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.worker.TaskInfo.DBHandleStatus;
import com.sankuai.canyin.r.wushan.service.DBInfo;

public class TaskSchedule {
	
	public Map<String , Set<Db>> assign(Task task){
		Map<String,Set<Db>> assignDB = new ConcurrentHashMap<String, Set<Db>>();
		 Map<String, Set<DBInfo>> dbInfos = ClientInfosManager.getDbinfos();
		 for(String key : dbInfos.keySet()){
			 for(DBInfo dbInfo : dbInfos.get(key)){
				 String dbString = new String(dbInfo.getDb(),Charsets.UTF_8);
				 if(task.getDbs().contains(dbString)){
					 Set<Db> dbSets = assignDB.get(key);
					 Db db = new Db(dbString,DBHandleStatus.WAITING);
					 if(dbSets == null){
						 dbSets = new HashSet<Db>();
						 assignDB.put(key, dbSets);
					 }
					 dbSets.add(db);
				 }
			 }
		 }
		 return assignDB;
	}
	
}
