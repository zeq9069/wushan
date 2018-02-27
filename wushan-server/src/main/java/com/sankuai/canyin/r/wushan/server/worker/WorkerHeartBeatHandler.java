package com.sankuai.canyin.r.wushan.server.worker;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class WorkerHeartBeatHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(WorkerHeartBeatHandler.class);
	
	private Worker worker;
	
	public WorkerHeartBeatHandler(Worker worker) {
		this.worker = worker;
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
}
