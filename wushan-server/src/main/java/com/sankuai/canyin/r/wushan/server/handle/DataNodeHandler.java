package com.sankuai.canyin.r.wushan.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.datanode.store.StorageFactory;
import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class DataNodeHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(DataNodeHandler.class);
	
	private StorageFactory factory;
	
	public DataNodeHandler(StorageFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("EchoClientHandler -> channelActive");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		try{
			if(in instanceof DataPacket){
				DataPacket data = (DataPacket)in;
				factory.put(data.getDbString(),new String(data.getKey(),Charsets.UTF_8), data.getData());
			}else if(in instanceof HeartbeatPakcet){
				LOG.info(" recieve heartbeat => "+((HeartbeatPakcet)in).getCpu());
			}
		}finally{
            ReferenceCountUtil.release(in);
		}
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("EchoClientHandler -> channelInactive");
	}
	
}
