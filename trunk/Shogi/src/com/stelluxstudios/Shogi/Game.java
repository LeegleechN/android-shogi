package com.stelluxstudios.Shogi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.stelluxstudios.Shogi.MovementCalculator.Position;

public class Game {
	
	public enum Player{White,Black};
	private Piece[][] field = new Piece[9][9];
	private List<Piece> whiteHand = new ArrayList<Piece>(), 
						blackHand = new ArrayList<Piece>();
	
	private Player currentPlayer;
	
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
	
	
	public static Game makeNew()
	{
		Game b = Game.fromString(initialConfig);
		b.currentPlayer = Player.Black;
		return b;
	}
	
	public static Game fromString(String in)
	{
		Game b = new Game();
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
	
	private static void interpretHand(String line,Game board) {
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

	private Game()
	{
		for (int i = 0 ; i < 9 ; i++)
		{
			for (int j = 0 ; j <9 ; j++)
			{
				field[i][j] = Piece.Empty;
			}
		}
	}
	
	public static Game newBoard()
	{
		return new Game();
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
	
	public void setCurrentPlayer(Player p)
	{
		currentPlayer = p;
	}
	public Player getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	
	public List<Position> getValidMovesForPiece(int i, int j)
	{
		return MovementCalculator.getValidMoves(this, i, j);
	}
	
	
	public static enum Piece{
		WPromotedPawn   ("Promoted Pawn"   ,"",Player.White,"gto"  ,"-TO",null,			 	GOLD_MOVEMENT),
		WPawn           ("Pawn"            ,"",Player.White,"gfu"  ,"-FU",WPromotedPawn, 	FORWARD_ONE),
		WPromotedLance  ("Promoted Lance"  ,"",Player.White,"gnkyo","-NY",null, 		 	GOLD_MOVEMENT),
		WLance          ("Lance"           ,"",Player.White,"gkyo" ,"-KY",WPromotedLance,	FORWARD_SLIDE),
		WPromotedKnight ("Promoted Knight" ,"",Player.White,"gnkei","-NK",null, 		 	GOLD_MOVEMENT),
		WKnight         ("Knight"          ,"",Player.White,"gkei" ,"-KE",WPromotedKnight,	FORWARD_LEFT_KNIGHT | FORWARD_RIGHT_KNIGHT),
		WPromotedSilver ("Promoted Silver" ,"",Player.White,"gngin","-NG",null, 			GOLD_MOVEMENT),
		WSilverGeneral  ("Silver General"  ,"",Player.White,"ggin" ,"-GI",WPromotedSilver, 	SILVER_MOVEMENT),
		WGoldGeneral    ("Gold General"    ,"",Player.White,"gkin", "-KI",null, 			GOLD_MOVEMENT),
		WDragonHorse    ("Dragon Horse"    ,"",Player.White,"guma" ,"-UM",null, 			BISHOP_MOVEMENT | KING_MOVEMENT),
		WBishop         ("Bishop"          ,"",Player.White,"gkaku","-KA",WDragonHorse, 	BISHOP_MOVEMENT),
		WDragonKing     ("Dragon King"     ,"",Player.White,"gryu" ,"-RY",null, 			ROOK_MOVEMENT | KING_MOVEMENT),
		WRook           ("Rook"            ,"",Player.White,"ghi"  ,"-HI",WDragonKing, 		ROOK_MOVEMENT),
		WKing           ("King"            ,"",Player.White,"gou"  ,"-OU",null, 			KING_MOVEMENT),
		
		BPromotedPawn   ("Promoted Pawn"   ,"",Player.Black,"sto"  ,"+TO",null, 			GOLD_MOVEMENT),
		BPawn           ("Pawn"            ,"",Player.Black,"sfu"  ,"+FU",BPromotedPawn, 	FORWARD_ONE),
		BPromotedLance  ("Promoted Lance"  ,"",Player.Black,"snkyo","+NY",null, 			GOLD_MOVEMENT),
		BLance          ("Lance"           ,"",Player.Black,"skyo" ,"+KY",BPromotedLance, 	FORWARD_SLIDE),
		BPromotedKnight ("Promoted Knight" ,"",Player.Black,"snkei","+NK",null, 			GOLD_MOVEMENT),
		BKnight         ("Knight"          ,"",Player.Black,"skei" ,"+KE",BPromotedKnight, 	FORWARD_LEFT_KNIGHT | FORWARD_RIGHT_KNIGHT),
		BPromotedSilver ("Promoted Silver" ,"",Player.Black,"sngin","+NG",null, 			GOLD_MOVEMENT),
		BSilverGeneral  ("Silver General"  ,"",Player.Black,"sgin" ,"+GI",BPromotedSilver, 	SILVER_MOVEMENT),
		BGoldGeneral    ("Gold General"    ,"",Player.Black,"skin", "+KI",null, 			GOLD_MOVEMENT),
		BDragonHorse    ("Dragon Horse"    ,"",Player.Black,"suma" ,"+UM",null,				BISHOP_MOVEMENT | KING_MOVEMENT),
		BBishop         ("Bishop"          ,"",Player.Black,"skaku","+KA",BDragonHorse,		BISHOP_MOVEMENT),
		BDragonKing     ("Dragon King"     ,"",Player.Black,"sryu" ,"+RY",null, 			ROOK_MOVEMENT | KING_MOVEMENT),
		BRook           ("Rook"            ,"",Player.Black,"shi"  ,"+HI",BDragonKing, 		ROOK_MOVEMENT),
		BKing           ("King"            ,"",Player.Black,"sou"  ,"+OU",null, 			KING_MOVEMENT),
		Empty           ("Empty","",null,""," * ",null,0);
		

		
		public final String englishName, japName, shortJapName, japAbbr;
		public final int movementCapabilities;
		public final Player owner;
		public final Piece promoted;
		
		/*
		static
		{
			WPromotedPawn.dropped 	= BPawn; 			WPawn.dropped 			= BPawn;
			WPromotedLance.dropped 	= BLance; 			WLance.dropped 			= BLance;
			WPromotedKnight.dropped = BKnight;			WKnight.dropped			= BKnight;
			WPromotedSilver.dropped = BSilverGeneral; 	WSilverGeneral.dropped 	= BSilverGeneral;
			WGoldGeneral.dropped 	= BGoldGeneral;
			WDragonHorse.dropped 	= BBishop; 			WBishop.dropped 		= BBishop;
			WDragonKing.dropped 	= BRook; 			WRook.dropped 			= BRook;
			
			BPromotedPawn.dropped 	= WPawn; 			BPawn.dropped 			= WPawn;
			BPromotedLance.dropped 	= WLance; 			BLance.dropped 			= WLance;
			BPromotedKnight.dropped = WKnight; 			BKnight.dropped 		= WKnight;
			BPromotedSilver.dropped = WSilverGeneral; 	BSilverGeneral.dropped 	= WSilverGeneral;
			BGoldGeneral.dropped 	= WGoldGeneral;
			BDragonHorse.dropped 	= WBishop; 			BBishop.dropped 		= WBishop;
			BDragonKing.dropped 	= WRook; 			BRook.dropped 			= WRook;
		}*/
		
		Piece(String englishName, String japName,Player owner,String shortJapName,String japAbbr, Piece promoted, int movementCapabilities)
		{
			this.englishName = englishName;
			this.japName = japName;
			this.shortJapName = shortJapName;
			this.japAbbr = japAbbr;
			this.movementCapabilities = movementCapabilities;
			this.owner = owner;
			this.promoted = promoted;
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
		
		public Piece swapSides()
		{
			if (owner == Player.White)
				return from3CharCode('+' + japAbbr.substring(1));
			else
				return from3CharCode('-' + japAbbr.substring(1));
		}
	
	}
}

