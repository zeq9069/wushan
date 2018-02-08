package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;
import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 实现业务逻辑
 * 
 * @author kyrin
 *
 */
@Sharable
public class NameNodeServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNodeServerHandler.class);
	
	//激活
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.regist(addr.getAddress().getHostAddress(),addr.getPort(),ctx.channel());
		LOG.info("{} connect namenode",addr.getAddress().getHostAddress());
	}
	
	//断开
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.unregist(addr.getAddress().getHostAddress(),addr.getPort());
		LOG.info("{}  disconnect namenode",addr.getAddress().getHostAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		if(msg instanceof HeartbeatPakcet){
			ClientInfosManager.updateHeartbeat(addr.getAddress().getHostAddress(),addr.getPort(), (HeartbeatPakcet)msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		LOG.error("NameNodeServerHandler errors.",cause);
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.unregist(addr.getAddress().getHostAddress(),addr.getPort());
		ctx.close();
	}

}
