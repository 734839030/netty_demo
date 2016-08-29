package net.lbtech.obj;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Netty 服务端代码 
 *  
 */  
public class NioServer {  
  
	/**
	 * 日志对象
	 */
	protected static Logger logger = LoggerFactory.getLogger(NioServer.class);
	private static int port = 9999;
    public static void main(String args[])  {  
    	
        // Server服务启动器  
        ServerBootstrap bootstrap = new ServerBootstrap();  
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        bootstrap.group(bossGroup, workerGroup)
        	.channel(NioServerSocketChannel.class)
        	.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel  ch) throws Exception {
					//可以在socket接上来的时候添加很多指定义逻辑
					//添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
					//设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问  防止内存溢出 
					ch.pipeline().addLast(new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					//添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10,TimeUnit.SECONDS));
					ch.pipeline().addLast(new  NioServerHandler());
				}
			}).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
        	// Bind and start to accept incoming connections. 
        	ChannelFuture future = bootstrap.bind(port).sync();
        	logger.info("server started ,listen {}" ,port);
           // Wait until the server socket is closed.
           // In this example, this does not happen, but you can do that to gracefully
           // shut down your server. 调用实现优雅关机
           future.channel().closeFuture().sync();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
    		bossGroup.shutdownGracefully();
    		workerGroup.shutdownGracefully();
    	}
    }  
}  