package com.sankuai.canyin.r.wushan.server.worker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.exception.TaskNotNullException;
import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.worker.TaskInfo.DBHandleStatus;

import io.netty.channel.Channel;
import javassist.NotFoundException;

/**
 * 负责管理client发送过来的Task（分发、持久化、状态同步等）
 * 
 * @author kyrin
 *
 */
public class TaskManager {

	private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

	private Map<String, TaskInfo> tasks = new ConcurrentHashMap<String, TaskInfo>();// Taskid 与 taskInfo的映射

	private TaskSchedule taskSchedule = new TaskSchedule();
	
	private ReentrantLock lock = new ReentrantLock();

	public TaskManager() {
	}

	public void upload(Task task) {
		if (task == null || task.getExpression() == null || task.getDbs() == null || task.getDbs().isEmpty()) {
			throw new TaskNotNullException("Task can't be null.");
		}
		String taskId = TaskIdUtils.generateTaskId();
		task.setId(taskId);
		TaskInfo taskInfo = new TaskInfo(task);
		lock.lock();
		try {
			Map<String, Set<Db>> assignDb = taskSchedule.assign(task);
			taskInfo.setHandleDb(assignDb);
			tasks.put(taskId, taskInfo);
			for (String key : assignDb.keySet()) {
				Channel channel = ClientInfosManager.getRpcClientConn(key);
				if(channel == null){
					LOG.error("{} 已经断开! ",key);
					continue;
				}
				Set<String> dbnames = new HashSet<String>();
				for (Db db : assignDb.get(key)) {
					dbnames.add(db.getDb());
				}
				channel.writeAndFlush(new Task(taskId , task.getExpression(), dbnames, task.getParams()));
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void updateTaskStatus(String ip , int port , WorkerStatus status) throws NotFoundException{
		TaskInfo taskInfo = tasks.get(status.getTaskId());
		if(taskInfo == null){
			throw new NotFoundException("Namenode not found Task.taskId = "+status.getTaskId());
		}
		taskInfo.setStatus(status.isOver ? DBHandleStatus.FINISHED : DBHandleStatus.PROCESSING);
		Map<String, Set<Db>> handleDB = taskInfo.getHandleDb();
		Set<Db> sets = handleDB.get(ip+":"+port);
		for(Db db : sets){
			if(status.getOverDB().contains(db.getDb())){
				db.setLastTimestamp(System.currentTimeMillis());
				db.setStatus(DBHandleStatus.FINISHED);
			}
		}
	}
}
