package com.stelluxstudios.Shogi;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private Piece[][] field = new Piece[9][9];
	private List<Piece> whiteHand = new ArrayList<Piece>(), 
						blackHand = new ArrayList<Piece>();
	
	public static Board fromString(String in)
	{
		Board b = new Board();
		try{
		
		BufferedReader stream = new BufferedReader(new StringReader(in));
		
		//skip the first line, it's just the coordinates
		stream.readLine();
		
		char[] buffer = new char[3];
		for (int row = 0 ; row < 9 ; row++)
		{
			//skip the row coordinates
			stream.read(buffer, 0, 2);
			
			for (int col = 0; col < 9 ; col++)
			{
				//read everything in groups of 3
				stream.read(buffer,0,3);
				Piece p = Piece.from3CharCode(new String(buffer));
				b.setPiece(p, row, col);
			}
			
			//skip to the beginning of the next line
			stream.readLine();
		}
		return b;
		
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Board newBoard()
	{
		return new Board();
	}
	
	public Piece pieceAt(int row, int col)
	{
		return field[row][col];
	}
	
	public void setPiece(Piece p, int row, int col)
	{
		field[row][col] = p;
	}
	
	public List<Piece> getWhiteHand()
	{
		return whiteHand;
	}
	
	public List<Piece> getBlackHand()
	{
		return blackHand;
	}
	
	public static enum Piece{
		WPawn           ("Pawn"            ,"","gfu"  ,"-FU"),
		WLance          ("Lance"           ,"","gkyo" ,"-KY"),
		WKnight         ("Knight"          ,"","gkei" ,"-KE"),
		WSilverGeneral  ("Silver General"  ,"","ggin" ,"-GI"),
		WGoldGeneral    ("Gold General"    ,"","gkin", "-KI"),
		WBishop         ("Bishop"          ,"","gkaku","-KA"),
		WDragonHorse    ("Dragon Horse"    ,"","guma" , "-" ),
		WRook           ("Rook"            ,"","ghi"  ,"-HI"),
		WDragonKing     ("Dragon King"     ,"","gryu" , "-" ),
		WKing           ("King"            ,"","gou"  ,"-OU"),
		BPawn           ("Pawn"            ,"","sfu"  ,"+FU"),
		BLance          ("Lance"           ,"","skyo" ,"+KY"),
		BKnight         ("Knight"          ,"","skei" ,"+KE"),
		BSilverGeneral  ("Silver General"  ,"","sgin" ,"+GI"),
		BGoldGeneral    ("Gold General"    ,"","skin", "+KI"),
		BBishop         ("Bishop"          ,"","skaku","+KA"),
		BDragonHorse    ("Dragon Horse"    ,"","suma" , "+" ),
		BRook           ("Rook"            ,"","shi"  ,"+HI"),
		BDragonKing     ("Dragon King"     ,"","sryu" , "+" ),
		BKing           ("King"            ,"","sou"  ,"+OU"),
		Empty           ("Empty","",""," * ");
		
		public String englishName, japName, shortJapName, japAbbr;
		
		Piece(String englishName, String japName,String shortJapName,String japAbbr)
		{
			this.englishName = englishName;
			this.japName = japName;
			this.shortJapName = shortJapName;
			this.japAbbr = japAbbr;
		};
		
		static Piece from3CharCode(String in)
		{
			for (Piece p : Piece.values())
			{
				if (p.japAbbr.equals(in))
					return p;
			}
			throw new RuntimeException();
		}
	
	}
}

