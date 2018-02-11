package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.service.DBInfo;

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
public class NameNodeRpcServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNodeRpcServerHandler.class);
	
	//激活
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.registClient(addr.getAddress().getHostAddress(),addr.getPort(),ctx.channel());
		LOG.info("{} connect namenode",addr.getAddress().getHostAddress());
	}
	
	//断开
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.unregistClient(addr.getAddress().getHostAddress(),addr.getPort());
		LOG.info("{}  disconnect namenode",addr.getAddress().getHostAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		LOG.info("receive DBInfo from {}. {}",addr,msg);
		if(msg instanceof DBInfo){
			ClientInfosManager.addDBInfo(addr.getAddress().getHostAddress(),addr.getPort(),(DBInfo)msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("NameNodeRpcServerHandler errors.",cause);
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ClientInfosManager.unregistClient(addr.getAddress().getHostAddress(),addr.getPort());
		ctx.close();
	}

}
