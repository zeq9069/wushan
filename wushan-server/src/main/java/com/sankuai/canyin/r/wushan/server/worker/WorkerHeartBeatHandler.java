package com.sankuai.canyin.r.wushan.server.worker;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.handle.Rennection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class WorkerHeartBeatHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(WorkerHeartBeatHandler.class);
	
	private Worker worker;
	
	private Rennection rennection;
	
	public WorkerHeartBeatHandler(Worker worker , Rennection rennection) {
		this.worker = worker;
		this.rennection = rennection;
	}
	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent ise = (IdleStateEvent)evt;
			if(ise.state() == IdleState.ALL_IDLE){
				LOG.info("send heartbeat to {}",ctx.channel().remoteAddress().toString()); 
	           ctx.writeAndFlush(worker.getStatus());
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.error("Worker to datanode connection close.");
		rennection.rennection();
	}
	
}
