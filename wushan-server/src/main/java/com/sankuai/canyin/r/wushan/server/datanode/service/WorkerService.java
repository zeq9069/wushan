package com.sankuai.canyin.r.wushan.server.datanode.service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.codec.NameNodeDecode;
import com.sankuai.canyin.r.wushan.codec.NameNodeEncode;
import com.sankuai.canyin.r.wushan.server.handle.WorkerHandler;
import com.sankuai.canyin.r.wushan.service.DataNodeClientSideService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * worker服务端 ， 负责与Worker进行交互
 * 
 * @author kyrin
 *
 */
public class WorkerService implements Service {

	private static final Logger LOG = LoggerFactory.getLogger(WorkerService.class);

	int port;
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workGroup;
	private static ServerBootstrap server;
	WorkerManager workerManager;
	DataNodeClientSideService client;
	
	static {
		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();
		server = new ServerBootstrap();
	}

	public WorkerService(int port , WorkerManager workerManager , DataNodeClientSideService client) {
		this.port = port;
		this.workerManager = workerManager;
		this.client = client;
	}

	public void start() {
		LOG.debug("starting WorkerService ...");
		server.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline()
								.addLast(new NameNodeDecode())
								.addLast(new NameNodeEncode())
								.addLast(new WorkerHandler(workerManager , client));
					}
				});
		try {
			boolean future = server.bind().sync().await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("An error occurred during WorkerService startup.", e);
		}
	}

	public void init() {
	}

	public void destroy() {
		try {
			if (bossGroup != null) {
				bossGroup.shutdownGracefully().sync();
			}
			if (workGroup != null) {
				workGroup.shutdownGracefully().sync();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
