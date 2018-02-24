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

import io.netty.channel.Channel;

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
		tasks.put(taskId, taskInfo);
		lock.lock();
		try {
			Map<String, Set<Db>> assignDb = taskSchedule.assign(task);
			taskInfo.setHandleDb(assignDb);
			for (String key : assignDb.keySet()) {
				Channel channel = ClientInfosManager.getRpcClientConn(key);
				Set<String> dbnames = new HashSet<String>();
				for (Db db : assignDb.get(key)) {
					dbnames.add(db.getDb());
				}
				channel.writeAndFlush(new Task(task.getExpression(), dbnames, task.getParams()));
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void updateTaskStatus(){
		
	}

}
