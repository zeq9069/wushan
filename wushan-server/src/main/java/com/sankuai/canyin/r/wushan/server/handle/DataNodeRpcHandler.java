package com.sankuai.canyin.r.wushan.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.service.DataNodeServiceImpl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 负责跟namenode的消息处理
 * @author kyrin
 *
 */
public class DataNodeRpcHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(DataNodeRpcHandler.class);
	private DataNodeServiceImpl protocolImpl;
	
	public DataNodeRpcHandler(DataNodeServiceImpl protocolImpl) {
		this.protocolImpl = protocolImpl;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOG.info("DataNodeRpcService connected Namenode.");
		protocolImpl.refreshCon(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		if(in instanceof Task){
			LOG.info("DataNode receive a Task. Task = {}",in);
			//TODO datanode 处理 task
			
			
			
			
			
		}
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.warn("DataNodeRpcService unconnected Namenode");
	}
}
