package DataClientHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.snakuai.canyin.r.wushan.client.ClientChannel;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class DataClientHandler extends ChannelInboundHandlerAdapter{

	ClientChannel channel;
	public DataClientHandler(ClientChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channel.setChannel(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("EchoClientHandler -> channelInactive");
	}
	
}
