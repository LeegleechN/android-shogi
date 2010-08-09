package com.stelluxstudios.Shogi;

import java.io.File;
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
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Debug.MemoryInfo;
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
import android.widget.Toast;

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
	

	final String save_file_name = "save.csa";
	File saveFile;
	byte[] saveFile_cstring;
	
	MemoryInfo meminfo = new MemoryInfo();
	
	String handicap = "PI";
	
	boolean game_finished = false;

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
        
        //saveFile = new File(getFilesDir(),save_file_name);
        saveFile = new File(ContentDownloader.path_to_storage,save_file_name);
    	String path = saveFile.getAbsolutePath();
    	saveFile_cstring = new byte[path.length() + 1];
		path.getBytes(0, path.length(), saveFile_cstring, 0);
		saveFile_cstring[saveFile_cstring.length-1] = 0;
        
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
    
    void onCompFinishedMove()
    {
    	processGameStatus();
		System.gc();
		updateStateFromEngine();
    }
    
    private void makeComputerMove()
    {
    	Thread thinkThread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				e.makeMove();	
				onCompFinishedMove();
			}
		});
    	thinkThread.run();
    }
    
    //returns true if the move worked, false if it was illegal
    public boolean tryMakeHumanMove(String move)
    {
    	 int ret = e.tryApplyMove(move.getBytes());
         Log.d("Engine", "tried applying human move, got: " + ret);
         
     	Debug.getMemoryInfo(meminfo);
    	Log.d("memory", "dalvik pss: " + meminfo.dalvikPss + "native pss: " + meminfo.nativePss + "other pss: " + meminfo.otherPss);
         
         if (ret == 0)
         {
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
    	if (game_finished)
    		saveFile.delete();
    	else
    		e.saveToFile();
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
        e.getBoardString();
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
		//menu.add(0, 2, 0, "Save Game");
		//menu.add(0, 3, 0, "Load Game");
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
		case 2:
		
			//int saveRet = e.saveToFile(saveFile.getAbsolutePath());
			int saveRet = e.saveToFile();
			if (saveRet < 0)
				Toast.makeText(this, "Sorry,unable to save!", 1000).show();
			return true;
		case 3:
			//int loadRet = e.loadFromFile(saveFile.getAbsolutePath());
			int loadRet = e.loadFromFile();
			if (loadRet < 0)
				Toast.makeText(this, "Sorry,unable to load! Have you saved before?", 2000).show();
			updateStateFromEngine();
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
				saveFile.delete();
				whiteIsComp = data.getBooleanExtra("whiteIsComp", false);
				blackIsComp = data.getBooleanExtra("blackIsComp", false);
				handicap = data.getStringExtra("handicap_bonanza_str");
				if (handicap == null)
					handicap = "PI";
			}
			break;
		case 1:
			break;
		case 2:
			if (resultCode == ContentDownloader.SUCCESSFUL_SETUP)
			{
				//continue to onResume, which should succeed now
			}
			else
			{
				//can't do anything useful
				finish();
			}
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
		boardView.setPiecePrefix(piecePrefix);	
		whiteHandView.piecePrefix = piecePrefix;
		whiteHandView.setBackgroundResource(boardRes);
		blackHandView.piecePrefix = piecePrefix;
		blackHandView.setBackgroundResource(boardRes);
		
		boardView.setGame(Game.makeNew());
		boolean sdcard_is_set_up = ContentDownloader.verifyContentPresence(this, handler);

		if (sdcard_is_set_up)
		{
			e.initialize();
			
			if (e.loadFromFile() == 1)
			{
				//successful resume
				updateStateFromEngine();
				mainLoop();
			}
			else
			{
				//new game
				boolean whiteIsComp = this.whiteIsComp;
				boolean blackIsComp = this.blackIsComp;
				String handicap = this.handicap;
				this.whiteIsComp = false;
				this.blackIsComp = false;
				this.handicap = "PI";
				startGame(handicap, whiteIsComp, blackIsComp);
			}
		}
	}
	
	void startGame(String bonanza_handicap_str,boolean whiteIsComp, boolean blackIsComp)
	{
		this.whiteIsComp = whiteIsComp;
		this.blackIsComp = blackIsComp;
		byte[] out = new byte[bonanza_handicap_str.length() + 1];
		bonanza_handicap_str.getBytes(0, bonanza_handicap_str.length(), out, 0);
		out[out.length-1] = 0;
		e.newGame(out);
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
					game_finished = true;
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