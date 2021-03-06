package com.stelluxstudios.Shogi;

import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.stelluxstudios.Shogi.Game.Piece;
import com.stelluxstudios.Shogi.Game.Player;
import com.stelluxstudios.Shogi.MovementCalculator.Position;

public class BoardView extends ImageView {

	private Game board;
	private Bitmap background, lines, hintOverlay, sourceOverlay, destOverlay, dropOverlay;
	private float 
	left_pad = 9f,
	top_pad = 10f,
	widthPerPiece,
	heightPerPiece;
	
	String boardGraphics, pieceGraphics;
	
	private UIState currentUIState = UIState.Clear;
	private enum UIState{Clear,Piece_Selected,WaitingToPlaceFromHand};
	
	private int selectedI, selectedJ;
	private Paint defaultPaint, selectedPaint;
	private Piece selectedPieceInHand;
	
	private boolean isInitialized = false;
	boolean showMoveHints = true;
	
	private List<Position> legalMovesForSelectedPiece;
	private String pieceName; //used for the promote dialog
	
	private String piecePrefix = "koma_kinki_";
	
	int backgroundRes = R.drawable.ban_kaya_b;
	
	//a value of -1 indicates not to highlight anything
	int moveHighlightSourceI = -1, moveHighlightSourceJ = -1;
	int moveHighlightDestI = -1, moveHighlightDestJ = -1;
	boolean moveHighlightIsDrop;
	
	WeakHashMap<Piece, Bitmap> pieceBitmaps = new WeakHashMap<Piece, Bitmap>();
	
	GameActivity gameActivity;
	public boolean selectionsEnabled = true;
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameActivity = (GameActivity)context;
	}

	
	public BoardView(Context context) {
		super(context);
		gameActivity = (GameActivity)context;
	}
	
	//Must be called when there is enough info to init properly
	private void initialize()
	{		
		setBackgroundRes(backgroundRes);
		lines = BitmapFactory.decodeResource(getResources(), R.drawable.masu_dot);
		hintOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.hint_overlay);
		sourceOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.focus_source);
		destOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.focus_trpt_g);
		dropOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.focus_trpt_r);
		
		defaultPaint = new Paint();
		
		selectedPaint = new Paint();
		selectedPaint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, Mode.LIGHTEN));
	}
	
	public void setBackgroundRes(int res)
	{
		backgroundRes = res;
		background = BitmapFactory.decodeResource(getResources(), res);
		if (background == null)
			background = BitmapFactory.decodeResource(getResources(), R.drawable.ban_kaya_b);
	}
	
	public void setGame(Game board)
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
		
		boolean drawingHintOverlays = (showMoveHints && currentUIState == UIState.Piece_Selected);
		legalMovesForSelectedPiece = board.getValidMovesForPiece(selectedI, selectedJ);

		for (int i = 0 ; i < 9 ; i++)
		{
			for (int j = 0 ; j < 9 ; j++)
			{
				Piece p = board.pieceAt(i, j);
				Rect bound = new Rect(
						(int)((i*widthPerPiece) + left_pad),
						(int)((j*heightPerPiece) + top_pad),
						(int)(((i+1)*widthPerPiece)+ left_pad),
						(int)(((j+1)*heightPerPiece) + top_pad));
				if (i == moveHighlightDestI && j == moveHighlightDestJ)
				{
					if (moveHighlightIsDrop)
						canvas.drawBitmap(dropOverlay, null, bound, defaultPaint);
					else
						canvas.drawBitmap(destOverlay, null, bound, defaultPaint);
				}
				if (i == moveHighlightSourceI && j == moveHighlightSourceJ)
					canvas.drawBitmap(sourceOverlay, null, bound, defaultPaint);
				if (p != Piece.Empty)
				{
					
					
					boolean isSelected = (currentUIState == UIState.Piece_Selected && i == selectedI && j == selectedJ);
					Bitmap bitmap = pieceBitmaps.get(p);
					if (bitmap == null)
					{
						String piece_res_name = piecePrefix + p.shortJapName;
						int id = getResources().getIdentifier(piece_res_name, "drawable", "com.stelluxstudios.Shogi");
						bitmap = BitmapFactory.decodeResource(getResources(), id);
						pieceBitmaps.put(p, bitmap);
					}
				
					canvas.drawBitmap(bitmap, null,bound, isSelected?selectedPaint:defaultPaint);
				}
				
				if (drawingHintOverlays && legalMovesForSelectedPiece.contains(new Position(i, j)))
				{
					canvas.drawBitmap(hintOverlay, null, bound, defaultPaint);
				}
				
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (selectionsEnabled == false || event.getAction() != MotionEvent.ACTION_DOWN)
			return true;
		
		//figure out the coordinates of the touch event on the board
		
		float x = event.getX();
		float y = event.getY();
		
		x -= left_pad;
		y -= top_pad;
		
		final int i = (int) (x/widthPerPiece);
		final int j = (int) (y/heightPerPiece);
		
		//bounds check
		if (i < 0 || i> 8 || j < 0 || j>8)
		{
			currentUIState = UIState.Clear;
			invalidate();
			return true;
		}
		
		
		Piece touchedPiece = board.pieceAt(i, j);
		
		if (currentUIState == UIState.Clear)
		{
			//Check if the piece is owned by the current player. If it is, select it.
			Player owner = touchedPiece.owner;
			
			if (owner != board.getCurrentPlayer())
				return true;
			
			currentUIState = UIState.Piece_Selected;
			selectedI = i;
			selectedJ = j;
			
			//deselect any pieces in the hand
			gameActivity.notifyPieceSelectedOnBoard();
			
			invalidate();
			return true;
		}
		else if (currentUIState == UIState.Piece_Selected)
		{
			final Piece selectedPiece = board.pieceAt(selectedI, selectedJ);
			
			
			if (!legalMovesForSelectedPiece.contains(new Position(i, j)))
			{
				currentUIState = UIState.Clear;
				invalidate();
				return true;
			}
			
			pieceName = selectedPiece.japAbbr.substring(1);
			
			//you can promote if you started or ended in the promotion zone
			if (selectedPiece.promoted != null && (
					new Position(selectedI, selectedJ).inPromotionZone(board.getCurrentPlayer()) ||
					new Position(i, j).inPromotionZone(board.getCurrentPlayer())))
			{
				
				AlertDialog.Builder promoteDialog = new AlertDialog.Builder(getContext());
				promoteDialog.setTitle("Promote?");
				promoteDialog.setMessage("Would you like to promote this piece?");
				promoteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pieceName = selectedPiece.promoted.japAbbr.substring(1);
						submitMove(i, j);
					}
				});
				promoteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						submitMove(i, j);						
					}
				});
				promoteDialog.show();
			}
			else
			{
				submitMove(i, j);
			}
			
			
		}
		else if (currentUIState == UIState.WaitingToPlaceFromHand)
		{
			pieceName = selectedPieceInHand.japAbbr.substring(1);
			
			if ((selectedPieceInHand == Piece.WPawn || selectedPieceInHand == Piece.BPawn) 
				&& board.columnContainsPawn(i, board.getCurrentPlayer()))
			{
				AlertDialog.Builder illegalPawnDropDialog = new AlertDialog.Builder(getContext());
				illegalPawnDropDialog.setTitle("Illegal Move");
				illegalPawnDropDialog.setMessage("You can't drop a pawn in a file where you already have one.");
				illegalPawnDropDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				illegalPawnDropDialog.show();
			}
			else
				submitMove(i, j);
		}
		return true;
	}
	
	int getCSAColFromI(int i)
	{
		return 9-i;
	}
	
	int getCSARowFromJ(int j)
	{
		return j+1;
	}
	
	int getIFromCSACol(int csa_col)
	{
		return 9-csa_col;
	}
	
	int getJFromCSARow(int csa_row)
	{
		return csa_row - 1;
	}
	
	private void submitMove(int i, int j)
	{
		String move;
		if (currentUIState == UIState.Piece_Selected)
			move = "" + getCSAColFromI(selectedI) + getCSARowFromJ(selectedJ) + getCSAColFromI(i) + getCSARowFromJ(j) + pieceName; //example: 7776FU
		else if (currentUIState == UIState.WaitingToPlaceFromHand)
			move = "00" + getCSAColFromI(i) + getCSARowFromJ(j) + pieceName;
		else
			return;
		
		Log.d("Move", "submitted move: " + move);
		
		boolean success = gameActivity.tryMakeHumanMove(move);
		
		if (success)
		{
			currentUIState = UIState.Clear;
			invalidate();
		}
		else
		{
			Log.d("Move", "Move " + move + " rejected");
			currentUIState = UIState.Clear;
			invalidate();
		}
	}
	
	public void setSelectedPieceInHand(Piece piece)
	{
		if (piece != null)
		{
			currentUIState = UIState.WaitingToPlaceFromHand;
			selectedPieceInHand = piece;
			invalidate();
		}
		else
		{
			currentUIState = UIState.Clear;
			selectedPieceInHand = null;
			invalidate();
		}
	}
	
	public void setPiecePrefix(String prefix)
	{
		piecePrefix = prefix;
		pieceBitmaps.clear();
	}


	public void highlightMove(String lastMove)
	{
		clearMoveHighlights();
		//this is the move reported after e.g. a new game is started
		if (lastMove.equals("9191* "))
			return;
		if (lastMove.substring(0, 2).equals("00"))
		{
			//drop
			moveHighlightDestI = getIFromCSACol(Integer.parseInt(lastMove.substring(2, 3)));
			moveHighlightDestJ = getJFromCSARow(Integer.parseInt(lastMove.substring(3,4)));
			moveHighlightIsDrop = true;
		}
		else
		{
			//regular move
			moveHighlightSourceI = getIFromCSACol(Integer.parseInt(lastMove.substring(0, 1)));
			moveHighlightSourceJ = getJFromCSARow(Integer.parseInt(lastMove.substring(1, 2)));
			moveHighlightDestI = getIFromCSACol(Integer.parseInt(lastMove.substring(2, 3)));
			moveHighlightDestJ = getJFromCSARow(Integer.parseInt(lastMove.substring(3,4)));
			moveHighlightIsDrop = false;
		}
	}
	
	public void clearMoveHighlights()
	{
		moveHighlightSourceI = moveHighlightSourceJ = moveHighlightDestI = moveHighlightDestJ = -1;
	}


}
