package com.sankuai.canyin.r.wushan.server.namenode.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.namenode.DataInfo;

import io.netty.channel.Channel;

/**
 * 任务或数据分发 负责将数据或者任务按照一定的算法分发到每台datanode
 * 
 * 分发模型修改： 采用生产者消费这的模式，改为批量的消费发送，同时当没有数据发送的时候，就让发送线程awite
 * 
 * @author kyrin
 *
 */
public class Dispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

	private Strategy strategy;

	private ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private Queue<Object> dataQueue = new LinkedBlockingQueue<Object>();

	private static final Strategy DEFAULT_STRATEGY = new DefaultDispatcherStrategy();

	volatile AtomicLong count = new AtomicLong(0);

	private volatile boolean flag = false;

	public Dispatcher(Strategy strategy) {
		this.strategy = strategy;
		init();
	}

	public Dispatcher() {
		this(DEFAULT_STRATEGY);
	}

	public void dispatch(final Object target) {
		dataQueue.add(target);
	}

	private void init() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (!dataQueue.isEmpty()) {
						final List<Object> datas = new ArrayList<Object>();
						for (int i = 0; i < 100 && !dataQueue.isEmpty(); i++) {
							datas.add(dataQueue.poll());
						}
						Map<DataInfo, List<Object>> dataInfoMap = new HashMap<DataInfo, List<Object>>();
						for (Object target : datas) {
							DataInfo dataInfo = strategy.choose(target);
							List<Object> list = dataInfoMap.get(dataInfo);
							if (list == null) {
								list = new ArrayList<Object>();
								dataInfoMap.put(dataInfo, list);
							}
							list.add(target);
						}
						for (DataInfo info : dataInfoMap.keySet()) {
							Channel channel = ClientInfosManager.getTransferDataChannel(info);
							if (channel != null) {
								for (Object obj : dataInfoMap.get(info)) {
									channel.write(obj);
									LOG.info("分发数量：" + count.incrementAndGet());
								}
								channel.flush();
							} else {
								LOG.error("没有可见的datanode，数据丢弃");
							}
						}
					} else {
						LOG.info("分发线程等待中...");
						try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}).start();

	}
}
