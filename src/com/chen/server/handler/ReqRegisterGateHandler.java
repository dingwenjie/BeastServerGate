package com.chen.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
import com.chen.server.GateServer;
import com.chen.server.message.req.ReqRegisterGateMessage;
import com.chen.server.message.res.ResRegisterGateMessage;

public class ReqRegisterGateHandler extends Handler
{
	private Logger log = LogManager.getLogger(ReqRegisterGateHandler.class);
	@Override
	public void action()
	{
		try {
			ReqRegisterGateMessage msg = (ReqRegisterGateMessage)this.getMessage();
			GateServer.getInstance().registerGameServer(msg.getServerId(), msg.getSession());
			log.info("游戏服务器" + msg.getServerName() + "注册到" + GateServer.getInstance().getServer_name() + "成功！");
			//返回成功消息
			ResRegisterGateMessage returnMsg = new ResRegisterGateMessage();
			returnMsg.setServerId(GateServer.getInstance().getServer_id());
			returnMsg.setServerName(GateServer.getInstance().getServer_name());
			msg.getSession().write(returnMsg);
		} catch (Exception e) {
			log.error(e,e);
		}		
	}
}
