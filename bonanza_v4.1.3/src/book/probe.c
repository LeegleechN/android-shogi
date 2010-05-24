/*
 * probe.c
 * Copyright (C) 2004 - 2006 Kunihito Hoki
 * Version 2.0 - May 12 2006
 */

#include <stdio.h>
#include <stdlib.h>

#if defined(_MSC_VER)
#  define PRIx64        "I64x"
#  define UINT64_C(u)  ( u ## U )
typedef unsigned __int64 uint64_t;
typedef unsigned __int16 uint16_t;
#else
#  include <inttypes.h>
#endif

#include "key.h"

#define NUM_SECTION       0x4000
#define SIZE_SECTION      0x10000
#define SIZE_MOVE_ENTRY   4
#define SIZE_INDEX_ENTRY  6
#define SIZE_HEADER_ENTRY 9
#define MAX_LEGAL_MOVE    1024
#define BLACK             0
#define WHITE             1

#define PAWN              1
#define LANCE             2
#define KNIGHT            3
#define SILVER            4
#define GOLD              5
#define BISHOP            6
#define ROOK              7
#define KING              8
#define PRO_PAWN          9
#define PRO_LANCE         10
#define PRO_KNIGHT        11
#define PRO_SILVER        12
#define HORSE             13
#define DRAGON            14

static uint64_t hash_func( int *pflipped );
static int book_read( FILE *pf_book, uint64_t key, uint16_t *pmove,
		      uint16_t *pfreq );
static void bm2move( unsigned int book_move, int turn, int flipped,
		     unsigned int *pfrom, unsigned int *pto,
		     unsigned int *ppromo );

static signed char board[81] = {
  -LANCE, -KNIGHT, -SILVER, -GOLD, -KING, -GOLD, -SILVER, -KNIGHT, -LANCE,
  0, -ROOK, 0, 0, 0, 0, 0, -BISHOP, 0,
  -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN,
  0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0,
  PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN,
  0, BISHOP, 0, 0, 0, 0, 0, ROOK, 0,
  LANCE, KNIGHT, SILVER, GOLD, KING, GOLD, SILVER, KNIGHT, LANCE };

static int hand_pawn[2]   = { 0, 0 };
static int hand_lance[2]  = { 0, 0 };
static int hand_knight[2] = { 0, 0 };
static int hand_silver[2] = { 0, 0 };
static int hand_gold[2]   = { 0, 0 };
static int hand_bishop[2] = { 0, 0 };
static int hand_rook[2]   = { 0, 0 };

static int turn = BLACK;

int
main( void )
{
  uint16_t amove[ MAX_LEGAL_MOVE ];
  uint16_t afreq[ MAX_LEGAL_MOVE ];
  uint64_t key;
  FILE *pf_book;
  unsigned int from, to, promo;
  int flipped, moves, i;

  pf_book = fopen( "book.bin", "r" );
  if ( pf_book == NULL )
    {
      fprintf( stderr, "can't open book.bin.\n" );
      exit(1);
    }

  key   = hash_func( &flipped );
  moves = book_read( pf_book, key, amove, afreq );
  if ( ! moves )
    {
      printf( "out of book\n" );
    }
  else for ( i = 0; i < moves; i++ )
    {
      bm2move( (unsigned int)amove[i], turn, flipped, &from, &to, &promo );
      printf( "%4.1f%% %d -> %d%s\n",
	      (double)afreq[i] * 100.0 / (double)0xffff,
	      from, to, promo ? " p" : "" );
    }

  fclose( pf_book );

  return EXIT_SUCCESS;
}


static uint64_t
hash_func( int *pflipped )
{
  uint64_t key, key_flip;
  int i, t, rank, file, piece, sq, sq_flip;

  key = 0;

  t = turn;
  i = hand_pawn[t];    if (i) { key ^= b_hand_pawn_rand[i-1]; }
  i = hand_lance[t];   if (i) { key ^= b_hand_lance_rand[i-1]; }
  i = hand_knight[t];  if (i) { key ^= b_hand_knight_rand[i-1]; }
  i = hand_silver[t];  if (i) { key ^= b_hand_silver_rand[i-1]; }
  i = hand_gold[t];    if (i) { key ^= b_hand_gold_rand[i-1]; }
  i = hand_bishop[t];  if (i) { key ^= b_hand_bishop_rand[i-1]; }
  i = hand_rook[t];    if (i) { key ^= b_hand_rook_rand[i-1]; }

  t = ( turn == BLACK ) ? WHITE : BLACK;
  i = hand_pawn[t];    if (i) { key ^= w_hand_pawn_rand[i-1]; }
  i = hand_lance[t];   if (i) { key ^= w_hand_lance_rand[i-1]; }
  i = hand_knight[t];  if (i) { key ^= w_hand_knight_rand[i-1]; }
  i = hand_silver[t];  if (i) { key ^= w_hand_silver_rand[i-1]; }
  i = hand_gold[t];    if (i) { key ^= w_hand_gold_rand[i-1]; }
  i = hand_bishop[t];  if (i) { key ^= w_hand_bishop_rand[i-1]; }
  i = hand_rook[t];    if (i) { key ^= w_hand_rook_rand[i-1]; }

  key_flip = key;

  for ( rank = 0; rank < 9; rank++ )
    for ( file = 0; file < 9; file++ )
      {
	if ( turn == BLACK )
	  {
	    sq      = rank * 9 + file;
	    sq_flip = rank * 9 + 8 - file;
	    piece   = (int)board[sq];
	  }
	else {
	  sq      = ( 8 - rank ) * 9 + 8 - file;
	  sq_flip = ( 8 - rank ) * 9 + file;
	  piece   = -(int)board[80-sq];
	}

	switch ( piece )
	  {
#define BLAH(x)  key      ^= ( x ## _rand ) [sq];     \
                 key_flip ^= ( x ## _rand ) [sq_flip];
	  case  PAWN:        BLAH( b_pawn );        break;
	  case  LANCE:       BLAH( b_lance );       break;
	  case  KNIGHT:      BLAH( b_knight );      break;
	  case  SILVER:      BLAH( b_silver );      break;
	  case  GOLD:        BLAH( b_gold );        break;
	  case  BISHOP:      BLAH( b_bishop );      break;
	  case  ROOK:        BLAH( b_rook );        break;
	  case  KING:        BLAH( b_king );        break;
	  case  PRO_PAWN:    BLAH( b_pro_pawn );    break;
	  case  PRO_LANCE:   BLAH( b_pro_lance );   break;
	  case  PRO_KNIGHT:  BLAH( b_pro_knight );  break;
	  case  PRO_SILVER:  BLAH( b_pro_silver );  break;
	  case  HORSE:       BLAH( b_horse );       break;
	  case  DRAGON:      BLAH( b_dragon );      break;
	  case -PAWN:        BLAH( w_pawn );        break;
	  case -LANCE:       BLAH( w_lance );       break;
	  case -KNIGHT:      BLAH( w_knight );      break;
	  case -SILVER:      BLAH( w_silver );      break;
	  case -GOLD:        BLAH( w_gold );        break;
	  case -BISHOP:      BLAH( w_bishop );      break;
	  case -ROOK:        BLAH( w_rook );        break;
	  case -KING:        BLAH( w_king );        break;
	  case -PRO_PAWN:    BLAH( w_pro_pawn );    break;
	  case -PRO_LANCE:   BLAH( w_pro_lance );   break;
	  case -PRO_KNIGHT:  BLAH( w_pro_knight );  break;
	  case -PRO_SILVER:  BLAH( w_pro_silver );  break;
	  case -HORSE:       BLAH( w_horse );       break;
	  case -DRAGON:      BLAH( w_dragon );      break;
#undef BLAH
	  }
      }

  if ( key > key_flip )
    {
      key       = key_flip;
      *pflipped = 1;
    }
  else { *pflipped = 0; }

  return key;
}


static int
book_read( FILE *pf_book, uint64_t key, uint16_t *pmove, uint16_t *pfreq )
{
  static unsigned char book_section[ SIZE_SECTION ];

  uint64_t book_key;
  const unsigned char *p;
  unsigned int position, size_section, size, u;
  int ibook_section, moves;
  uint16_t u16;

  ibook_section = (int)( (unsigned int)key & (unsigned int)( NUM_SECTION-1 ) );

  if ( fseek( pf_book, SIZE_INDEX_ENTRY * ibook_section , SEEK_SET ) == EOF
       || fread( &position, sizeof(unsigned int), 1, pf_book ) != 1
       || fread( &u16, sizeof(uint16_t), 1, pf_book ) != 1 )
    {
      fprintf( stderr, "ERROR: line %d\n", __LINE__ );
      exit( 1 );
    }
  size_section = (unsigned int)u16;

  if ( fseek( pf_book, (long)position, SEEK_SET ) == EOF
       || fread( book_section, sizeof(unsigned char), (size_t)size_section,
		 pf_book ) != (size_t)size_section )
    {
      fprintf( stderr, "ERROR: line %d\n", __LINE__ );
      exit( 1 );
    }
  
  size = 0;
  p    = book_section;
  while ( book_section + size_section > p )
    {
      size     = (unsigned int)p[0];
      book_key = *(uint64_t *)( p + 1 );
      if ( book_key == key ) { break; }
      p       += size;
    }
  if ( book_section + size_section <= p ) { return 0; } /* out of book */

  for ( moves = 0, u = SIZE_HEADER_ENTRY; u < size;
	moves++, u += SIZE_MOVE_ENTRY )
    {
      pmove[moves] = *(unsigned short *)(p+u+0);
      pfreq[moves] = *(unsigned short *)(p+u+2);
    }

  return moves;
}


static void
bm2move( unsigned int book_move, int turn, int flipped,
	 unsigned int *pfrom, unsigned int *pto, unsigned int *ppromo )
{
  unsigned int rank_to, rank_from, file_to, file_from;

  *pto    = 0x007fU & book_move;
  *pfrom  = 0x007fU & ( book_move >> 7 );
  *ppromo = ( 1U << 14 ) & book_move;

  rank_to = *pto / 9U;
  file_to = *pto % 9U;
  if ( *pfrom < 81 )
    {
      rank_from = *pfrom / 9U;
      file_from = *pfrom % 9U;
    }
  else { rank_from = file_from = 0; }

  if ( turn == WHITE )
    {
      rank_to = 8 - rank_to;
      file_to = 8 - file_to;
      if ( *pfrom < 81 )
	{
	  rank_from = 8 - rank_from;
	  file_from = 8 - file_from;
	}
    }

  if ( flipped )
    {
      file_to = 8 - file_to;
      if ( *pfrom < 81 ) { file_from = 8 - file_from; }
    }

  
  *pto = ( 9 - file_to ) * 10 + rank_to + 1;
  if ( *pfrom < 81 )
    {
      *pfrom = ( 9 - file_from ) * 10 + rank_from + 1;
    }
  else { *pfrom = 100 + *pfrom - 80; }
}
