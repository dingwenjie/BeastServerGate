package com.chen.player.structs;
/**
 * 玩家类
 * @author chen
 *
 */
public class Player
{
	private long id;
	private int server;//所在服务器
	private int createServer;//玩家创建服务器
	private String userid;
	private String web;//所在平台
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getServer() {
		return server;
	}
	public void setServer(int server) {
		this.server = server;
	}
	public int getCreateServer() {
		return createServer;
	}
	public void setCreateServer(int createServer) {
		this.createServer = createServer;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
}
