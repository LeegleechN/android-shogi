package com.stelluxstudios.Shogi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Gallery;

public class Preferences extends Activity {
	
	Gallery boardGallery, pieceGallery;
	CheckBox hintCheckbox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.preferences);
		
		boardGallery = (Gallery)findViewById(R.id.boardGallery);
		pieceGallery = (Gallery)findViewById(R.id.pieceGallery);
		hintCheckbox = (CheckBox)findViewById(R.id.hintCheckbox);
		
		GalleryImageAdapter boardAdapter = new GalleryImageAdapter(this);
		boardAdapter.setImageSize(205, 227);
		boardAdapter.setImages(new int[]{R.drawable.ban_gohan,R.drawable.ban_kaya_a,R.drawable.ban_kaya_b,R.drawable.ban_kaya_c,
						R.drawable.ban_kaya_d,R.drawable.ban_muji,R.drawable.ban_oritatami,R.drawable.ban_stripe});
		boardGallery.setAdapter(boardAdapter);
		boardGallery.setUnselectedAlpha(1);
		
		GalleryImageAdapter pieceAdapter = new GalleryImageAdapter(this);
		pieceAdapter.setImageSize(86, 96);
		pieceAdapter.setImages(new int[]{R.drawable.koma_dirty_skei,R.drawable.koma_kinki_skei,R.drawable.koma_ryoko_skei});
		
		pieceGallery.setAdapter(pieceAdapter);
	}
}
