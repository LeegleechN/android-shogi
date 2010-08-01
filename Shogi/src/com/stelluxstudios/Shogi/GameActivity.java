package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stelluxstudios.Shogi.Game.Piece;
import com.stelluxstudios.Shogi.Game.Player;

public class GameActivity extends Activity {
   
	private BoardView boardView;
	private HandView blackHandView, whiteHandView;
	private Engine e;
	private Game game;
	private Handler handler = new Handler();
	//private Button moveButton;
	
	private boolean whiteIsComp, blackIsComp;
	
	SharedPreferences prefs;
	
	TextView turnReminder;
	ImageView turnReminderImage;
	
	Matrix upsideDownMatrix;
	Bitmap pointingUp, pointingDown;
	
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
        
        boardView = (BoardView)findViewById(R.id.boardView);
        whiteHandView = (HandView) findViewById(R.id.whiteHand);
        blackHandView = (HandView) findViewById(R.id.blackHand);
        turnReminder = (TextView)findViewById(R.id.turnReminder);
        turnReminderImage = (ImageView)findViewById(R.id.turnReminderImage);
        
        upsideDownMatrix = new Matrix();
        upsideDownMatrix.postRotate(180);
        pointingUp = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        pointingDown = Bitmap.createBitmap(pointingUp, 0, 0, pointingUp.getWidth(), pointingUp.getHeight(), upsideDownMatrix, false);
        
        e = new Engine();
    }
    
    private void makeComputerMove()
    {
    	e.makeMove();	
    	processGameStatus();
    	
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
    
    //returns true if the move worked, false if it was illegal
    public boolean tryMakeHumanMove(String move)
    {
    	 int ret = e.tryApplyMove(move.getBytes());
         Log.d("Engine", "tried applying human move, got: " + ret);
         
         if (ret == 0)
         {
        	 e.getBoardString();
        	 updateStateFromEngine();
        	 handler.post(new Runnable() {
				
				@Override
				public void run() {
					mainLoop();
				}
			});
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
    	game = Game.fromString(boardString);
    	
    	int player = e.getCurrentPlayer();
    	if (player == 0)
    		game.setCurrentPlayer(Player.Black);
    	else
    		game.setCurrentPlayer(Player.White);
    	
    	
		boardView.setGame(game);
		boardView.invalidate();
		
		whiteHandView.updateFromPieceList(game.getWhiteHand());
		whiteHandView.invalidate();
		blackHandView.updateFromPieceList(game.getBlackHand());
		blackHandView.invalidate();
		
		
		if (game.getCurrentPlayer() == Player.Black)
		{
			turnReminderImage.setImageBitmap(pointingUp);
			turnReminder.setText("Black's Turn");
			whiteHandView.highlightsEnabled = false;
			blackHandView.highlightsEnabled = true;
		}
		else
		{
			turnReminderImage.setImageBitmap(pointingDown);
			turnReminder.setText("White's Turn");
			blackHandView.highlightsEnabled = false;
			whiteHandView.highlightsEnabled = true;
		}
    }
    
    public void notifyPieceInHand(Piece p)
    {
    	boardView.setSelectedPieceInHand(p);
    }
    
    public void notifyPieceSelectedOnBoard()
    {
    	whiteHandView.clearHighlights();
    	blackHandView.clearHighlights();
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
			i.setClassName(GameActivity.this, NewGameActivity.class.getName());
			startActivityForResult(i, 0);
			return true;
		case 1:
			i.setClassName(GameActivity.this, Preferences.class.getName());
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
			if (resultCode == NewGameActivity.NEW_GAME_RESULT)
			{
				boolean whiteIsComp = data.getBooleanExtra("whiteIsComp", true);
				boolean blackIsComp = data.getBooleanExtra("blackIsComp", true);
				int handicapPosition = data.getIntExtra("handicap", 0);
				startGame(Game.initialConfig, whiteIsComp, blackIsComp);
			}
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
		
		boolean have_sd_card = ContentDownloader.verifyContentPresence(this,handler);
		
		prefs = getSharedPreferences(Preferences.PREFS, MODE_PRIVATE);
		
		boolean moveHints = prefs.getBoolean("showingHints", true);
		boardView.showMoveHints = moveHints;
		
		int boardPosition = prefs.getInt("boardImagePosition", 0);
		int boardRes = Preferences.boardOptions[boardPosition];
		boardView.setBackgroundRes(boardRes);
		
		int piecePosition = prefs.getInt("pieceImagePosition", 0);
		String piecePrefix = Preferences.pieceResName[piecePosition];
		boardView.piecePrefix = piecePrefix;	
		whiteHandView.piecePrefix = piecePrefix;
		whiteHandView.setBackgroundResource(boardRes);
		blackHandView.piecePrefix = piecePrefix;
		blackHandView.setBackgroundResource(boardRes);
		
		if (have_sd_card)
		{
		 e.initialize();
	     e.getBoardString();
	     updateStateFromEngine();
	     mainLoop();
		}
	}
	
	//TODO implement boardPosition (i.e. loading save games)
	void startGame(String boardPosition,boolean whiteIsComp, boolean blackIsComp)
	{
		this.whiteIsComp = whiteIsComp;
		this.blackIsComp = blackIsComp;
		e.newGame();
		e.getBoardString();
		updateStateFromEngine();
		mainLoop();
	}

	void mainLoop()
	{
		Player currentPlayer = game.getCurrentPlayer();
		boolean compTakesMove = (currentPlayer == Player.White && whiteIsComp || currentPlayer == Player.Black && blackIsComp);

		if (compTakesMove)
		{
			makeComputerMove();
			if (!processGameStatus())
			{
				handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						mainLoop();
					}
				},200);
				return;
			}
		} 
		else
		{
			processGameStatus();
			return;
		}
	}
	
	//returns true if the game is over
	boolean processGameStatus()
	{
		int gameStatus = e.getGameStatus();
		String cause = "Game ended for unknown reason";
		if (gameStatus == 18)
			cause = "Checkmate!";
		if (gameStatus == 19)
			cause = "White resigns!";
		if (gameStatus == 20)
			cause = "Black resigns!";
		if (gameStatus == 21)
			cause = "Draw!";
		if (gameStatus == 22)
			cause = "Game has been suspended??";
		
		if (gameStatus > 1)
    	{
    		AlertDialog.Builder b = new AlertDialog.Builder(GameActivity.this);
    		b.setTitle("Game Over");
    		b.setMessage(cause);
    		b.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GameActivity.this.finish();
					
				}
			});
    		b.create().show();
    		return true;
    	}
		else if (gameStatus < 0)
    	{
    		cause = "Lost communication with Bonanza engine!";
    		AlertDialog.Builder b = new AlertDialog.Builder(GameActivity.this);
    		b.setTitle("Error");
    		b.setMessage(cause);
    		b.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GameActivity.this.finish();
					
				}
			});
    		b.create().show();
    		return true;
    	}	
		return false;
	}
}