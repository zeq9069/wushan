package com.sankuai.canyin.r.wushan.server.handle;

import com.sankuai.canyin.r.wushan.server.datanode.service.WorkerManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WorkerHandler extends ChannelInboundHandlerAdapter{

	WorkerManager workerManager;
	
	public WorkerHandler(WorkerManager workerManager) {
		this.workerManager = workerManager;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	}

}
