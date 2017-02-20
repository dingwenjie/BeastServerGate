package com.chen.server.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameConfig 
{
	//服务器索引  key=>服务器id，value=>世界地图id（0-为公共区）
	private HashMap<Integer, Integer> servers = new HashMap<Integer, Integer>();

	public HashMap<Integer, Integer> getServers() {
		return servers;
	}

	public void setServers(HashMap<Integer, Integer> servers) {
		this.servers = servers;
	}
	/**
	 * 根据角色所在地区取得服务器id
	 * @param area
	 * @return
	 */
	public int getServerByArea(int area)
	{
		Set<java.util.Map.Entry<Integer, Integer>> set = this.servers.entrySet();
		Iterator<java.util.Map.Entry<Integer, Integer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>)it.next();
			if (entry.getValue().equals(area))
			{
				return ((Integer)entry.getKey()).intValue();
			}		
		}
		return -1;
	}
	/**
	 * 根据角色所在的服务器得到地区id
	 * @param server
	 * @return
	 */
	public int getAreaByServer(int server)
	{
		if (servers.containsKey(server))
		{
			return servers.get(server);
		}else
		{
			return -1;
		}
	}
}
