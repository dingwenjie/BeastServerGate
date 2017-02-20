package com.chen.util;


import java.nio.ByteOrder;
import java.util.List;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.chen.message.Message;
import com.chen.message.TransfersMessage;
import com.chen.server.GateServer;

public class MessageUtil 
{
	private static Logger log = LogManager.getLogger(MessageUtil.class);
	/**
	 * 网关服务器发送消息到游戏服务器
	 * @param server
	 * @param id
	 * @param message
	 * @return
	 */
	public static boolean sendMessageToGameServer(int server,int id,Message message)
	{
		List<IoSession> sessions = GateServer.getInstance().getGameSession(server);
		if (sessions != null)
		{
			message.setSendId(id);
			IoSession session = sessions.get(0);
			writeToGame(session, message);
			return true;
		}
		else
		{
			log.error("与游戏服务器"+server+"通讯session不存在!");
			return false;
		}
	}
	/**
	 * 发送消息到游戏服务器的缓存
	 * @param session
	 * @param message
	 */
	private static void writeToGame(IoSession session, Message message)
	{
		IoBuffer buf = IoBuffer.allocate(100);
		buf.setAutoExpand(true);
		buf.setAutoShrink(true);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putInt(message.getId());
		buf.putLong(message.getSendId());
		buf.putInt(message.getRoleId().size());
		for (int i =0;i<message.getRoleId().size();i++)
		{
			buf.putLong(message.getRoleId().get(i));
		}
		message.write(buf);
		buf.flip();
		buf.putInt(buf.limit() - Integer.SIZE/Byte.SIZE);
		buf.rewind();
		IoBuffer sendbuf = null;
		synchronized (session) 
		{
			if (session.containsAttribute("send_buf"))
			{
				sendbuf = (IoBuffer)session.getAttribute("send_buf");
			}
			else
			{
				sendbuf = IoBuffer.allocate(1024);
				sendbuf.setAutoExpand(true);
				sendbuf.setAutoShrink(true);
				sendbuf.order(ByteOrder.LITTLE_ENDIAN);
				session.setAttribute("send_buf", sendbuf);
			}
			sendbuf.put(buf);
		}
	}
	/**
	 * 直接发送消息到游戏服务器缓存
	 * @param session
	 * @param message
	 */
	private static void writeToGame(IoSession session,TransfersMessage message)
	{
		IoBuffer buf = IoBuffer.allocate(1024);
		buf.setAutoExpand(true);
		buf.setAutoShrink(true);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(message.getLengthWithRole());
		buf.putInt(message.getId());
		buf.putLong(message.getSendId());
		buf.putInt(message.getRoleIds().size());
		for (int i = 0; i < message.getRoleIds().size(); i++) {
			buf.putLong(message.getRoleIds().get(i));
		}
		buf.put(message.getBytes());
		buf.flip();
		
		synchronized (session) {
			IoBuffer sendbuf = null;
			if(session.containsAttribute("send_buf")){
				sendbuf = (IoBuffer)session.getAttribute("send_buf");
			}else{
				sendbuf = IoBuffer.allocate(1024);
				sendbuf.setAutoExpand(true);
				sendbuf.setAutoShrink(true);
				sendbuf.order(ByteOrder.LITTLE_ENDIAN);
				session.setAttribute("send_buf", sendbuf);
			}			
			sendbuf.put(buf);
		}
	}
	/**
	 * 网关发送消息给客户端玩家
	 * @param roleId
	 * @param message
	 */
	public static void tell_player_message(long roleId,Message message)
	{
		IoSession session  = GateServer.getInstance().getSessionByRole(roleId);
		if (session != null)
		{
			writeToPlayer(session, message);
		}
	}
	public static void tell_player_message(long roleId,TransfersMessage msg)
	{
		IoSession session = GateServer.getInstance().getSessionByRole(roleId);
		if (session != null)
		{
			writeToPlayer(session, msg);
		}
	}
	/**
	 * 写入到游戏服务器缓存
	 * @param session
	 * @param message
	 */
	private static void writeToPlayer(IoSession session, Message message)
	{
		IoBuffer buf = IoBuffer.allocate(100);
		buf.setAutoExpand(true);
		buf.setAutoShrink(true);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putInt(message.getId());
		message.write(buf);
		buf.flip();
		buf.putInt(buf.limit() - Integer.SIZE/Byte.SIZE);
		buf.rewind();	
		IoBuffer sendbuf = null;
		synchronized (session) {
			if(session.containsAttribute("send_buf")){
				sendbuf = (IoBuffer)session.getAttribute("send_buf");
			}else{
				sendbuf = IoBuffer.allocate(1024);
				sendbuf.setAutoExpand(true);
				sendbuf.setAutoShrink(true);
				sendbuf.order(ByteOrder.LITTLE_ENDIAN);
				session.setAttribute("send_buf", sendbuf);
			}			
			sendbuf.put(buf);
		}
	}
	/**
	 * 发送消息到游戏服务器缓存
	 * @param session
	 * @param msg
	 */
	private static void writeToPlayer(IoSession session,TransfersMessage msg)
	{
		IoBuffer buf = IoBuffer.allocate(1024);
		buf.setAutoExpand(true);
		buf.setAutoShrink(true);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putInt(msg.getId());
		buf.put(msg.getBytes());
		buf.flip();
		int length = buf.limit() - (Integer.SIZE / Byte.SIZE);
		buf.putInt(length);
		buf.rewind();
		synchronized (session)
		{
			IoBuffer sendBuf = null;
			if (session.containsAttribute("send_buf"))
			{
				sendBuf = (IoBuffer)session.getAttribute("send_buf");
			}
			else
			{
				sendBuf = IoBuffer.allocate(1024);
				sendBuf.setAutoExpand(true);
				sendBuf.setAutoShrink(true);
				sendBuf.order(ByteOrder.LITTLE_ENDIAN);
				session.setAttribute("send_buf",sendBuf);
			}
			sendBuf.put(buf);
		}
	}
	
	/**
	 * 客户端的消息直接发送到游戏服务器
	 * @param server
	 * @param id
	 * @param sendbuf
	 */
	public static void send_to_game(int server, int id, TransfersMessage sendbuf)
	{
		List<IoSession> sessions = GateServer.getInstance().getGameSession(server);
		if (sessions != null)
		{
			sendbuf.setSendId(id);
			IoSession session = sessions.get(0);
			writeToGame(session, sendbuf);
		}
		else
		{
			log.error("与游戏服务器"+server+"通信不存在");
		}
	}
}
