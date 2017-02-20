package com.chen.login.bean;

import org.apache.mina.core.buffer.IoBuffer;

import com.chen.message.Bean;

public class CharacterInfo extends Bean
{
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getLogintime() {
		return logintime;
	}
	public void setLogintime(int logintime) {
		this.logintime = logintime;
	}
	private long playerId;
	private String name;
	private int level;
	private byte sex;
	private String icon;
	private int logintime;
	private int playerIndex;
	private int mapId;
	@Override
	public boolean read(IoBuffer buf) {
		this.playerId = readLong(buf);
		this.name = readString(buf);
		this.level = readInt(buf);
		this.sex = readByte(buf);
		this.icon = readString(buf);
		this.logintime = readInt(buf);
		this.playerIndex = readInt(buf);
		this.mapId = readInt(buf);
		return true;
	}
	@Override
	public boolean write(IoBuffer buf) {
		writeLong(buf, playerId);
		writeString(buf, name);
		writeInt(buf, level);
		writeByte(buf, sex);
		writeString(buf, icon);
		writeInt(buf, logintime);
		writeInt(buf, playerIndex);
		writeInt(buf, mapId);
		return true;
	}
	public int getPlayerIndex() {
		return playerIndex;
	}
	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

}
