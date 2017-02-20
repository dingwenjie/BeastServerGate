package com.chen.login.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
import com.chen.login.message.req.ReqCreateCharacterMessage;
import com.chen.player.manager.PlayerManager;

public class ReqCreateCharacterHandler extends Handler
{
	private Logger log = LogManager.getLogger(ReqCreateCharacterHandler.class);
	@Override
	public void action()
	{
		try {
			ReqCreateCharacterMessage msg = (ReqCreateCharacterMessage)this.getMessage();
			PlayerManager.getInstance().createCharacter(msg.getSession(),msg.getName(),msg.getIcon(),msg.getRoleIndex());
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
}
