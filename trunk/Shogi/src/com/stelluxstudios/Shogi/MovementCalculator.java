package com.stelluxstudios.Shogi;

import static com.stelluxstudios.Shogi.Board.*;

import java.util.ArrayList;
import java.util.List;

import com.stelluxstudios.Shogi.Board.Piece;
import com.stelluxstudios.Shogi.Board.Player;

public class MovementCalculator 
{
	//enforces movement restrictions by whose turn it is, in addition to all other constraints
	//The resulting list may have duplicates
	public static List<Position> getValidMoves(Board b,int i, int j)
	{
		List<Position> out = new ArrayList<Position>();
		if (!Position.boundsCheck(i,j))
			return out;
		
		Piece movingPiece = b.pieceAt(i, j);
		if (movingPiece == Piece.Empty || movingPiece.owner != b.getCurrentPlayer())
			return out;
		
		Player side = movingPiece.owner;
		
		Position currentPosition = new Position(i, j);
		
		int move = movingPiece.movementCapabilities;
		
		//TODO rather ugly
		if ((move & FORWARD_ONE) != 0)
		{
			Position p = currentPosition.relativeForward(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & BACK_ONE) != 0)
		{
			Position p = currentPosition.relativeBack(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & LEFT_ONE) != 0)
		{
			Position p = currentPosition.left();
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & RIGHT_ONE) != 0)
		{
			Position p = currentPosition.right();
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & DIAG_FORWARD_LEFT_ONE) != 0)
		{
			Position p = currentPosition.relativeForwardLeft(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & DIAG_FORWARD_RIGHT_ONE) != 0)
		{
			Position p = currentPosition.relativeForwardRight(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & DIAG_BACK_LEFT_ONE) != 0)
		{
			Position p = currentPosition.relativeBackLeft(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & DIAG_BACK_RIGHT_ONE) != 0)
		{
			Position p = currentPosition.relativeBackRight(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		
		
		
		if ((move & FORWARD_SLIDE) != 0)
		{
			Position p = currentPosition.relativeForward(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);

				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.relativeForward(side);
				
			}
		}
		if ((move & BACK_SLIDE) != 0)
		{
			Position p = currentPosition.relativeBack(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.relativeBack(side);
				
			
			}
		}
		if ((move & LEFT_SLIDE) != 0)
		{
			Position p = currentPosition.left();
			while (isLegalTarget(b,p))
			{
				out.add(p);
				
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.left();
				
				
			}
		}
		if ((move & RIGHT_SLIDE) != 0)
		{
			Position p = currentPosition.right();
			while (isLegalTarget(b,p))
			{
				out.add(p);
				
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.right();
				
				
			}
		}
		if ((move & DIAG_FORWARD_LEFT_SLIDE) != 0)
		{
			Position p = currentPosition.relativeForwardLeft(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);
				
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.relativeForwardLeft(side);
				
				
			}
		}
		if ((move & DIAG_FORWARD_RIGHT_SLIDE) != 0)
		{
			Position p = currentPosition.relativeForwardRight(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);
				
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.relativeForwardRight(side);
				
				
			}
		}
		if ((move & DIAG_BACK_LEFT_SLIDE) != 0)
		{
			Position p = currentPosition.relativeBackLeft(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);
				
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				p = p.relativeBackLeft(side);
				
				
			}
		}
		if ((move & DIAG_BACK_RIGHT_SLIDE) != 0)
		{
			Position p = currentPosition.relativeBackRight(side);
			while (isLegalTarget(b,p))
			{
				out.add(p);
				if (b.pieceAt(p.i, p.j) != Piece.Empty)
					break;
				
				p = p.relativeBackRight(side);
			}
		}
		
		
		if ((move & FORWARD_LEFT_KNIGHT) != 0)
		{
			Position p = currentPosition.relativeForwardLeftKnight(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		if ((move & FORWARD_RIGHT_KNIGHT) != 0)
		{
			Position p = currentPosition.relativeForwardRightKnight(side);
			if (isLegalTarget(b,p))	out.add(p);
		}
		
		
		
		
		
		return out;
	}
	
	private static boolean isLegalTarget(Board b, Position p)
	{
		if (!Position.boundsCheck(p.i, p.j))
			return false;
		Piece capturedPiece = b.pieceAt(p.i, p.j);
		
		if (b.getCurrentPlayer() == capturedPiece.owner)
			return false;
		
		return true;
		
		
	}
	
	//immutable
	//Origin on the board is 0,0 top left
	public static class Position
	{
		public final int i,j;
		
		public Position(int i, int j) 
		{
			this.i = i;
			this.j = j;
		}
		
		public Position relativeForward(Player side)
		{
			if (side == Player.Black)
				return new Position(i, j-1);
			else
				return new Position(i, j+1);
		}
		

		public Position relativeBack(Player side)
		{
			if (side == Player.Black)
				return new Position(i, j+1);
			else
				return new Position(i, j-1);
		}
		
		public Position left()
		{
			return new Position(i-1, j);
		}
		
		public Position right()
		{
			return new Position(i+1, j);
		}
		
		public Position relativeForwardLeft(Player side)
		{
			if (side == Player.Black)
				return new Position(i-1, j-1);
			else
				return new Position(i+1, j+1);
		}
		

		public Position relativeForwardRight(Player side)
		{
			if (side == Player.Black)
				return new Position(i+1, j-1);
			else
				return new Position(i-1, j+1);
		}
		
		public Position relativeBackLeft(Player side)
		{
			if (side == Player.Black)
				return new Position(i-1, j+1);
			else
				return new Position(i+1, j-1);
		}
		

		public Position relativeBackRight(Player side)
		{
			if (side == Player.Black)
				return new Position(i+1, j+1);
			else
				return new Position(i-1, j-1);
		}
		
		public Position relativeForwardLeftKnight(Player side)
		{
			if (side == Player.Black)
				return new Position(i-1, j-2);
			else
				return new Position(i+1, j+2);
		}
		
		public Position relativeForwardRightKnight(Player side)
		{
			if (side == Player.Black)
				return new Position(i+1, j-2);
			else
				return new Position(i-1, j+2);
		}
		
	
		public static boolean boundsCheck(int i, int j)
		{
			if (i<0 || i>8 || j<0 || j>8)
				return false;
			return true;
		}
		
		public boolean inPromotionZone(Player player)
		{
			if (player == Player.Black)
				return (j<=2);
			else
				return (j>=6);
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Position))
				return false;
			
			Position comp = (Position)o;
			
			if (comp.i == i && comp.j == j)
				return true;
			
			return false;
		}
	}

}
