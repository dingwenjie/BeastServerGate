package com.chen.login.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
import com.chen.login.message.req.ReqLoginMessage;
import com.chen.player.manager.PlayerManager;

public class ReqLoginHandler extends Handler
{

	@Override
	public void action() {
		Logger log = LogManager.getLogger(ReqLoginHandler.class);
		try {
			ReqLoginMessage msg = (ReqLoginMessage)this.getMessage();
			//登陆用户
			PlayerManager.getInstance().login(msg.getSession(), msg.getServerId(), msg.getName(), msg.getPassword());
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
}
