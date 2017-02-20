package com.chen.db.dao;

import java.util.HashMap;



import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.db.DBServer;
import com.chen.db.bean.User;

public class UserDao 
{
	private Logger log = LogManager.getLogger(UserDao.class);
	private static final Logger dbConsuminglog = LogManager.getLogger("DBConsuming");
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();
	
	public int insert(User user)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			long start = System.currentTimeMillis();
			int rows = session.insert("com.chen.db.sqlmap.game_user.insert",user);
			session.commit();
			long end = System.currentTimeMillis();
			log.info("数据库插入账号耗费："+(end - start));
			return rows;
		} catch (Exception e) {	
			this.log.error(e,e);
			return -1;
		}finally
		{
			session.close();
		}
	}
	public User select(String username,int server)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			long start = System.currentTimeMillis();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("username", username);
			map.put("server", server);
			User user = (User)session.selectOne("com.chen.db.sqlmap.game_user.select",map);
			long end = System.currentTimeMillis();
			log.info("选择用户耗费："+(end-start));
			return user;
		} catch (Exception e) {
			log.error(e,e);
			return null;
		}finally{
			session.close();
		}
	}
	public int update(User user)
	{
		SqlSession session = sqlMapper.openSession();
		try {
			long start = System.currentTimeMillis();
			int rows = session.update("com.chen.db.sqlmap.game_user.update",user);
			session.commit();
			long end = System.currentTimeMillis();
			log.info("更新用户数据："+(end - start));
			return rows;
		} catch (Exception e) {
			log.error(e,e);
			return -1;
		}finally{
			session.close();
		}
	}
}
