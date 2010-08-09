#ifndef BONANZA_H
#define BONANZA_H
#include "shogi.h"
int proce_cui( tree_t * restrict ptree );
int cmd_move_now( void );
int cmd_ponder( char **lasts );
int cmd_limit( char **lasts );
int cmd_quit( void );
int cmd_beep( char **lasts );
int cmd_peek( char **lasts );
int cmd_hash( char **lasts );
int cmd_ping( void );
int cmd_suspend( void );
int cmd_problem( tree_t* ptree, char **lasts );
int cmd_display( tree_t* ptree, char **lasts );
int cmd_move( tree_t* ptree, char **lasts );
int cmd_new( tree_t* ptree, char **lasts );
int cmd_read( tree_t* ptree, char **lasts );
int cmd_resign( tree_t* ptree, char **lasts );
int cmd_time( char **lasts );
int is_move( const char *str );
//int ini( tree_t * ptree );
//int fin( void );
#endif /* BONANZA_H */
