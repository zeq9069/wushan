package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.namenode.dispatcher.Dispatcher;
import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.server.worker.TaskManager;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author kyrin
 *
 */
@Sharable
public class ClientServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientServerHandler.class);
	
	private Dispatcher dispatcher;
	
	private TaskManager taskManager;
	
	public ClientServerHandler(Dispatcher dispatcher , TaskManager taskManager) {
		this.dispatcher = dispatcher;
		this.taskManager = taskManager;
	}
	
	//激活
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		LOG.info("client -> namenode connected. client = {}",addr);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		if(msg instanceof DataPacket){
			dispatcher.dispatch(msg);
		}if(msg instanceof Task){
			taskManager.upload((Task)msg);
		}else{
			throw new Exception("错误的数据包");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ctx.close();
	}
}
