package com.stelluxstudios.Shogi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Gallery;

public class Preferences extends Activity {
	
	Gallery boardGallery, pieceGallery;
	CheckBox hintCheckbox;
	
	SharedPreferences prefs;
	static final String PREFS = "ShogiPrefs";
	
	static int[] boardOptions = new int[]{R.drawable.ban_gohan,R.drawable.ban_kaya_a,R.drawable.ban_kaya_b,R.drawable.ban_kaya_c,
			R.drawable.ban_kaya_d,R.drawable.ban_muji,R.drawable.ban_oritatami,R.drawable.ban_stripe};
	static int[] pieceOptions = new int[]{R.drawable.koma_western_skei,R.drawable.koma_dirty_skei,R.drawable.koma_kinki_skei,R.drawable.koma_ryoko_skei};
	static String[] pieceResName = new String[]{"koma_western_","koma_dirty_", "koma_kinki_", "koma_ryoko_"};
	                                   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.preferences);
		
		boardGallery = (Gallery)findViewById(R.id.boardGallery);
		pieceGallery = (Gallery)findViewById(R.id.pieceGallery);
		hintCheckbox = (CheckBox)findViewById(R.id.hintCheckbox);
		
		GalleryImageAdapter boardAdapter = new GalleryImageAdapter(this);
		boardAdapter.setImageSize(205, 227);
		boardAdapter.setImages(boardOptions);
		boardGallery.setAdapter(boardAdapter);
		boardGallery.setUnselectedAlpha(1);
		
		GalleryImageAdapter pieceAdapter = new GalleryImageAdapter(this);
		pieceAdapter.setImageSize(86, 96);
		pieceAdapter.setImages(pieceOptions);
		
		pieceGallery.setAdapter(pieceAdapter);
		
		prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
		
		boardGallery.setSelection(prefs.getInt("boardImagePosition", 0));
		pieceGallery.setSelection(prefs.getInt("pieceImagePosition", 0));
		hintCheckbox.setChecked(prefs.getBoolean("showingHints", true));
	}
	
	@Override
	protected void onPause()
	{
		Editor edit = prefs.edit();
		edit.putInt("boardImagePosition", boardGallery.getSelectedItemPosition());
		edit.putInt("pieceImagePosition", pieceGallery.getSelectedItemPosition());
		edit.putBoolean("showingHints", hintCheckbox.isChecked());
		edit.commit();
		super.onPause();
	}
}
