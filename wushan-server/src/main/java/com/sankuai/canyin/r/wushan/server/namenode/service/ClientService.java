package com.sankuai.canyin.r.wushan.server.namenode.service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.handle.ClientServerHandler;
import com.sankuai.canyin.r.wushan.server.namenode.dispatcher.Dispatcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ClientService implements Service{
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);
	
	int port;
	private static EventLoopGroup bossGroup ;
	private static EventLoopGroup workGroup ;
	private static ServerBootstrap server ;
	private Dispatcher dispatcher;
	
	static{
		bossGroup =  new NioEventLoopGroup();
		workGroup =  new NioEventLoopGroup();
		server = new ServerBootstrap();
	}
	
	public ClientService(int port , Dispatcher dispatcher){
		this.port = port;
		this.dispatcher = dispatcher;
	}
	 
	public void start(){
		LOG.info("ClientService starting...");
		server.group(bossGroup,workGroup)
			  .channel(NioServerSocketChannel.class)
			  .localAddress(new InetSocketAddress(port))
			  .childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new NameNodeDecode())
								.addLast(new NameNodeEncode())
								.addLast(new ClientServerHandler(dispatcher));
				}
			});
		try {
			boolean future = server.bind().sync().await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("An error occurred during ClientService startup.",e);
		}
	}
	
	public void init() {
		//TODO
	}

	public void destroy() {
		LOG.info("ClientService destroy.");
		try{
			if(bossGroup!=null){
				bossGroup.shutdownGracefully().sync();
			}
			if(workGroup!=null){
				workGroup.shutdownGracefully().sync();
			}
		} catch (InterruptedException e) {
			LOG.error("An error occurred during ClientService destroy.",e);
		}
	}
}
