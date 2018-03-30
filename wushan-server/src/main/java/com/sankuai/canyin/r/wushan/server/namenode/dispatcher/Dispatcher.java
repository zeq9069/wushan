package com.sankuai.canyin.r.wushan.server.namenode.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

	private Queue<Object> dataQueue = new LinkedBlockingQueue<Object>();

	private static final Strategy DEFAULT_STRATEGY = new DefaultDispatcherStrategy();

	volatile AtomicLong count = new AtomicLong(0);

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
						List<Object> tmpFailRecords = new ArrayList<Object>(); 
						for (DataInfo info : dataInfoMap.keySet()) {
							Channel channel = ClientInfosManager.getTransferDataChannel(info);
							if (channel != null && channel.isOpen()) {
								for (Object obj : dataInfoMap.get(info)) {
									try{
										channel.write(obj);
										count.incrementAndGet();
									}catch(Exception e){
										LOG.error("dispatch error. channel write msg failed.",e);
										tmpFailRecords.add(obj);
									}
								}
								channel.flush();
							} else {
								tmpFailRecords.addAll(dataInfoMap.get(info));
								LOG.error("没有可见的datanode，数据随机分发");
								continue;
							}
						}
						if(!tmpFailRecords.isEmpty()){
							LOG.info("随机分发数量：{}",tmpFailRecords.size());
							Channel chn = ClientInfosManager.getRandomUsableChannel();
							for(Object obj : tmpFailRecords){
								if(chn != null && chn.isOpen()){
									chn.write(obj);
									count.incrementAndGet();
								}else{
									chn = ClientInfosManager.getRandomUsableChannel();
								}
							}
							chn.flush();
						}
						LOG.info("分发数量：" + count.get());
					} else {
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
