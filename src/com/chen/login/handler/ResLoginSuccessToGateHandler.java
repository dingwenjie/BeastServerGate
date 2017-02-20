package com.chen.login.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
import com.chen.login.message.res.ResEnterLobbyMessage;
import com.chen.login.message.res.ResLoginMessage;
import com.chen.login.message.res.ResLoginSuccessToGateMessage;
import com.chen.player.manager.PlayerManager;
import com.chen.util.MessageUtil;

public class ResLoginSuccessToGateHandler extends Handler
{
	private Logger log = LogManager.getLogger(ResLoginSuccessToGateHandler.class);
	@Override
	public void action() 
	{
		try {
			//收到游戏服务器发送的创建角色成功之后登陆成功的消息
			ResLoginSuccessToGateMessage msg = (ResLoginSuccessToGateMessage)this.getMessage();
			PlayerManager.getInstance().registerPlayer(msg.getServerId(), msg.getCreateServerId(), msg.getUserId(), msg.getPlayerId());
			//发送登陆成功之后,进入大厅消息给用户
			/*ResLoginMessage return_msg = new ResLoginMessage();
			return_msg.setErrorCode(0);
			MessageUtil.tell_player_message(msg.getPlayerId(), return_msg);
			*/
			ResEnterLobbyMessage return_msg = new ResEnterLobbyMessage();
			return_msg.setPlayerId(msg.getPlayerId());
			return_msg.setRoleAllInfo(msg.getRoleAllInfo());
			MessageUtil.tell_player_message(msg.getPlayerId(), return_msg);
		} catch (Exception e) {
			log.error(e,e);
		}
	}

}
