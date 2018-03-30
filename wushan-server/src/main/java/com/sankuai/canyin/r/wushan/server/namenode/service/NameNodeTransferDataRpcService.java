package com.sankuai.canyin.r.wushan.server.namenode.service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.handle.HeartBeatServerHandler;
import com.sankuai.canyin.r.wushan.server.handle.NameNodeTransferDataServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NameNodeTransferDataRpcService implements Service{
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNodeTransferDataRpcService.class);
	
	int port;
	private static EventLoopGroup bossGroup ;
	private static EventLoopGroup workGroup ;
	private static ServerBootstrap server ;
	
	static{
		bossGroup =  new NioEventLoopGroup();
		workGroup =  new NioEventLoopGroup();
		server = new ServerBootstrap();
	}
	
	public NameNodeTransferDataRpcService(int port){
		this.port = port;
	}
	 
	public void start(){
		LOG.info("starting NameNodeTransferDataRpcService ...");
		server.group(bossGroup,workGroup)
			  .channel(NioServerSocketChannel.class)
			  .localAddress(new InetSocketAddress(port))
			  .option(ChannelOption.SO_KEEPALIVE, true)
			  .childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(0,0,5,TimeUnit.SECONDS))
								.addLast(new NameNodeDecode())
								.addLast(new NameNodeEncode())
								.addLast(new HeartBeatServerHandler())
								.addLast(new NameNodeTransferDataServerHandler());
				}
			});
		try {
			boolean future = server.bind().sync().await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("An error occurred during NameNodeTransferDataRpcService startup.",e);
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
