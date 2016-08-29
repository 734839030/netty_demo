package net.lbtech.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty 客户端代码
 * 
 */
public class NioClient {
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(NioClient.class);
	private static String host = "127.0.0.1";
	private static int port = 9999;
	public static  Channel channel = null;
	public static void main(String args[]) {
		start();
	}
	/**
	 * 启动
	 */
	public static void start(){
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("encode",new StringEncoder());      
					ch.pipeline().addLast("decode",new StringDecoder());  
					ch.pipeline().addLast(new NioClientHandler());
					ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10,TimeUnit.SECONDS));
					channel = ch;
				}
			});
			// Start the client.
			ChannelFuture f = b.connect(host, port);
			new Thread(new ClientTask()).run();
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
class ClientTask implements Runnable{
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(ClientTask.class);
	public void run() {
		try {
			while (true) {
				if (null != NioClient.channel) {
					if (NioClient.channel.isActive() && NioClient.channel.isWritable()) {
						NioClient.channel.writeAndFlush("我是客户端发的消息.....");
					} else { //重连
						logger.error("客户端重连....");
						NioClient.channel.close();
						NioClient.start();
					}
				}
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}