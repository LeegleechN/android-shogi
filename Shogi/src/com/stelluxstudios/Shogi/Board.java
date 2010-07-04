package com.stelluxstudios.Shogi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private Piece[][] field = new Piece[9][9];
	private List<Piece> whiteHand = new ArrayList<Piece>(), 
						blackHand = new ArrayList<Piece>();
	
	public final static String initialConfig = 
"'  9  8  7  6  5  4  3  2  1 \n"+
"P1-KY-KE-GI-KI-OU-KI-GI-KE-KY\n"+
"P2 * -HI *  *  *  *  * -KA * \n"+
"P3-FU-FU-FU-FU-FU-FU-FU-FU-FU\n"+
"P4 *  *  *  *  *  *  *  *  * \n"+
"P5 *  *  *  *  *  *  *  *  * \n"+
"P6 *  * +FU *  *  *  *  *  * \n"+
"P7+FU+FU * +FU+FU+FU+FU+FU+FU\n"+
"P8 * +KA *  *  *  *  * +HI * \n"+
"P9+KY+KE+GI+KI+OU+KI+GI+KE+KY\n";
	
	public static Board fromString(String in)
	{
		Board b = new Board();
		BufferedReader stream = null;
		try{
		
		stream = new BufferedReader(new StringReader(in));
		
		//skip the first line, it's just the coordinates
		stream.readLine();
		
		char[] buffer = new char[3];
		for (int y = 0 ; y < 9 ; y++)
		{
			//skip the row coordinates
			stream.read(buffer, 0, 2);
			
			for (int x = 0; x < 9 ; x++)
			{
				//read everything in groups of 3
				stream.read(buffer,0,3);
				Piece p = Piece.from3CharCode(new String(buffer));
				b.setPiece(p, x, y);
			}
			
			//skip to the beginning of the next line
			stream.readLine();
		}
		
		String nextLine = stream.readLine();
		
		//Check if there's nothing in hand
		if (nextLine == null)
			return b;
		interpretHand(nextLine);
		
		nextLine = stream.readLine();
		
		//Check if there's nothing in hand
		if (nextLine == null)
			return b;
		interpretHand(nextLine);
		
		return b;
		
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally
		{
			if (stream!= null)
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private static void interpretHand(String nextLine) {
		return;
	}

	private Board()
	{
		for (int i = 0 ; i < 9 ; i++)
		{
			for (int j = 0 ; j <9 ; j++)
			{
				field[i][j] = Piece.Empty;
			}
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
		WPromotedPawn   ("Promoted Pawn"   ,"","gto"  ,"-TO"),
		WLance          ("Lance"           ,"","gkyo" ,"-KY"),
		WPromotedLance  ("Promoted Lance"  ,"","gnkyo","-NY"),
		WKnight         ("Knight"          ,"","gkei" ,"-KE"),
		WPromotedKnight ("Promoted Knight" ,"","gnkei","-NK"),
		WSilverGeneral  ("Silver General"  ,"","ggin" ,"-GI"),
		WPromotedSilver ("Promoted Silver" ,"","gngin","-NG"),
		WGoldGeneral    ("Gold General"    ,"","gkin", "-KI"),
		WBishop         ("Bishop"          ,"","gkaku","-KA"),
		WDragonHorse    ("Dragon Horse"    ,"","guma" ,"-UM" ),
		WRook           ("Rook"            ,"","ghi"  ,"-HI"),
		WDragonKing     ("Dragon King"     ,"","gryu" ,"-RY" ),
		WKing           ("King"            ,"","gou"  ,"-OU"),
		BPawn           ("Pawn"            ,"","sfu"  ,"+FU"),
		BPromotedPawn   ("Promoted Pawn"   ,"","sto"  ,"+TO"),
		BLance          ("Lance"           ,"","skyo" ,"+KY"),
		BPromotedLance  ("Promoted Lance"  ,"","snkyo","+NY"),
		BKnight         ("Knight"          ,"","skei" ,"+KE"),
		BPromotedKnight ("Promoted Knight" ,"","snkei","+NK"),
		BSilverGeneral  ("Silver General"  ,"","sgin" ,"+GI"),
		BPromotedSilver ("Promoted Silver" ,"","sngin","+NG"),
		BGoldGeneral    ("Gold General"    ,"","skin", "+KI"),
		BBishop         ("Bishop"          ,"","skaku","+KA"),
		BDragonHorse    ("Dragon Horse"    ,"","suma" ,"+UM" ),
		BRook           ("Rook"            ,"","shi"  ,"+HI"),
		BDragonKing     ("Dragon King"     ,"","sryu" ,"+RY" ),
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

