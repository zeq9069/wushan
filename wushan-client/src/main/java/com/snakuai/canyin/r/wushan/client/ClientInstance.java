package com.snakuai.canyin.r.wushan.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snakuai.canyin.r.wushan.client.codec.WushanDecode;
import com.snakuai.canyin.r.wushan.client.codec.WushanEncode;

import DataClientHandler.DataClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientInstance {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientInstance.class);
	
	private static EventLoopGroup work ;
	private Bootstrap boot;
	private String host;
	private int port;
	
	ClientChannel channel = new ClientChannel();
	
	static{
		work = new NioEventLoopGroup();
	}
	
	public ClientInstance(String host , int port) {
		this.host = host;
		this.port = port;
		init();
	}
	
	public void start(){
		boot = new Bootstrap();
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
					.addLast(new DataClientHandler(channel , new Rennection() {
						@Override
						public void rennection() {
							try {
								reconnect();
							} catch (InterruptedException e) {
								LOG.error("CLientInstance reconnected Namenode failed");
							}
						}
					}));
				}
			});
		try {
			reconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		
	}
	
	//TODO 重试提取到 channelInactive
		public void reconnect() throws InterruptedException{
			ChannelFuture future = boot.connect();
			future.addListener(new ChannelFutureListenerImpl());
			future.await(1, TimeUnit.SECONDS);
		}
		
		class ChannelFutureListenerImpl implements ChannelFutureListener{
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					LOG.info("ClientInstance connected Namenode success!");
				}else {
					future.channel().eventLoop().schedule(new Runnable() {
						public void run() {
							LOG.info("ClientInstance connected Namenode fialed. reconnecting Namenode...");
							try {
								reconnect();
							} catch (InterruptedException e) {
								LOG.info("ClientInstance reconnected Namenode fialed",e);
							}
						}
					}, 1, TimeUnit.SECONDS);
				}
			}
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
