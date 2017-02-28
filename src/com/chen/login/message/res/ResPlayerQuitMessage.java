package com.chen.login.message.res;

import org.apache.log4j.helpers.Transform;
import org.apache.mina.core.buffer.IoBuffer;

import com.chen.message.Message;
/**
 * 服务器发送给客户端玩家退出服务区消息
 * @author Administrator
 *
 */
public class ResPlayerQuitMessage extends Message
{
	public long userId;
	public byte bIsForced;
	@Override
	public int getId() {
		return 1032;
	}

	@Override
	public String getQueue() {
		return "Local";
	}

	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean read(IoBuffer buffer) {
		this.userId = readLong(buffer);
		this.bIsForced = readByte(buffer);
		return true;
	}

	@Override
	public boolean write(IoBuffer buffer) {
		writeLong(buffer, userId);
		writeByte(buffer, bIsForced);
		return true;
	}

}
