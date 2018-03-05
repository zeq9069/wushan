package com.sankuai.canyin.r.wushan.server.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.worker.Command.CommandType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WorkerCommandHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(WorkerCommandHandler.class);
	
	private Worker worker;
	
	public WorkerCommandHandler(Worker worker) {
		this.worker = worker;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOG.info("Worker receive a Command.",msg);
		if(msg instanceof Command){
			Command cmd = (Command)msg;
			if(CommandType.DESTROY.code() == cmd.getType().code()){
				if(worker != null){
					LOG.error("Worker starting destroy...");
					worker.destroy();
				}
			}else if(CommandType.RESATRT.code() == cmd.getType().code()){
				// TODO restart worker service
				LOG.info("Worker receive a restart command.",msg);
			}
		}else{
			LOG.error("Worker receive a unknown message type.",msg);
		}
	}

}
