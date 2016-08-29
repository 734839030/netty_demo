package net.lbtech.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 实际的业务处理类
 * 
 * @author DF
 *
 */
public class NioServerHandler extends SimpleChannelInboundHandler<String> {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(NioServerHandler.class);

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
		logger.info("--------server channelRegistered-------");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		logger.info("server--->收到客户端发的消息:" + msg);
		GatewayService.add("11", ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ctx.writeAndFlush("你好。。。。");
		logger.info("--------server channelActive-------");
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
                //可以发包了， 然后发出去后  客户单回应一个 这时就不会进入read了 来判断心跳
            }
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		try {
			super.exceptionCaught(ctx, cause);
		} catch (Exception e) {
			logger.info("server exception ....关闭连接");
			ctx.close();
		}
	}

}
