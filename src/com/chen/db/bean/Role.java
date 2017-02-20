package com.chen.db.bean;
/**
 * 角色实体类
 * @author chen
 *
 */
public class Role 
{
	private Long roleid;
	private String userid;
	private Integer createServer;
	private Integer area;
	private Integer level;
	private Integer sex;
	private String name;
	private String icon;
	private Integer roleindex;
	public Long getRoleid() {
		return roleid;
	}
	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Integer getCreateServer() {
		return createServer;
	}
	public void setCreateServer(Integer createServer) {
		this.createServer = createServer;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Long getLogintime() {
		return logintime;
	}
	public void setLogintime(Long logintime) {
		this.logintime = logintime;
	}
	public Integer getArea() {
		return area;
	}
	public void setArea(Integer area) {
		this.area = area;
	}
	public Integer getRoleindex() {
		return roleindex;
	}
	public void setRoleindex(Integer roleindex) {
		this.roleindex = roleindex;
	}
	private String data;
	private Long logintime;
}
