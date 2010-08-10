package com.stelluxstudios.Shogi;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.stelluxstudios.Shogi.Game.Piece;

public class HandView extends LinearLayout
{	
	public boolean highlightsEnabled = true;
	public PieceView highlightedView;
	public boolean isBlack; //otherwise, is white
	String piecePrefix;
	
	public HandView(Context context) {
		super(context);
		
	}
	
	public HandView(Context context,AttributeSet attributes) {
		super(context,attributes);
		
	}
	
	//removes all highlights
	public void updateFromPieceList(List<Piece> in)
	{
		removeAllViews();
		
		for (Piece p : in)
		{
			PieceView pv = new PieceView(getContext(), p, this);
			addView(pv);
		}
	}
	
	//returns first highlighted piece (there should never be more than one highlighted
	public Piece getHighlighted()
	{
		int numChildren = getChildCount();
		
		for (int i = 0 ; i< numChildren ; i++)
		{
			PieceView pieceView = (PieceView)getChildAt(i);
			
			if (pieceView.isHighlighted)
				return pieceView.piece;	
		}
		return null;
	}
	
	public void removeHighlighted()
	{
		int numChildren = getChildCount();
		
		for (int i = 0 ; i< numChildren ; i++)
		{
			PieceView pieceView = (PieceView)getChildAt(i);
			
			if (pieceView.isHighlighted)
			{
				removeViewAt(i);
				break;
			}
		}
	}
	
	public void clearHighlights()
	{
		int numChildren = getChildCount();
		
		for (int i = 0 ; i< numChildren ; i++)
		{
			PieceView pieceView = (PieceView)getChildAt(i);
			Log.d("handview","unhighlighting: " + pieceView);
			pieceView.isHighlighted = false;
		}
	}

	public void notifyOfSelection(Piece p) {
		((GameActivity)getContext()).notifyPieceInHand(p);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return true;
		clearHighlights();
		((GameActivity)getContext()).notifyPieceInHand(null);
		invalidate();
		return true;
	}

	public void highlight(PieceView pieceView)
	{
		clearHighlights();
		Log.d("handview", "highlighting: " + pieceView);
		pieceView.isHighlighted = true;
		notifyOfSelection(pieceView.piece);
		invalidate();
	}
	

}
