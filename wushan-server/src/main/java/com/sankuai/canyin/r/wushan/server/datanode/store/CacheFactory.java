package com.sankuai.canyin.r.wushan.server.datanode.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 			businessLine
 * 		    /       \
 *       scence     scence
 *       /   \
 *  platform  platform  
 *   /    \
 * event  event
 *   |  
 * DataFile 
 * @author kyrin
 *
 *
 *
 */
@Deprecated
public class CacheFactory {
	
	private static final List<Node> roots = new ArrayList<Node>();
	
	public void put(NodeIndex index , DataFile file){
		if(index == null || file == null){
			return;
		}
		if(StringUtils.isBlank(index.getBusinessLine()) 
				|| StringUtils.isBlank(index.getEvent())
				|| StringUtils.isBlank(index.getPlatform())
				|| StringUtils.isBlank(index.getScence())){
			return;
		}
		
		Node event = build(index);
		
		if(event == null){
			return;
		}
		Node node = event.get(file.getKey());
		if(node != null){
			DataFile f = (DataFile)node;
			f.getOffsets().addAll(file.getOffsets());
		}else{
			event.putData(file.getKey(), file);
		}
	}
	
	private Node build(NodeIndex index){
		Node node = build(roots, index.getBusinessLine(), index, 1);
		int level = node.getLevel();
		while(level++ < 4 ){
			node = node.getChilds().get(0);
		}
		return node;
	}
	
	private Node build(List<Node> nodes , String value , NodeIndex index, int level){
		Node n = null;
		if(level > 4){
			return n;
		}
		for(Node node :nodes){
			if(Objects.equals(node.getValue(),value)){
				n = node;
				break;
			}
		}
		if(n == null){
			switch (level) {
			case 1: //businessLine
				n = buildBusinessLine(index);
				break;
			case 2: //scence
				n = buildSence(index);
				break;
			case 3: //platform
				n = buildPlatform(index);
				break;
			case 4: //event
				n = buildEvent(index);
				break;
			}
			nodes.add(n);
			return n;
		}else{
			if(level == 4){
				return n;
			}
			level++;
			switch (level) {
			case 1: //businessLine
				value = index.getBusinessLine();
				break;
			case 2: //scence
				value = index.getScence();
				break;
			case 3: //platform
				value = index.getPlatform();
				break;
			case 4: //event
				value = index.getEvent();
				break;
			}
			return build(n.getChilds(), value, index, level);
		}
	}
	
	private Node buildBusinessLine(NodeIndex index){
		Node businessLine = new Node(index.getBusinessLine() , 1);
		businessLine.getChilds().add(buildSence(index));
		return businessLine;
	}
	
	private Node buildSence(NodeIndex index){
		Node scence = new Node(index.getScence() , 2);
		scence.getChilds().add(buildPlatform(index));
		return scence;
	}
	
	public Node buildPlatform(NodeIndex index){
		Node platform = new Node(index.getPlatform() , 3);
		platform.getChilds().add(buildEvent(index));
		return platform;
	}
	
	public Node buildEvent(NodeIndex index){
		return new Node(index.getEvent() , 4);
	}
	
	public List<Node> getRoots(){
		return roots;
	}
	
//	public static void main(String[] args) {
//		CacheFactory factory = new CacheFactory();
//		NodeIndex index = new NodeIndex("1","2","3","4");
//		factory.put(index, new DataFile("18310700192"));
//		factory.put(index, new DataFile("18310700193"));
//		factory.put(index, new DataFile("18310700193"));
//
//		List<Node> nodes = factory.getRoots();
//		System.out.println(nodes.isEmpty());
//	}
	
}
