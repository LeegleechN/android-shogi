package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.stelluxstudios.Shogi.Board.Piece;
import com.stelluxstudios.Shogi.Board.Player;

public class BoardActivity extends Activity {
   
	private BoardView boardView;
	private HandView blackHandView, whiteHandView;
	private Engine e;
	private Button moveButton;
	
	private boolean whiteIsComp, blackIsComp;
	
	SharedPreferences prefs;
	
	
	
	
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
        
        e.getBoardString();
        updateStateFromEngine();
        
  
     
        moveButton= (Button)findViewById(R.id.buttonMove);
        moveButton.setOnClickListener(new OnClickListener() {
			
        	
			@Override
			public void onClick(View v) {
		makeMove();	        
    }
        });
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
			updateStateFromEngine();
			
			/*
			moveButton.post(new Runnable() {
				
				@Override
				public void run() {
					makeMove();
					
				}
			});
		*/
    	}
    }
    
    //returns true if the move worked, false if it was illegal
    public boolean tryMakeHumanMove(String move)
    {
    	 int ret = e.tryApplyMove(move.getBytes());
         Log.d("Engine", "tried applying human move, got: " + ret);
         
         if (ret == 0)
         {
        	 e.getBoardString();
        	 updateStateFromEngine();
        	 return true;
         }
        	
         else
        	 return false;
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
    
    private void updateStateFromEngine()
    {
    	String boardString = getBoardString();
    	Board board = Board.fromString(boardString);
    	
    	int player = e.getCurrentPlayer();
    	if (player == 0)
    		board.setCurrentPlayer(Player.Black);
    	else
    		board.setCurrentPlayer(Player.White);
    	
    	
		boardView.setBoard(board);
		boardView.invalidate();
		
		whiteHandView.updateFromPieceList(board.getWhiteHand());
		whiteHandView.invalidate();
		blackHandView.updateFromPieceList(board.getBlackHand());
		blackHandView.invalidate();
		whiteHandView.highlightsEnabled = false;
    }
    
    public void notifyPieceInHand(Piece p)
    {
    	boardView.setSelectedPieceInHand(p);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "New Game");
		menu.add(0, 1, 0, "Preferences");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i = new Intent();
		switch (item.getItemId()) {
		case 0:
			i.setClassName(BoardActivity.this, NewGameActivity.class.getName());
			startActivityForResult(i, 0);
			return true;
		case 1:
			i.setClassName(BoardActivity.this, Preferences.class.getName());
			startActivityForResult(i, 1);
			return true;
		default:
			throw new RuntimeException();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			break;
		case 1:
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		prefs = getSharedPreferences(Preferences.PREFS, MODE_PRIVATE);
		
		boolean moveHints = prefs.getBoolean("showingHints", true);
		boardView.showMoveHints = moveHints;
		
		int boardPosition = prefs.getInt("boardImagePosition", 0);
		int boardRes = Preferences.boardOptions[boardPosition];
		boardView.setBackgroundRes(boardRes);
		
		int piecePosition = prefs.getInt("pieceImagePosition", 0);
		String piecePrefix = Preferences.pieceResName[piecePosition];
		boardView.piecePrefix = piecePrefix;	
	}
}