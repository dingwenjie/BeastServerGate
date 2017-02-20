package com.chen.login.handler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.chen.command.Handler;
import com.chen.login.message.req.ReqSelectCharacterMessage;
import com.chen.player.manager.PlayerManager;

public class ReqSelectCharacterHandler extends Handler
{
	private Logger log = LogManager.getLogger(ReqSelectCharacterHandler.class);
	@Override
	public void action()
	{
		try {
			ReqSelectCharacterMessage msg = (ReqSelectCharacterMessage)getMessage();
			PlayerManager.getInstance().selectCharacter(msg.getSession(), msg.getPlayerId());
		} catch (Exception e) {
			log.error("选择角色失败");
			e.printStackTrace();
		}
	}

}
