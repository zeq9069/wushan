package com.sankuai.canyin.r.wushan.task;

import java.util.HashMap;
import java.util.Map;

import com.sankuai.meituan.canyin.risk.virbius.expression.ExpressionResult;
import com.sankuai.meituan.canyin.risk.virbius.expression.groovy.GroovyExpressionManager;

public class Task {
	
	static GroovyExpressionManager groovyExpressionManager = new GroovyExpressionManager();
	
	public static void main(String[] args) {
		Map<String,Object> context = new HashMap<String, Object>();
        ExpressionResult<Boolean> expressionResult = groovyExpressionManager.executeBoolResultExpression("1>2", context);
        System.out.println(expressionResult.getResult());
        
	}

}
