package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BoardActivity extends Activity {
   
	private BoardView boardView;
	private HandView blackHandView, whiteHandView;
	private Engine e;
	private Button moveButton;
	
    static {
        System.loadLibrary("bonanza");
    }
    
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Debug.startMethodTracing("Shogi_trace");

        setContentView(R.layout.main);
        
        e = new Engine();
        e.newGame();
        
        boardView = (BoardView)findViewById(R.id.boardView);
        whiteHandView = (HandView) findViewById(R.id.whiteHand);
        blackHandView = (HandView) findViewById(R.id.blackHand);
       
     
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
    		AlertDialog.Builder b = new AlertDialog.Builder(BoardActivity.this);
    		b.setTitle("Game Over");
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
			
			try 
			{
				FileReader boardFile = new FileReader("/sdcard/Android/com.stelluxstudios.Shogi/board_out.txt");
				char[] buffer = new char[2048];
				boardFile.read(buffer);
				Board board = Board.fromString(new String(buffer));
				boardView.setBoard(board);
				boardView.invalidate();
				
				whiteHandView.updateFromPieceList(board.getWhiteHand());
				blackHandView.updateFromPieceList(board.getBlackHand());
				whiteHandView.highlightsEnabled = false;
				
				boardFile.close();
				
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

    @Override
    protected void onPause() {
    	//Debug.stopMethodTracing();
    	super.onPause();
    }
}