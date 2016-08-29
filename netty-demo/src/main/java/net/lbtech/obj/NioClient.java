package net.lbtech.obj;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

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
					//添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
					//设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问  防止内存溢出 
					ch.pipeline().addLast(new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					//添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new NioClientHandler());
					ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10,TimeUnit.SECONDS));
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
