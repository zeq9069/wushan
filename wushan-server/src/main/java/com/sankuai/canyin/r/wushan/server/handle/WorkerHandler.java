package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.datanode.service.WorkerManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WorkerHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(WorkerHandler.class);
	
	WorkerManager workerManager;
	
	public WorkerHandler(WorkerManager workerManager) {
		this.workerManager = workerManager;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
		workerManager.registChannel(addr.getAddress().getHostAddress(),addr.getPort(),ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
		workerManager.unregistChannel(addr.getAddress().getHostAddress(),addr.getPort(),ctx.channel());	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOG.info("Worker Service receive a new message : {}",msg);
	}

}
