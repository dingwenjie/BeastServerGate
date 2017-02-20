package com.chen.db;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBServer 
{
	private Logger log = LogManager.getLogger(DBServer.class);
	private static Object obj = new Object();
	private SqlSessionFactory sqlMapper;
	private static DBServer server;
	private DBServer()
	{
		try {
			String filePath = "gate-config/db-config.xml";
			InputStream in = new FileInputStream(filePath);
			sqlMapper = new SqlSessionFactoryBuilder().build(in);
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e,e);
		}
	}
	public static DBServer getInstance()
	{
		synchronized (obj)
		{
			if (server == null)
			{
				server = new DBServer();
			}
			return server;
		}
	}
	public SqlSessionFactory getSqlMapper()
	{
		return this.sqlMapper;
	}
	public void SetSqlMapper(SqlSessionFactory sqlMapper)
	{
		this.sqlMapper = sqlMapper;
	}
}
