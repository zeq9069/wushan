package com.sankuai.canyin.r.wushan.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WushanThreadFactory implements ThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);

	private final AtomicInteger threadNumber = new AtomicInteger(1);

	private final ThreadGroup group;

	private final String namePrefix;

	public WushanThreadFactory() {
		this("pool-" + poolNumber.getAndIncrement() + "-thread-");
	}

	public WushanThreadFactory(String namePrefix) {
		this.namePrefix = namePrefix + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

}
