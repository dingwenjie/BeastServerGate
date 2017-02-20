package com.chen.login.message.res;

import org.apache.mina.core.buffer.IoBuffer;

import com.chen.login.bean.RoleAllInfo;
import com.chen.message.Message;
/**
 * 网关服务器发送给客户端进入大厅消息1005
 * @author chen
 *
 */
public class ResEnterLobbyMessage extends Message
{
	private RoleAllInfo roleAllInfo = new RoleAllInfo();
	private long playerId;
	public RoleAllInfo getRoleAllInfo() {
		return roleAllInfo;
	}

	public void setRoleAllInfo(RoleAllInfo roleAllInfo) {
		this.roleAllInfo = roleAllInfo;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 1005;
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
	public boolean write(IoBuffer buf) {
		// TODO Auto-generated method stub
		writeLong(buf, this.playerId);
		writeBean(buf, this.roleAllInfo);
		return true;
	}

	@Override
	public boolean read(IoBuffer buf) {
		this.playerId = readLong(buf);
		this.roleAllInfo = (RoleAllInfo)readBean(buf,RoleAllInfo.class);
		return true;
	}

}
