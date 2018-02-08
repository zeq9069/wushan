package com.sankuai.canyin.r.wushan.demo.test;


public class ExitException extends SecurityException{

	private static final long serialVersionUID = 8461131347522778479L;
	
	public ExitException() {
		super("禁止 exit JVM");
	}
}
