package DataClientHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snakuai.canyin.r.wushan.client.ClientChannel;
import com.snakuai.canyin.r.wushan.client.Rennection;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class DataClientHandler extends ChannelInboundHandlerAdapter{

	private static final Logger LOG =  LoggerFactory.getLogger(DataClientHandler.class);
	
	ClientChannel channel;
	 Rennection rennection;
	public DataClientHandler(ClientChannel channel , Rennection rennection) {
		this.channel = channel;
		this.rennection = rennection;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOG.info("Client connect namenode successed !");
		channel.setChannel(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("Client connection throw a exception !" , cause);
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.info("Client disconnect namenode .");
		rennection.rennection();
	}
	
}
