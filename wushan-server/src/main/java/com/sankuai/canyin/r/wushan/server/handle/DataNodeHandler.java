package com.sankuai.canyin.r.wushan.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.datanode.SystemInfo;
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
	private SystemInfo sysInfo;
	Rennection rennection;
	
	public DataNodeHandler(StorageFactory factory , SystemInfo sysInfo , Rennection rennection) {
		this.factory = factory;
		this.sysInfo = sysInfo;
		this.rennection = rennection;
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
				ctx.writeAndFlush(new HeartbeatPakcet(sysInfo.getCpu() ,sysInfo.getCpuLoad(),sysInfo.getMemory(), sysInfo.getMemoryLoad(),
						sysInfo.getDisk() , sysInfo.getDiskLoad() , sysInfo.getLastUpdateTimestamp()));
			}
		}finally{
            ReferenceCountUtil.release(in);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.error("datanode to namenode connect close.");
		rennection.rennection();
	}
}
