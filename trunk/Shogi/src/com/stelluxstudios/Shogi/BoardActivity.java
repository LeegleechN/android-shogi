package com.stelluxstudios.Shogi;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BoardActivity extends Activity {
   
	private TextView boardText;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        final Engine e = new Engine();
        e.newGame();
        
        boardText = (TextView)findViewById(R.id.boardText);
        boardText.setBackgroundResource(R.drawable.ban_kaya_b);
        boardText.setTextColor(Color.BLACK);
     
        Button b2 = (Button)findViewById(R.id.buttonMove);
        b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				e.makeMove();
				e.getBoardString();
				try {
					FileReader board = new FileReader("/sdcard/Android/com.stelluxstudios.Shogi/board_out.txt");
					char[] buffer = new char[2048];
					board.read(buffer);
					Board.fromString(new String(buffer));
					boardText.setText(new String(buffer));
					board.close();
					
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