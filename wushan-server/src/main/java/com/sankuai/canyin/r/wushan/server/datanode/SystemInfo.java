package com.sankuai.canyin.r.wushan.server.datanode;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sankuai.canyin.r.wushan.ProcessUtils;

public class SystemInfo {
	
	int cpu;
	
	double cpuLoad;
	
	int memory;//M
	
	double memoryLoad;
	
	int disk;//M
	
	double diskLoad;
	
	long lastUpdateTimestamp;
	
	boolean stop = false;
	
	
	public SystemInfo() {
	}
	
	public void init(){
		Thread monitor = new Thread(new Runnable() {
			public void run() {
				while(!stop){
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
					}
					ProcessUtils proc = new ProcessUtils();
					String dir = System.getProperty("user.dir");
					String jsonCpus = proc.run("sh",dir+"/conf/cpu.sh");
					String jsonMemorys = proc.run("sh",dir+"/conf/cpu.sh");
					String jsonDisk = proc.run("sh",dir+"/conf/cpu.sh");
					
					try{
						JSONObject cpu = JSON.parseObject(jsonCpus);
						setCpu(cpu.getInteger("cpu"));
						setCpuLoad(cpu.getDouble("cpu_sum"));
						
						JSONObject memory = JSON.parseObject(jsonMemorys);
						setMemory(memory.getInteger("all_size"));
						setMemoryLoad(memory.getInteger("used_size")/memory.getInteger("all_size"));
						
						JSONObject disk = JSON.parseObject(jsonDisk);
						setDisk(disk.getInteger("all_size"));
						setDiskLoad(disk.getInteger("used_size")/disk.getInteger("all_size"));
					}catch(Exception e){
					}
				}		
			}
		});
		monitor.setDaemon(true);
		monitor.start();
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public double getMemoryLoad() {
		return memoryLoad;
	}

	public void setMemoryLoad(double memoryLoad) {
		this.memoryLoad = memoryLoad;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}

	public double getDiskLoad() {
		return diskLoad;
	}

	public void setDiskLoad(double diskLoad) {
		this.diskLoad = diskLoad;
	}

	public long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
}
