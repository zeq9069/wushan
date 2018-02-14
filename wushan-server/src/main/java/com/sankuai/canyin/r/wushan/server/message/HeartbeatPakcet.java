package com.sankuai.canyin.r.wushan.server.message;

/**
 * 心跳包
 * 收集每台datanode机器上的物理资源
 * 
 * CPU:核数，当前负载
 * 内存：总大小，当前负载
 * 磁盘：总大小，当前负载
 * JVM相关资源：堆大小，使用大小(暂时没有)
 * 
 * @author kyrin
 *
 */
public class HeartbeatPakcet {
	
	int cpu; //cpu数量
	double cpuLoad; //当前cpu负载
	int memory;//总内存大小Mb
	double memoryLoad;//当前内存负载
	int disk;//磁盘总容量Mb
	double diskLoad;//磁盘负载
	long lastDatetime ;//毫秒
	
	public HeartbeatPakcet() {
		this(0, 0, 0, 0, 0, 0, System.currentTimeMillis());
	}
	
	public HeartbeatPakcet(int cpu , double cpuLoad , int memory , double memoryLoad ,
			int disk, double diskLoad , long lastDateTime){
		this.cpu = cpu;
		this.cpuLoad = cpuLoad;
		this.memory = memory;
		this.memoryLoad = memoryLoad;
		this.lastDatetime = lastDateTime;
		this.disk = disk;
		this.diskLoad = diskLoad;
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

	public int getDisk() {
		return disk;
	}

	public double getDiskLoad() {
		return diskLoad;
	}

	@Override
	public String toString() {
		return "HeartbeatPakcet [cpu=" + cpu + ", cpuLoad=" + cpuLoad + ", memory=" + memory + ", memoryLoad="
				+ memoryLoad + ", disk=" + disk + ", diskLoad=" + diskLoad + ", lastDatetime=" + lastDatetime + "]";
	}
}
