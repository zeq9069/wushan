package com.sankuai.canyin.r.wushan.server.namenode.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.namenode.DataInfo;

import io.netty.channel.Channel;

/**
 * 任务或数据分发
 * 	负责将数据或者任务按照一定的算法分发到每台datanode
 * 
 * @author kyrin
 *
 */
public class Dispatcher {
	
	private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);
	
	private Strategy strategy;
	
	private ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private static final Strategy DEFAULT_STRATEGY = new DefaultDispatcherStrategy(); 
	
	volatile AtomicLong count = new AtomicLong(0);
	
	public Dispatcher(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public Dispatcher() {
		this.strategy = DEFAULT_STRATEGY;
	}
	
	public void dispatch(final Object target){
		exec.execute(new Runnable() {
			public void run() {
				DataInfo dataInfo = strategy.choose(target);
				Channel channel = ClientInfosManager.getChannel(dataInfo);
				if(channel != null){
					channel.writeAndFlush(target);
					System.out.println("分发数量："+count.incrementAndGet());
				}else{
					System.out.println("没有可见的datanode，数据丢弃");
				}
			}
		});
	}
}
