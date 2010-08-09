package com.stelluxstudios.Shogi;

public class Engine
{
	public Engine()
	{
		
	}
	public native void initialize();
	public native void newGame(byte[] handicap);
	
	public native int tryApplyMove(byte[] move);
	public native int makeMove();
	
	public native void getBoardString();
	
	public native int getGameStatus();
	public native int getCurrentPlayer();
	
	public native int saveToFile();
	public native int loadFromFile();
	
}
