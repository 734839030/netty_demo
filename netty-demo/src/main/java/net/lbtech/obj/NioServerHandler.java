package net.lbtech.obj;

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
public class NioServerHandler extends SimpleChannelInboundHandler<Person> {

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
	protected void channelRead0(ChannelHandlerContext ctx, Person msg)
			throws Exception {
		logger.info("client----->收到服务器端消息：name:{},age:{}" , msg.getName(),msg.getAge());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ctx.writeAndFlush(new Person("黄登峰",11));
		logger.info("--------server channelActive-------");
	}
}
