package com.chen.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
import com.chen.server.GateServer;
import com.chen.server.message.res.ResGateRegisterWorldMessage;

public class ResGateRegisterWorldHandler extends Handler
{
	private Logger log = LogManager.getLogger(ResGateRegisterWorldHandler.class);
	@Override
	public void action() {
		try{
			ResGateRegisterWorldMessage msg = (ResGateRegisterWorldMessage)this.getMessage();
			log.info("网关服务器" + GateServer.getInstance().getServer_name() + "注册到" + msg.getServerName() + "返回成功！");
		}catch(ClassCastException e){
			log.error(e);
		}
		
	}

}
