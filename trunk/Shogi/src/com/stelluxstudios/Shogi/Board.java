package com.stelluxstudios.Shogi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

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
	
	//these should be in Piece, but I couldn't get that placement to work with the Piece constructors
	public final static int 
	FORWARD_ONE =		 0x00000001,
	BACK_ONE = 			 0x00000002,
	LEFT_ONE = 			 0x00000004,
	RIGHT_ONE =			 0x00000008,
	
	DIAG_FORWARD_LEFT_ONE =  0x00000010,
	DIAG_FORWARD_RIGHT_ONE = 0x00000020,
	DIAG_BACK_LEFT_ONE = 	 0x00000040,
	DIAG_BACK_RIGHT_ONE = 	 0x00000080,
	
	FORWARD_SLIDE = 	 0x00000100,
	BACK_SLIDE = 		 0x00000200,
	LEFT_SLIDE = 		 0x00000400,
	RIGHT_SLIDE = 		 0x00000800,
	
	DIAG_FORWARD_LEFT_SLIDE = 	 0x00001000,
	DIAG_FORWARD_RIGHT_SLIDE = 	 0x00002000,
	DIAG_BACK_LEFT_SLIDE = 		 0x00004000,
	DIAG_BACK_RIGHT_SLIDE = 	 0x00008000,
	
	FORWARD_LEFT_KNIGHT= 0x00010000,
	FORWARD_RIGHT_KNIGHT=0x00020000,
	
	GOLD_MOVEMENT =  FORWARD_ONE | DIAG_FORWARD_LEFT_ONE | DIAG_FORWARD_RIGHT_ONE | LEFT_ONE | RIGHT_ONE | BACK_ONE,
	SILVER_MOVEMENT =  FORWARD_ONE | DIAG_FORWARD_LEFT_ONE | DIAG_FORWARD_RIGHT_ONE | DIAG_BACK_LEFT_ONE | DIAG_BACK_RIGHT_ONE,
	KING_MOVEMENT = FORWARD_ONE | DIAG_FORWARD_LEFT_ONE | DIAG_FORWARD_RIGHT_ONE | LEFT_ONE | RIGHT_ONE | DIAG_BACK_LEFT_ONE | DIAG_BACK_RIGHT_ONE | BACK_ONE,
	ROOK_MOVEMENT = FORWARD_SLIDE | BACK_SLIDE | LEFT_SLIDE | RIGHT_SLIDE,
	BISHOP_MOVEMENT = DIAG_FORWARD_LEFT_ONE | DIAG_FORWARD_RIGHT_SLIDE | DIAG_BACK_LEFT_SLIDE | DIAG_BACK_RIGHT_SLIDE;
	
	
	public static Board fromString(String in)
	{
		Board b = new Board();
		BufferedReader stream = null;
		try
		{
		
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
		
		while (nextLine != null)
		{
			interpretHand(nextLine,b);
			nextLine = stream.readLine();
		}
		
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
	
	private static void interpretHand(String line,Board board) {
		int length = line.length();
		//2 for the P+/P- header, for because the piece codes are 2 chars, and there's 2 chars of spacer '00's.
		int numPieces = (length - 2) / 4;
		boolean isBlack;
		
		
		if (line.subSequence(0, 2).equals("P+"))
			isBlack = true;
		else if (line.subSequence(0, 2).equals("P-"))
			isBlack = false;
		else
			throw new IllegalArgumentException("The given line does not seem to be a piece in hand line");
		
		for (int i = 0 ; i < numPieces ; i++)
		{
			CharSequence pieceAbbr = line.subSequence(4 + 4 * i, 6 + 4 * i);
			String fullPieceAbbr = (isBlack?"+":"-") + pieceAbbr;
			
			Log.d("Board", "trying to parse: " + fullPieceAbbr);
			Piece piece = null;
			try
			{
				piece = Piece.from3CharCode(fullPieceAbbr);
			}
			catch (Exception e) {
				Log.d("Board", "parse exception");
			}
			if (isBlack)
				board.blackHand.add(piece);
			else
				board.whiteHand.add(piece);
		}
			
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
		WPawn           ("Pawn"            ,"","gfu"  ,"-FU", FORWARD_ONE),
		WPromotedPawn   ("Promoted Pawn"   ,"","gto"  ,"-TO", GOLD_MOVEMENT),
		WLance          ("Lance"           ,"","gkyo" ,"-KY", FORWARD_SLIDE),
		WPromotedLance  ("Promoted Lance"  ,"","gnkyo","-NY", GOLD_MOVEMENT),
		WKnight         ("Knight"          ,"","gkei" ,"-KE", FORWARD_LEFT_KNIGHT | FORWARD_RIGHT_KNIGHT),
		WPromotedKnight ("Promoted Knight" ,"","gnkei","-NK", GOLD_MOVEMENT),
		WSilverGeneral  ("Silver General"  ,"","ggin" ,"-GI", SILVER_MOVEMENT),
		WPromotedSilver ("Promoted Silver" ,"","gngin","-NG", GOLD_MOVEMENT),
		WGoldGeneral    ("Gold General"    ,"","gkin", "-KI", GOLD_MOVEMENT),
		WBishop         ("Bishop"          ,"","gkaku","-KA", BISHOP_MOVEMENT),
		WDragonHorse    ("Dragon Horse"    ,"","guma" ,"-UM", BISHOP_MOVEMENT | KING_MOVEMENT),
		WRook           ("Rook"            ,"","ghi"  ,"-HI", ROOK_MOVEMENT),
		WDragonKing     ("Dragon King"     ,"","gryu" ,"-RY", ROOK_MOVEMENT | KING_MOVEMENT),
		WKing           ("King"            ,"","gou"  ,"-OU", KING_MOVEMENT),
		BPawn           ("Pawn"            ,"","sfu"  ,"+FU", FORWARD_ONE),
		BPromotedPawn   ("Promoted Pawn"   ,"","sto"  ,"+TO", GOLD_MOVEMENT),
		BLance          ("Lance"           ,"","skyo" ,"+KY", FORWARD_SLIDE),
		BPromotedLance  ("Promoted Lance"  ,"","snkyo","+NY", GOLD_MOVEMENT),
		BKnight         ("Knight"          ,"","skei" ,"+KE", FORWARD_LEFT_KNIGHT | FORWARD_RIGHT_KNIGHT),
		BPromotedKnight ("Promoted Knight" ,"","snkei","+NK", GOLD_MOVEMENT),
		BSilverGeneral  ("Silver General"  ,"","sgin" ,"+GI", SILVER_MOVEMENT),
		BPromotedSilver ("Promoted Silver" ,"","sngin","+NG", GOLD_MOVEMENT),
		BGoldGeneral    ("Gold General"    ,"","skin", "+KI", GOLD_MOVEMENT),
		BBishop         ("Bishop"          ,"","skaku","+KA", BISHOP_MOVEMENT),
		BDragonHorse    ("Dragon Horse"    ,"","suma" ,"+UM", BISHOP_MOVEMENT | KING_MOVEMENT),
		BRook           ("Rook"            ,"","shi"  ,"+HI", ROOK_MOVEMENT),
		BDragonKing     ("Dragon King"     ,"","sryu" ,"+RY", ROOK_MOVEMENT | KING_MOVEMENT),
		BKing           ("King"            ,"","sou"  ,"+OU", KING_MOVEMENT),
		Empty           ("Empty","",""," * ",0);
		

		
		public final String englishName, japName, shortJapName, japAbbr;
		public final int movementCapabilities;
		
		Piece(String englishName, String japName,String shortJapName,String japAbbr, int movementCapabilities)
		{
			this.englishName = englishName;
			this.japName = japName;
			this.shortJapName = shortJapName;
			this.japAbbr = japAbbr;
			this.movementCapabilities = movementCapabilities;
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

