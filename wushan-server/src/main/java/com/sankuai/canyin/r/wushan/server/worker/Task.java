package com.sankuai.canyin.r.wushan.server.worker;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Task implements Serializable{
	
	private static final long serialVersionUID = -979240591963014249L;

	private String expression;
	
	private Set<String> dbs;
	
	private Map<String,Object> params;
	
	public Task(String expression , Set<String> dbs , Map<String,Object> params) {
		this.expression = expression;
		this.dbs = dbs;
		this.params = params;
	}

	public String getExpression() {
		return expression;
	}

	public Set<String> getDbs() {
		return dbs;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "Task [expression=" + expression + ", dbs=" + dbs + ", params=" + params + "]";
	}
}
