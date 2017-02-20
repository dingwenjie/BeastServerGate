package com.chen.db.bean;

public class User 
{
	private Long userid;
	private String username;	
	private String password;
	private Integer server;
	private Long createtime; 
	private Long lastlogintime;
	private Integer isforbid;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}
	public Long getLastlogintime() {
		return lastlogintime;
	}
	public void setLastlogintime(Long lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	public Integer getServer() {
		return server;
	}
	public void setServer(Integer server) {
		this.server = server;
	}
	public Integer getIsForbid() {
		return isforbid;
	}
	public void setIsForbid(Integer isForbid) {
		this.isforbid = isForbid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
