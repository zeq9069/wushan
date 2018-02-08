package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sankuai.canyin.r.wushan.server.datanode.exception.NotAllowedException;

public class Node {
	
	public int level; // 1: businessLine , 2:scence , 3:platform , 4:event , 5:DataFile
	
	public String value;
	
	public List<Node> childs = new ArrayList<Node>();
	
	public Map<String,Node> datas = new HashMap<String, Node>();//只有level为4，也就是event节点的时候，才会有数据，其他时候都是空的
	
	public Node(String value) {
		this(value,5);
	}

	public Node(String value , int level) {
		this.value = value;
		this.level = level;
		if(level == 5){
			datas = null;
			childs = null;
		}else if(level != 4){
			datas = null;
		}else{
			childs = null;
		}
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Node> getChilds() {
		return childs;
	}

	public void setChilds(List<Node> childs) {
		this.childs = childs;
	}

	public int getLevel() {
		return level;
	}
	
	public void putData(String key , Node node){
		if(level == 4){
			datas.put(key, node);
		}else{
			throw new NotAllowedException("Must level equals 4 !");
		}
	}
	
	public Node get(String key){
		return datas.get(key);
	}
}
