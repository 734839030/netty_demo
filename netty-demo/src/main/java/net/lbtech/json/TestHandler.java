package net.lbtech.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lbtech.server.NioServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestHandler extends SimpleChannelInboundHandler<String>{
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(NioServerHandler.class);
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
		//logger.info("--------server channelRegistered-------");
		ctx.writeAndFlush("send to client test");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		logger.info("server--->收到客户端发的消息:" + msg);
		ctx.writeAndFlush("reply: " + msg);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		try {
			//super.exceptionCaught(ctx, cause);
		} catch (Exception e) {
			logger.info("server exception ....关闭连接");
			ctx.close();
		}
	}
}
