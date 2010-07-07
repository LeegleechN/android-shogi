package com.stelluxstudios.Shogi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.stelluxstudios.Shogi.Board.Piece;
import com.stelluxstudios.Shogi.Board.Player;

public class BoardView extends ImageView {

	private Board board;
	private Bitmap background, lines;
	private float 
	left_pad = 12f,
	top_pad = 10f,
	widthPerPiece,
	heightPerPiece;
	
	private UIState currentUIState = UIState.Clear;
	private enum UIState{Clear,Piece_Selected,WaitingToPlaceFromHand};
	
	private int selectedI, selectedJ;
	private Paint defaultPaint, selectedPaint;
	
	private boolean isInitialized = false;
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	public BoardView(Context context) {
		super(context);
	}
	
	//Must be called when there is enough info to init properly
	private void initialize()
	{		
		background = BitmapFactory.decodeResource(getResources(), R.drawable.ban_kaya_b);
		lines = BitmapFactory.decodeResource(getResources(), R.drawable.masu_dot);
		
		defaultPaint = new Paint();
		
		selectedPaint = new Paint();
		selectedPaint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, Mode.LIGHTEN));
	}
	
	public void setBoard(Board board)
	{
		this.board = board;
	}
	
	//TODO make more robust in the face of nearly square screens
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int x = MeasureSpec.getSize(widthMeasureSpec);
		int y = MeasureSpec.getSize(heightMeasureSpec);
		
		
		if (x < y)
		{
			setMeasuredDimension(x, (int)(x * (454.0/410.0)));
		}
		else
		{
			setMeasuredDimension(y,(int)( y * (410.0/454.0)));
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (board == null)
			return;
		
		if (!isInitialized)
		{
			initialize();
			isInitialized = true;
		}
		
		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();
		
		float widthScaleRatio, heightScaleRatio;
		
		//dimensions are nearly square, but not exactly
		if (canvasWidth <= canvasHeight)
			canvasHeight = (int)(canvasWidth * (454.0 / 410.0));
		else
			canvasWidth = (int)(canvasHeight * (410.0 / 454.0));
		
		widthScaleRatio = canvasWidth / 410.0f;
		heightScaleRatio = canvasHeight / 454.0f;
		
		Rect fullCanvas = new Rect(0, 0, canvasWidth, canvasHeight);
		
		canvas.drawBitmap(background,null,fullCanvas,new Paint());
		canvas.drawBitmap(lines,null,fullCanvas,new Paint());
		
		widthPerPiece = 43 * widthScaleRatio;
		heightPerPiece = 48 * heightScaleRatio;
		
		for (int i = 0 ; i < 9 ; i++)
		{
			for (int j = 0 ; j < 9 ; j++)
			{
				Piece p = board.pieceAt(i, j);
				if (p != Piece.Empty)
				{
					String piece_res_name = 
						"koma_kinki_torafu_" + p.shortJapName;
					int id = getResources().getIdentifier(piece_res_name, "drawable", "com.stelluxstudios.Shogi");
					boolean isSelected = (currentUIState == UIState.Piece_Selected && i == selectedI && j == selectedJ);
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
					Rect bound = new Rect(
							(int)((i*widthPerPiece) + left_pad),
							(int)((j*heightPerPiece) + top_pad),
							(int)(((i+1)*widthPerPiece)+ left_pad),
							(int)(((j+1)*heightPerPiece) + top_pad));
					canvas.drawBitmap(bitmap, null,bound, isSelected?selectedPaint:defaultPaint);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return true;
		
		//figure out the coordinates of the touch event on the board
		
		float x = event.getX();
		float y = event.getY();
		
		x -= left_pad;
		y -= top_pad;
		
		int i = (int) (x/widthPerPiece);
		int j = (int) (y/heightPerPiece);
		
		//bounds check
		if (i < 0 || i> 8 || j < 0 || j>8)
		{
			currentUIState = UIState.Clear;
			invalidate();
			return true;
		}
		
		
		Piece touchedPiece = board.pieceAt(i, j);
		
		Log.d("Touch", "detected a touch on the board at position: " + i + "," + j);
		Log.d("Touch", "touched piece: " + touchedPiece.englishName);
		
		
		if (currentUIState == UIState.Clear)
		{
			//Check if the piece is owned by the current player. If it is, select it.
			Player owner = touchedPiece.owner;
			
			if (owner != board.getCurrentPlayer())
				return true;
			
			currentUIState = UIState.Piece_Selected;
			selectedI = i;
			selectedJ = j;
			
			invalidate();
			return true;
		}
		if (currentUIState == UIState.Piece_Selected)
		{
			//for now, just try to move the piece
			
			Piece selectedPiece = board.pieceAt(selectedI, selectedJ);
			
			String move = "" + (9-selectedI) + (selectedJ+1) + (9-i) + (j +1) + selectedPiece.japAbbr.substring(1); //example: 7776FU
			
			boolean success = ((BoardActivity)getContext()).tryMakeHumanMove(move);
			
			if (success)
			{
				currentUIState = UIState.Clear;
				invalidate();
				return true;
			}
			else
			{
				Log.d("Move", "Move " + move + " rejected");
				currentUIState = UIState.Clear;
				invalidate();
				return true;
			}
		}
		
		
		
		return true;
	}


}
