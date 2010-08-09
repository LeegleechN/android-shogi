package com.stelluxstudios.Shogi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class NewGameActivity extends Activity {
	
	public static final int NEW_GAME_RESULT = 27;
	Spinner whitePlayerSpinner, blackPlayerSpinner, handicapSpinner;
	Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.new_game);
		
		whitePlayerSpinner = (Spinner)findViewById(R.id.whitePlayerSpinner);
		blackPlayerSpinner = (Spinner)findViewById(R.id.blackPlayerSpinner);
		handicapSpinner = (Spinner)findViewById(R.id.handicapSpinner);
		
		String[] options = getResources().getStringArray(R.array.human_or_comp);
		ArrayAdapter<String> whitePlayerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,android.R.id.text1, options);
		whitePlayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> blackPlayerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,android.R.id.text1, options);
		blackPlayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		options = getResources().getStringArray(R.array.handicaps);
		ArrayAdapter<String> handicapAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
		handicapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		whitePlayerSpinner.setAdapter(whitePlayerAdapter);
		blackPlayerSpinner.setAdapter(blackPlayerAdapter);
		handicapSpinner.setAdapter(handicapAdapter);
		
		startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = getIntent();
				i.putExtra("whiteIsComp",whitePlayerSpinner.getSelectedItemPosition() == 1);
				i.putExtra("blackIsComp",blackPlayerSpinner.getSelectedItemPosition() == 1);
				String[] bonanzaStr = getResources().getStringArray(R.array.handicaps_bonanza_str);
				i.putExtra("handicap_bonanza_str", bonanzaStr[handicapSpinner.getSelectedItemPosition()]);
				setResult(NEW_GAME_RESULT, i);
				finish();
			}
		});
		
		
	}

}
