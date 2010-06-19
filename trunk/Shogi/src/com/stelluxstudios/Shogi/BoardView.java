package com.stelluxstudios.Shogi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.stelluxstudios.Shogi.Board.Piece;

public class BoardView extends ImageView {

	private Board board;
	private Bitmap background, lines;
	private float 
	left_pad = 8f,
	top_pad = 8f;
	
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
	}
	
	public void setBoard(Board board)
	{
		this.board = board;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (board == null)
			return;
		
		initialize();
		
		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();
		
		//dimensions are nearly square, but not exactly
		if (canvasWidth <= canvasHeight)
			canvasHeight = (int)(canvasWidth * (454.0 / 410.0));
		else
			canvasWidth = (int)(canvasHeight * (410.0 / 454.0));
		
		Rect fullCanvas = new Rect(0, 0, canvasWidth, canvasHeight);
		
		canvas.drawBitmap(background,null,fullCanvas,new Paint());
		canvas.drawBitmap(lines,null,fullCanvas,new Paint());
		
		float widthPerPiece = canvasWidth/9.2f;
		float heightPerPiece = canvasHeight/9.2f;
		
		for (int i = 0 ; i < 9 ; i++)
		{
			for (int j = 0 ; j < 9 ; j++)
			{
				Piece p = board.pieceAt(j, i);
				if (p != Piece.Empty)
				{
					String piece_res_name = 
						"koma_kinki_torafu_" + p.shortJapName;
					int id = getResources().getIdentifier(piece_res_name, "drawable", "com.stelluxstudios.Shogi");
					
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
					canvas.drawBitmap(bitmap, 
							i*widthPerPiece  + left_pad,
							j*heightPerPiece + top_pad, new Paint());
				}
			}
		}
	}


}
