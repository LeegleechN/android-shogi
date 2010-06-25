package com.stelluxstudios.Shogi;

public class Engine
{
	public Engine()
	{
		
	}
	
	public native void newGame();
	
	public native void applyMove(byte[] move);
	public native int makeMove();
	
	public native void getBoardString();
	
}
