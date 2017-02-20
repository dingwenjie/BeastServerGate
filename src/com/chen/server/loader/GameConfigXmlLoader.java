package com.chen.server.loader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.chen.server.config.GameConfig;

public class GameConfigXmlLoader 
{
	private Logger log = LogManager.getLogger(GameConfigXmlLoader.class);
	public GameConfig load(String file)
	{
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in = new FileInputStream(file);
			Document doc = builder.parse(in);
			NodeList list = doc.getElementsByTagName("servers");
			if (list.getLength() > 0)
			{
				GameConfig config = new GameConfig();
				Node node = list.item(0);
				NodeList childs = node.getChildNodes();
				for (int i=0;i<childs.getLength();i++)
				{
					if (childs.item(i).getNodeName().equals("server"))
					{
						NodeList schilds = childs.item(i).getChildNodes();
						ServerConfig sconfig = new ServerConfig();
						for (int j=0; j<schilds.getLength(); j++)
						{
							if (schilds.item(j).getNodeName().equals("area"))
							{
								sconfig.setArea(Integer.valueOf(schilds.item(j).getTextContent()));
							}
							else if(schilds.item(j).getNodeName().equals("server-id"))
							{
								sconfig.setServerId(Integer.valueOf(schilds.item(j).getTextContent()));
							}
						}
						if (!config.getServers().containsKey(sconfig.getServerId()))
						{
							config.getServers().put(sconfig.getServerId(), sconfig.getArea());
						}
					}
				}
				in.close();
				return config;
			}
		} catch (Exception e) {
			this.log.error(e,e);
		}
		return null;
	}
	private class ServerConfig
	{
		private int area;
		private int serverId;
		public int getArea() {
			return area;
		}
		public void setArea(int area) {
			this.area = area;
		}
		public int getServerId() {
			return serverId;
		}
		public void setServerId(int serverId) {
			this.serverId = serverId;
		}
	}	
}
