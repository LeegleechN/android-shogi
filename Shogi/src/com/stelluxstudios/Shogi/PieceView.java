package com.stelluxstudios.Shogi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.stelluxstudios.Shogi.Game.Piece;

public class PieceView extends ImageView {

	public final Piece piece;
	public boolean isHighlighted;
	private HandView parent;
	
	public PieceView(Context context, Piece piece, HandView parent) {
		super(context);
		this.piece = piece;
		this.parent = parent;
		
		String piece_res_name = 
			parent.piecePrefix + piece.shortJapName;
		int id = getResources().getIdentifier(piece_res_name, "drawable", "com.stelluxstudios.Shogi");
		setImageResource(id);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!parent.highlightsEnabled)
			return false;
		if (!isHighlighted)
		{
			parent.clearHighlights();
			isHighlighted = true;
			parent.notifyOfSelection(piece);
			return true;
		}
		else
		{
			parent.clearHighlights();
			parent.notifyOfSelection(null);
			return true;
		}
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
