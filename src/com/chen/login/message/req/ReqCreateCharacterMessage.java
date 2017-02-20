package com.chen.login.message.req;

import org.apache.mina.core.buffer.IoBuffer;

import com.chen.message.Message;

/**
 * 客户端向网关服务器发送创建角色请求消息
 * @author chen
 *
 */
public class ReqCreateCharacterMessage extends Message
{
	private String name;
	private String icon;
	private byte sex;
	private int roleIndex;
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 1004;
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
		this.icon = readString(buf);
		this.sex = readByte(buf);
		this.roleIndex = readInt(buf);
		return true;
	}
	@Override
	public boolean write(IoBuffer buf) {
		writeString(buf, name);
		writeString(buf, icon);
		writeByte(buf,this.sex);
		writeInt(buf, roleIndex);
		return true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}
	public int getRoleIndex() {
		return roleIndex;
	}
	public void setRoleIndex(int roleIndex) {
		this.roleIndex = roleIndex;
	}
	
}
