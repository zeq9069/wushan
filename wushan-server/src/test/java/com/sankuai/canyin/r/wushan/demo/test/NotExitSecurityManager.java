package com.sankuai.canyin.r.wushan.demo.test;


import java.security.Permission;

public class NotExitSecurityManager extends SecurityManager{

	private void forbiddenExitPermission(Permission perm){
		if(perm.getName().startsWith("exitVM")){
			throw new ExitException();
		}
	}
	
	@Override
	public void checkPermission(Permission perm) {
		forbiddenExitPermission(perm);
		super.checkPermission(perm);
	}

	@Override
	public void checkExit(int status) {
		super.checkExit(status);
		throw new ExitException();
	}
}
