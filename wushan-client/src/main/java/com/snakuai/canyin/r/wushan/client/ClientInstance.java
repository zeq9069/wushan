package com.snakuai.canyin.r.wushan.client;

import java.net.InetSocketAddress;

import com.snakuai.canyin.r.wushan.client.codec.WushanDecode;
import com.snakuai.canyin.r.wushan.client.codec.WushanEncode;

import DataClientHandler.DataClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientInstance {
	
	
	private static EventLoopGroup work ;
	private static final Bootstrap boot;
	private String host;
	private int port;
	
	ClientChannel channel = new ClientChannel();
	
	static{
		work = new NioEventLoopGroup();
		boot = new Bootstrap();
	}
	
	public ClientInstance(String host , int port) {
		this.host = host;
		this.port = port;
		init();
	}
	
	public void start(){
		final Bootstrap boot = new Bootstrap();
		boot.group(work)
			.channel(NioSocketChannel.class)
		    .option(ChannelOption.SO_BACKLOG, 128)
	        .option(ChannelOption.TCP_NODELAY, true)
	        .option(ChannelOption.SO_KEEPALIVE,true)
			.remoteAddress(new InetSocketAddress(host, port))
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					.addLast(new WushanDecode())
					.addLast(new WushanEncode())
					.addLast(new DataClientHandler(channel));
				}
			});
		try {
			ChannelFuture future1 = boot.connect().await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		
	}

	public void destroy(){
		try {
			if(work != null){
				work.shutdownGracefully().sync();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
