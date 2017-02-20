package com.chen.player.structs;

public enum UserState 
{
	Logining(1),
	Creating(2),
	Selecting(3),
	WaitQuiting(4),
	Quiting(5),
	Deleting(6),;
	private int value;
	UserState(int value)
	{
		this.value = value;
	}
	public int getValue()
	{
		return this.value;
	}
}
