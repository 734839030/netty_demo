package net.lbtech.json;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	private static String host = "10.4.1.206";
	private static int port = 9991;
	public static  Channel channel = null;
	public static void main(String args[]) {
		ExecutorService executorService = Executors.newFixedThreadPool(10000);
		 for(int i=0;i<1;i++)  {
			 executorService.execute(new Runnable() {
				 public void run() {
					 start();
				 }
			 });
			 System.out.println("当前是第：" + i + "  个");
		 }
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
					channel = ch;
				}
			});
			// Start the client.
			ChannelFuture f = b.connect(host, port);
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
