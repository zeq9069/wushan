package com.sankuai.canyin.r.wushan.server.datanode.store;

public class NodeIndex {
	
	public String businessLine;
	
	public String scence;
	
	public String platform;
	
	public String event;
	
	public NodeIndex(String businessLine , String scence , String platform , String event) {
		this.businessLine = businessLine;
		this.scence = scence;
		this.platform = platform;
		this.event = event;
	}

	public String getBusinessLine() {
		return businessLine;
	}

	public void setBusinessLine(String businessLine) {
		this.businessLine = businessLine;
	}

	public String getScence() {
		return scence;
	}

	public void setScence(String scence) {
		this.scence = scence;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
}
