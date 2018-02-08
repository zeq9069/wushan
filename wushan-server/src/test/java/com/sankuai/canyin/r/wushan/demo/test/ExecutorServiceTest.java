package com.sankuai.canyin.r.wushan.demo.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceTest {
	
	private static ExecutorService exe = Executors.newFixedThreadPool(4);
	private static volatile int num = 1;
	
	
	public static void main(String[] args) {
		
		
		for(int i = 0 ; i < 10 ; i++){
			exe.submit(new Runnable() {
				public void run() {
					System.out.println("run..."+num++);
				}
			});
		}
		
		exe.shutdown();
		
		for(int i = 0 ; i < 10 ; i++){
			exe.submit(new Runnable() {
				public void run() {
					System.out.println("run..."+num++);
				}
			});
		}
	}

}
