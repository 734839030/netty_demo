package net.lbtech.client;

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
		ctx.writeAndFlush("我先给服务器一个消息。。");
	}
	/**
	 * 响应netty 心跳  这个可以给客户端发送一些特定包 来标识
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			//多少秒没有读
			if (e.state() == IdleState.READER_IDLE) {
				logger.info("读超时.....");
			} else if (e.state() == IdleState.WRITER_IDLE) {//多少秒没有写
				//ctx.close();
				logger.info("写超时.....");
			}else if (e.state() == IdleState.ALL_IDLE) { //总时间
                logger.info("总超时.....");
                //ctx.close();
            }
		}
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
