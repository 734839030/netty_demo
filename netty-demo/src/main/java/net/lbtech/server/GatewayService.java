package net.lbtech.server;


import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayService {
	/**
	 * 日志对象
	 */
	protected static Logger logger = LoggerFactory.getLogger(GatewayService.class);
	/**
	 * 存放客户端连接
	 */
	public static Map<String,Channel> map = new ConcurrentHashMap<String, Channel>();
	/**
	 * 添加一个客户端连接
	 * @param uid
	 * @param socketChannel
	 */
	public static void add(String uid,Channel channel){
		if (map.containsKey(uid)) {
			Channel temp =map.get(uid);
			if (!(temp.isActive() && temp.isWritable())){
				map.remove(uid);
				map.put(uid, channel);
			}
		} else {
			map.put(uid, channel);
		}
		logger.info("添加一个客户端uid : {},当前客户端数量： {}" ,uid , getClientSize());
	}
	/**
	 * 移除一个客户端连接
	 * @param uid
	 */
	public static void remove(String uid){
		Channel channel = map.remove(uid);
		channel.close();
		logger.info("删除一个客户端uid : {},当前客户端数量： {}" ,uid , getClientSize());
	}
	/**
	 * 当前客户端数量
	 * @return
	 */
	public static int getClientSize(){
		return map.size();
	}
}
