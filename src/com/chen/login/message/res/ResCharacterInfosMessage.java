package com.chen.login.message.res;

import java.util.ArrayList;
import java.util.List;


import org.apache.mina.core.buffer.IoBuffer;

import com.chen.login.bean.CharacterInfo;
import com.chen.message.Message;
/**
 * 服务器发送给客户端角色信息列表
 * 当登陆成功的时候
 * @author chen
 *
 */
public class ResCharacterInfosMessage extends Message
{
	private List<CharacterInfo> characters = new ArrayList<CharacterInfo>();
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 1003;
	}

	public List<CharacterInfo> getCharacters() {
		return characters;
	}

	public void setCharacters(List<CharacterInfo> characters) {
		this.characters = characters;
	}

	@Override
	public String getQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean read(IoBuffer buf) {
		int characters_length = readInt(buf);
		for (int i=0;i<characters_length;i++)
		{
			characters.add((CharacterInfo)readBean(buf, CharacterInfo.class));
		}
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeInt(buf, characters.size());
		for (int i=0;i<characters.size();i++)
		{
			writeBean(buf, characters.get(i));
		}
		return true;
	}

}
