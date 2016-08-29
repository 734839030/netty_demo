package net.lbtech.json;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

import net.lbtech.server.NioServer;
import net.lbtech.server.NioServerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {
	/**
	 * 日志对象
	 */
	protected static Logger logger = LoggerFactory.getLogger(NioServer.class);
	private static int port = 9991;
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
					ch.pipeline().addLast("encode",new StringEncoder());      
					//ch.pipeline().addLast("decode",new StringDecoder());  
					// LineBasedFrameDecoder按行分割消息 
					ch.pipeline().addLast(new LineBasedFrameDecoder(10000)); 
                    // 再按UTF-8编码转成字符串 
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8)); 
					
					ch.pipeline().addLast(new  TestHandler());
				}
			}).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
        	// Bind and start to accept incoming connections. 
        	ChannelFuture future = bootstrap.bind(port).sync();
        	logger.info("server started ,listen {}" ,port);
        	//启动一个线程 来给客户端发消息
        	//new Thread(new ServerTask()).run();
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
