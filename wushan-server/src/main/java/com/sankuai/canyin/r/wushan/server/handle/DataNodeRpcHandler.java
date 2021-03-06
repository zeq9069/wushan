package com.sankuai.canyin.r.wushan.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.datanode.service.DataNodeRpcService;
import com.sankuai.canyin.r.wushan.server.datanode.service.WorkerManager;
import com.sankuai.canyin.r.wushan.server.namenode.ClientInfosManager;
import com.sankuai.canyin.r.wushan.server.worker.Command;
import com.sankuai.canyin.r.wushan.server.worker.Task;
import com.sankuai.canyin.r.wushan.service.DataNodeServiceImpl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 负责跟namenode的消息处理
 * @author kyrin
 *
 */
public class DataNodeRpcHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(DataNodeRpcHandler.class);
	private DataNodeServiceImpl protocolImpl;
	private WorkerManager workerManager;
	Rennection rennection;
	
	public DataNodeRpcHandler(DataNodeServiceImpl protocolImpl , WorkerManager workerManager , Rennection rennection) {
		this.protocolImpl = protocolImpl;
		this.workerManager = workerManager;
		this.rennection = rennection;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOG.info("DataNodeRpcService connected Namenode.");
		protocolImpl.refreshCon(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		if(in instanceof Task){
			LOG.info("DataNode receive a Task. Task = {}",in);
			workerManager.run((Task)in);
		}else if(in instanceof Command){
			LOG.info("datanode receive a command to worker.{}",in);
			Command cmd = (Command)in;
			Channel channel = workerManager.getWorkerChannelByTaskId(cmd.getTaskId());
			channel.writeAndFlush(cmd);
		}else{
			LOG.error("unknown message : {}",in);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.warn("DataNodeRpcService unconnected Namenode");
		rennection.rennection();
	}
}
