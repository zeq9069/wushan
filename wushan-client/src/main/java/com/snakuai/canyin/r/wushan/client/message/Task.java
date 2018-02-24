package com.snakuai.canyin.r.wushan.client.message;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Task implements Serializable{
	
	private static final long serialVersionUID = -979240591963014249L;

	private String id;
	
	private String expression;
	
	private Set<String> dbs;
	
	private Map<String,Object> params;
	
	public Task(String id , String expression , Set<String> dbs , Map<String,Object> params) {
		this.expression = expression;
		this.dbs = dbs;
		this.params = params;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Task(String expression , Set<String> dbs , Map<String,Object> params) {
		this(null, expression, dbs, params);
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
}
