package net.lbtech.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
					ch.pipeline().addLast("encode",new StringEncoder());      
					ch.pipeline().addLast("decode",new StringDecoder());  
					ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10,TimeUnit.SECONDS));
					ch.pipeline().addLast(new  NioServerHandler());
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
class ServerTask implements Runnable{
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(ServerTask.class);
	public void run() {
		Channel channel = null;
		Map.Entry<String, Channel>  entry = null;
		while (true) {
			Iterator<Entry<String, Channel>> it = GatewayService.map.entrySet().iterator();
			while (it.hasNext()) {
				entry = it.next();
				channel = entry.getValue();
				if (channel.isActive() && channel.isWritable()) {
					entry.getValue().writeAndFlush(new Date() + "我是测试的服务器端向客户端发数据");
				} else {
					channel.close();
					it.remove();
					logger.info("channel cannot connect uid : {}, server close and remove it" ,entry.getKey());
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
