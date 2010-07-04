package com.stelluxstudios.Shogi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.stelluxstudios.Shogi.Board.Piece;

public class PieceView extends ImageView {

	public final Piece piece;
	public boolean isHighlighted;
	private HandView parent;
	
	public PieceView(Context context, Piece piece, HandView parent) {
		super(context);
		this.piece = piece;
		this.parent = parent;
		
		String piece_res_name = 
			"koma_kinki_torafu_" + piece.shortJapName;
		int id = getResources().getIdentifier(piece_res_name, "drawable", "com.stelluxstudios.Shogi");
		setImageResource(id);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		parent.clearHighlights();
		isHighlighted = true;
		parent.invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (isHighlighted)
			setColorFilter(Color.BLUE, Mode.LIGHTEN);
		else
			clearColorFilter();
		super.onDraw(canvas);
	}

}
