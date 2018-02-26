package com.sankuai.canyin.r.wushan.server.worker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.meituan.canyin.risk.virbius.expression.ExpressionManager;
import com.sankuai.meituan.canyin.risk.virbius.expression.ExpressionResult;
import com.sankuai.meituan.canyin.risk.virbius.expression.groovy.GroovyExpressionManager;

public class TaskRunner implements Runnable{

	private static final Logger LOG = LoggerFactory.getLogger(TaskRunner.class);
	
	private List<Task> tasks;
	
	public TaskRunner(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public TaskRunner(Task task) {
		this(new ArrayList<Task>());
		tasks.add(task);
	}
	
	public void run() {
		ExpressionManager manager = new GroovyExpressionManager();
		for(Task task : tasks){
			ExpressionResult<Boolean> result = manager.executeBoolResultExpression(task.getExpression(),task.getParams());
		}
		LOG.info("执行完毕！{}",tasks.size());
	}
}
