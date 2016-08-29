package net.lbtech.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * client处理类
 * @author DF
 *
 */
public class NioClientHandler extends SimpleChannelInboundHandler<String>{
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(NioClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		logger.info("client----->收到服务器端消息：{}" , msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ctx.writeAndFlush("我先给服务器一个消息。。\r\n");
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		try {
			super.exceptionCaught(ctx, cause);
		} catch (Exception e) {
			logger.info("client exception ....关闭连接");
			ctx.close();
		}
	}

}
