package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.worker.TaskManager;
import com.sankuai.canyin.r.wushan.server.worker.WorkerStatus;
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
	
	TaskManager taskManager;
	
	public NameNodeRpcServerHandler(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
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
		ClientInfosManager.removeDatanode(addr.getAddress().getHostAddress(),addr.getPort());
		LOG.info("{}  disconnect namenode",addr.getAddress().getHostAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		if(msg instanceof DBInfo){
			ClientInfosManager.addDBInfo(addr.getAddress().getHostAddress(),addr.getPort(),(DBInfo)msg);
		}else if(msg instanceof WorkerStatus){
			LOG.info("namenode receive a WorkerStatus : {}",msg);
			taskManager.updateTaskStatus(addr.getAddress().getHostAddress(),addr.getPort(),(WorkerStatus)msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("NameNodeRpcServerHandler errors.",cause);
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ctx.close();
	}

}
