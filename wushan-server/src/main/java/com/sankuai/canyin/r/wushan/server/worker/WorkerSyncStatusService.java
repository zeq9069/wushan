package com.sankuai.canyin.r.wushan.server.worker;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.handle.Rennection;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * worker进程中，负责跟dn保持连接，并传递数据(同步worker执行状态到dn，并执行dn回传的命令)
 * 
 * @author kyrin
 *
 */
public class WorkerSyncStatusService {
	
	private static final Logger LOG = LoggerFactory.getLogger(WorkerSyncStatusService.class);
	
	private static EventLoopGroup work ;
	private static final Bootstrap boot;
	private String host;
	private int port;
	private Worker worker;
	
	
	static{
		work = new NioEventLoopGroup();
		boot = new Bootstrap();
	}
	
	public WorkerSyncStatusService(String host , int port , Worker worker) {
		this.host = host;
		this.port = port;
		this.worker = worker;
	}
	
	public void start(){
		LOG.info("WorkerSyncStatusService starting...");
		boot.group(work)
			.channel(NioSocketChannel.class)
			.remoteAddress(new InetSocketAddress(host, port))
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.handler(new ChannelInitializer<Channel>() {
				
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					.addFirst(new ChannelInboundHandlerAdapter(){
						@Override
						public void channelInactive(ChannelHandlerContext ctx) throws Exception {
							super.channelInactive(ctx);
							  ctx.channel().eventLoop().schedule(new Runnable() {
								public void run() {
									try {
										reconnect();
									} catch (InterruptedException e) {
									}
								}
							}, 1, TimeUnit.SECONDS);
						}
					})
					.addLast(new IdleStateHandler(0,0,5,TimeUnit.SECONDS))
					.addLast(new NameNodeDecode())
					.addLast(new NameNodeEncode())
					.addLast(new WorkerHeartBeatHandler(worker , new Rennection() {
						
						@Override
						public void rennection() {
							try {
								reconnect();
							} catch (InterruptedException e) {
								LOG.error("reconnect() fail.",e);
							}
						}
					}));
				}
			});
		try {
			reconnect();
		} catch (Exception e) {
			LOG.error("Datanonde connected Namenode failed.",e);
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
			LOG.error("WorkerSyncStatusService destroy failed.",e);
		}
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
				LOG.info("connected Datanode success!");
			}else {
				future.channel().eventLoop().schedule(new Runnable() {
					public void run() {
						LOG.info("connected Datanode fialed. reconnecting Datanode...");
						try {
							reconnect();
						} catch (InterruptedException e) {
							LOG.info("reconnected Datanode fialed",e);
						}
					}
				}, 1, TimeUnit.SECONDS);
			}
		}
	}
}
