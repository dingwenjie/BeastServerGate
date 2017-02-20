package com.chen.login.message.req;

import org.apache.mina.core.buffer.IoBuffer;

import com.chen.message.Message;
/**
 * 客户端登陆请求消息
 * @author chen
 *
 */
public class ReqLoginMessage extends Message
{
	private int serverId;
	private String name;
	private String password;
	@Override
	public int getId() {
		return 1002;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String getQueue() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getServer() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean read(IoBuffer buf) {
		this.name = readString(buf);
		this.password = readString(buf);
		this.serverId = readInt(buf);
		return true;
	}
	@Override
	public boolean write(IoBuffer buf) {
		writeString(buf, this.name);
		writeString(buf, this.password);
		writeInt(buf, this.serverId);
		return true;
	}
}
