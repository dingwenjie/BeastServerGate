package com.chen.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.db.DBServer;
import com.chen.db.bean.Role;

public class RoleDao 
{
	private Logger log = LogManager.getLogger(RoleDao.class);
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();
	/**
	 * 删除角色
	 * @param id
	 * @return
	 */
	public int delete(long id)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			long start = System.currentTimeMillis();
			int rows = session.delete("game_role.delete",id);
			long end = System.currentTimeMillis();
			log.info("game_role.delete"+"耗费："+(end - start));
			return rows;
		} catch (Exception e) {
			log.error(e,e);
			return -1;
		}finally{
			session.close();
		}
	}
	/**
	 * 选择所有角色信息
	 * @param name
	 * @param server
	 * @return
	 */
	public List<Role> selectByUser(String name, int server)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userid", name);
			List<Role> list = session.selectList("game_role.selectByUser",map);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			session.close();
		}
	}
	/**
	 * 根据id选择某个角色
	 * @param id
	 * @return
	 */
	public Role selectById(long id)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			Role role = session.selectOne("game_role.selectById",id);
			return role;
		} catch (Exception e) {
			return null;
		}finally
		{
			session.close();
		}
	}
}
