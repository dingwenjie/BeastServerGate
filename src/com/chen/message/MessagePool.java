package com.chen.message;

import java.util.HashMap;

import com.chen.command.Handler;
import com.chen.login.handler.ReqCreateCharacterHandler;
import com.chen.login.handler.ReqLoginHandler;
import com.chen.login.handler.ReqSelectCharacterHandler;
import com.chen.login.handler.ResLoginSuccessToGateHandler;
import com.chen.login.message.req.ReqCreateCharacterMessage;
import com.chen.login.message.req.ReqLoginMessage;
import com.chen.login.message.req.ReqSelectCharacterMessage;
import com.chen.login.message.res.ResLoginSuccessToGateMessage;
import com.chen.server.handler.ReqRegisterGateHandler;
import com.chen.server.handler.ResGateRegisterWorldHandler;
import com.chen.server.message.req.ReqRegisterGateMessage;
import com.chen.server.message.res.ResGateRegisterWorldMessage;
import com.chen.server.message.res.ResRegisterGateMessage;

public class MessagePool 
{
	HashMap<Integer, Class<?>> messages = new HashMap<Integer, Class<?>>();
	HashMap<Integer, Class<?>> handlers = new HashMap<Integer, Class<?>>();
	public MessagePool()
	{
		register(10002, ResGateRegisterWorldMessage.class,ResGateRegisterWorldHandler.class);
		register(10003, ReqRegisterGateMessage.class,ReqRegisterGateHandler.class);
		register(10008, ResLoginSuccessToGateMessage.class, ResLoginSuccessToGateHandler.class);
		register(1002, ReqLoginMessage.class, ReqLoginHandler.class);
		register(1004, ReqCreateCharacterMessage.class, ReqCreateCharacterHandler.class);
		register(1016, ReqSelectCharacterMessage.class, ReqSelectCharacterHandler.class);
	}
	private void register(int id,Class<?> messageClass,Class<?> handlerClass)
	{
		messages.put(id, messageClass);
		if (handlerClass != null)
		{
			handlers.put(id, handlerClass);
		}
	}
	public Message getMessage(int id) throws InstantiationException, IllegalAccessException 
	{
		if (!messages.containsKey(id))
		{
			return null;
		}
		else
		{
			return (Message)messages.get(id).newInstance();
		}
	}
	public Handler getHandler(int id) throws InstantiationException, IllegalAccessException
	{
		if (!handlers.containsKey(id))
		{
			return null;
		}
		else 
		{
			return (Handler)handlers.get(id).newInstance();
		}
	}
}
