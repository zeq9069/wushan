package com.sankuai.canyin.r.wushan.demo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.sankuai.meituan.canyin.risk.virbius.expression.Expression;
import com.sankuai.meituan.canyin.risk.virbius.expression.ExpressionManager;
import com.sankuai.meituan.canyin.risk.virbius.expression.groovy.CustomGroovyClassLoader;
import com.sankuai.meituan.canyin.risk.virbius.expression.groovy.GroovyExpression;
import com.sankuai.meituan.canyin.risk.virbius.expression.groovy.GroovyExpressionManager;

import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * 
 * java 沙箱
 * 
 * @author kyrin
 *
 */
public class JavaSafe {
	
	public static void main(String[] args) throws Exception {
      
        CustomGroovyClassLoader groovyClassLoader = new CustomGroovyClassLoader(GroovyExpressionManager.class.getClassLoader(), null, new Class[]{});

        Class clazz = groovyClassLoader.parseClass("System.exit(0)");
        
        Script script = InvokerHelper.createScript(clazz, new Binding());
        Object run = script.run();
        
        System.out.println(run);
//        
//        file();
//        
//        security.checkPermission(new RuntimePermission("exitVM.13w"));
//        security.checkPermission(new RuntimePermission("exitVM.2"));
//        security.checkPermission(new RuntimePermission("exitVM.3"));
//        security.checkPermission(new RuntimePermission("exitVM.4"));
//
//        //file();
//        //readProperties();
//        //exit();
        
	}
	
	private static void file() throws IOException{
		File file = new File("/Users/kyrin/workspace/testworkspace/javaSafe/java.policy");
		FileInputStream fis = new FileInputStream(file);
		fis.read();
	}
	
	private static void readProperties(){
        System.out.println(System.getProperty("java.security.policy"));
	}
	
	private static void exit(){
		 Runtime.getRuntime().exit(0);
		System.exit(0);
	}
	
	private static void system(){
		System.gc();
	}
	
}
