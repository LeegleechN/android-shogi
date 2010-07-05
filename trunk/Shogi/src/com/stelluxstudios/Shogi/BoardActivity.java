package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class BoardActivity extends Activity {
   
	private BoardView boardView;
	private HandView blackHandView, whiteHandView;
	private Engine e;
	private Button moveButton;
	
	private boolean whiteIsComp, blackIsComp;
	
	
    static {
        System.loadLibrary("bonanza");
    }
    
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Debug.startMethodTracing("Shogi_trace");

        setContentView(R.layout.main);
        
        whiteIsComp = false;
        blackIsComp = false;
        
        e = new Engine();
        e.newGame();
        
        boardView = (BoardView)findViewById(R.id.boardView);
        whiteHandView = (HandView) findViewById(R.id.whiteHand);
        blackHandView = (HandView) findViewById(R.id.blackHand);
        

        String move = "7776FU";
        
        int ret = e.tryApplyMove(move.getBytes());
        Log.d("Engine", "tried applying human move, got: " + ret);
        
        e.getBoardString();
        String boardString = getBoardString();
        updateViewsFromBoardString(boardString);
        
  
     /*
        moveButton= (Button)findViewById(R.id.buttonMove);
        moveButton.setOnClickListener(new OnClickListener() {
			
        	
			@Override
			public void onClick(View v) {
		makeMove();	        
    }
        });*/
    }
    
    private void makeMove()
    {
    	int ret = e.makeMove();
    	if (ret > 1)
    	{
    		String cause = "Game ended for unknown reason";
    		if (ret == 18)
    			cause = "Checkmate!";
    		if (ret == 19)
    			cause = "White resigns!";
    		if (ret == 20)
    			cause = "Black resigns!";
    		if (ret == 21)
    			cause = "Draw!";
    		if (ret == 22)
    			cause = "Game has been suspended??";
    		AlertDialog.Builder b = new AlertDialog.Builder(BoardActivity.this);
    		b.setTitle("Game Over");
    		b.setMessage(cause);
    		b.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BoardActivity.this.finish();
					
				}
			});
    		b.create().show();
    		
    	}	
    	else if (ret < 0)
    	{
    		String cause = "Lost communication with Bonanza engine!";
    		AlertDialog.Builder b = new AlertDialog.Builder(BoardActivity.this);
    		b.setTitle("Error");
    		b.setMessage(cause);
    		b.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BoardActivity.this.finish();
					
				}
			});
    		b.create().show();
    		
    	}	
    	else
    	{
			e.getBoardString();
			System.gc();
			
			String boardString = getBoardString();
			updateViewsFromBoardString(boardString);
			
			
			moveButton.post(new Runnable() {
				
				@Override
				public void run() {
					makeMove();
					
				}
			});
		
    	}
    }

    @Override
    protected void onPause() {
    	//Debug.stopMethodTracing();
    	super.onPause();
    }
    
    String getBoardString()
    {
    	try 
		{
			FileReader boardFile = new FileReader("/sdcard/Android/com.stelluxstudios.Shogi/board_out.txt");
			char[] buffer = new char[2048];
			int read = boardFile.read(buffer);
			String boardString = new String(buffer,0,read);
			
			Log.d("Board", boardString);
	
			boardFile.close();
			return boardString;
			
		} 
		catch (IOException e1) 
		{
			return null;
		}
    }
    
    private void updateViewsFromBoardString(String boardString)
    {
    	Board board = Board.fromString(boardString);
		boardView.setBoard(board);
		boardView.invalidate();
		
		whiteHandView.updateFromPieceList(board.getWhiteHand());
		whiteHandView.invalidate();
		blackHandView.updateFromPieceList(board.getBlackHand());
		blackHandView.invalidate();
		whiteHandView.highlightsEnabled = false;
    }
}