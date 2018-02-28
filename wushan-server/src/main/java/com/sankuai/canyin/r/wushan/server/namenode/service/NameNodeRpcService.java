package com.sankuai.canyin.r.wushan.server.namenode.service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.handle.NameNodeRpcServerHandler;
import com.sankuai.canyin.r.wushan.server.worker.TaskManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NameNodeRpcService implements Service{
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNodeTransferDataRpcService.class);
	
	int port;
	private static EventLoopGroup bossGroup ;
	private static EventLoopGroup workGroup ;
	private static ServerBootstrap server ;
	private TaskManager taskManager;
	
	static{
		bossGroup =  new NioEventLoopGroup();
		workGroup =  new NioEventLoopGroup();
		server = new ServerBootstrap();
	}
	
	public NameNodeRpcService(int port , TaskManager taskManager){
		this.port = port;
		this.taskManager = taskManager;
	}
	 
	public void start(){
		LOG.info("starting NameNodeRpcService service...");
		server.group(bossGroup,workGroup)
			  .channel(NioServerSocketChannel.class)
			  .localAddress(new InetSocketAddress(port))
			  .childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
								.addLast(new NameNodeDecode())
								.addLast(new NameNodeEncode())
								.addLast(new NameNodeRpcServerHandler(taskManager));
				}
			});
		try {
			boolean future = server.bind().sync().await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("An error occurred during NameNodeRpcService startup.",e);
		}
	}
	
	public void init() {
	}

	public void destroy() {
		try{
			if(bossGroup!=null){
				bossGroup.shutdownGracefully().sync();
			}
			if(workGroup!=null){
				workGroup.shutdownGracefully().sync();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
