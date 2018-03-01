package com.sankuai.canyin.r.wushan.server.datanode.service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.datanode.SystemInfo;
import com.sankuai.canyin.r.wushan.server.datanode.store.StorageFactory;
import com.sankuai.canyin.r.wushan.server.handle.DataNodeHandler;
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

/**
 * 数据传输rpc
 * （仅仅作为传输导入数据使用）
 * 
 * @author kyrin
 *
 */
public class DataNodeTransferDataRpcService implements Service{
	
	private static final Logger LOG = LoggerFactory.getLogger(DataNodeTransferDataRpcService.class);
	
	private static EventLoopGroup work ;
	private static final Bootstrap boot;
	private String host;
	private int port;
	private StorageFactory factory;
	private SystemInfo sysInfo;

	static{
		work = new NioEventLoopGroup();
		boot = new Bootstrap();
	}
	
	public DataNodeTransferDataRpcService(String host , int port , StorageFactory factory , SystemInfo sysInfo) {
		this.host = host;
		this.port = port;
		this.factory = factory;
		this.sysInfo = sysInfo;
	}
	
	public void start(){
		LOG.info("DataNodeTransferDataRpcService starting...");
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
					.addLast(new NameNodeDecode())
					.addLast(new NameNodeEncode())
					.addLast(new DataNodeHandler(factory , sysInfo , new Rennection() {
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
			LOG.error("DataNodeTransferDataRpcService destroy failed.",e);
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
				LOG.info("DataNodeTransferDataRpcService connected Namenode success!");
			}else {
				future.channel().eventLoop().schedule(new Runnable() {
					public void run() {
						LOG.info("DataNodeTransferDataRpcService connected Namenode fialed. reconnecting Namenode...");
						try {
							reconnect();
						} catch (InterruptedException e) {
							LOG.info("DataNodeTransferDataRpcService reconnected Namenode fialed",e);
						}
					}
				}, 1, TimeUnit.SECONDS);
			}
		}
	}
}
