package com.sankuai.canyin.r.wushan.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(HeartBeatServerHandler.class);
	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent ise = (IdleStateEvent)evt;
			if(ise.state() == IdleState.ALL_IDLE){
				LOG.info("send heartbeat to {}",ctx.channel().remoteAddress().toString()); 
	           ctx.writeAndFlush(new HeartbeatPakcet(0, 0 , 0 , 0 , 0 , 0 ,System.currentTimeMillis()));
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}
}
