package com.sankuai.canyin.r.wushan.server.message;

/**
 * 心跳包
 * 收集每台datanode机器上的物理资源
 * 
 * CPU:核数，当前负载
 * 内存：总大小，当前使用大小
 * JVM相关资源：堆大小，使用大小
 * 
 * 
 * @author kyrin
 *
 */
public class HeartbeatPakcet {
	
	int cpu;
	double cpuLoad;
	int memory;
	double memoryLoad;
	long lastDatetime ;
	
	public HeartbeatPakcet(int cpu , double cpuLoad , int memory , double memoryLoad , long lastDateTime){
		this.cpu = cpu;
		this.cpuLoad = cpuLoad;
		this.memory = memory;
		this.memoryLoad = memoryLoad;
		this.lastDatetime = lastDateTime;
	}

	public int getCpu() {
		return cpu;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

	public int getMemory() {
		return memory;
	}

	public double getMemoryLoad() {
		return memoryLoad;
	}

	public void setMemoryLoad(int memoryLoad) {
		this.memoryLoad = memoryLoad;
	}

	public long getLastDatetime() {
		return lastDatetime;
	}
	
}
