package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BoardActivity extends Activity {
   
	private BoardView boardView;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        final Engine e = new Engine();
        e.newGame();
        
        final BoardView boardView = (BoardView)findViewById(R.id.boardView);
       
     
        Button b2 = (Button)findViewById(R.id.buttonMove);
        b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				e.makeMove();
				e.getBoardString();
				
				try {
					FileReader boardFile = new FileReader("/sdcard/Android/com.stelluxstudios.Shogi/board_out.txt");
					char[] buffer = new char[2048];
					boardFile.read(buffer);
					Board board = Board.fromString(new String(buffer));
					boardView.setBoard(board);
					boardView.invalidate();
					boardFile.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
        
    }

    static {
        System.loadLibrary("bonanza");
    }
}