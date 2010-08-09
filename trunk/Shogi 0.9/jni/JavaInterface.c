#include "bonanza.h"
#include "com_stelluxstudios_Shogi_Engine.h"
#include <android/log.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
  
   JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_initialize
  (JNIEnv * env, jobject caller)
  {
     tree_t* ptree = &tree;
     int ret = ini(ptree);
     return;
  }
  
  JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_newGame
  (JNIEnv * env, jobject caller, jbyteArray handicap)
  {
    int iret;
     tree_t* ptree = &tree;
      min_posi_t min_posi;
      memset( &min_posi.asquare, empty, nsquare );
      min_posi.hand_black = min_posi.hand_white = 0;
      min_posi.turn_to_move = black;
      
      jbyte* ptr = (*env)->GetByteArrayElements(env,handicap,NULL);
      iret = read_board_rep1( ptr, &min_posi );
      (*env)->ReleaseByteArrayElements(env,handicap,ptr,0);
      if ( iret < 0 )
      {
           __android_log_write(ANDROID_LOG_ERROR,"bonanza","FAILED TO INIT GAME!");
        return;
       }
      iret = ini_game(ptree,&min_posi,flag_time,"Player1","Player2");
     return;
  }
  
  
  
  JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_tryApplyMove
  (JNIEnv * env, jobject caller, jbyteArray str)
  {
	jbyte* ptr = (*env)->GetByteArrayElements(env,str,NULL);
  
	tree_t* ptree = &tree;
  	unsigned int move;
	int iret = interpret_CSA_move( ptree, &move, ptr );
	(*env)->ReleaseByteArrayElements(env,str,ptr,0);
	
	if ( iret < 0 ) { return iret; }

	iret = make_move_root( ptree, move, ( flag_history | flag_time | flag_rep
					  | flag_detect_hang
					  | flag_rejections ) );
    if ( iret < 0 ) { return iret; }	
	
	
	
	return 0;
  }
  
  
  JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_makeMove
  (JNIEnv * env, jobject caller)
  {  
    tree_t* ptree = &tree;
     int ret = com_turn_start(ptree,0);
     char buf[128];
     sprintf(buf,"com_turn_start: ret %d",ret);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     
    if (game_status & flag_mated)
    {
      __android_log_write(ANDROID_LOG_ERROR,"bonanza","mated");  
      return 18;
    }
    if (game_status & flag_resigned)
    {  __android_log_write(ANDROID_LOG_ERROR,"bonanza","resigned");
      if (root_turn) //white resigned
      	return 19;
      else
      	return 20; //black resigned
    }
    if (game_status & flag_drawn)
    {  __android_log_write(ANDROID_LOG_ERROR,"bonanza","drawn");
      return 21;
    }
    if (game_status & flag_suspend)
    {  __android_log_write(ANDROID_LOG_ERROR,"bonanza","suspend");
      return 22;
    }
    
	if (game_status & flag_quit)
    {
      __android_log_write(ANDROID_LOG_ERROR,"bonanza","wtf quitting");  
      __android_log_write(ANDROID_LOG_ERROR,"bonanza",str_error==NULL?"no error string":str_error);  
	  //try just cancelling the quit
	  game_status &= ~flag_quit;
	  return ret;
      //return -2;
    }
     return ret;
  }
  
   JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_getGameStatus
  (JNIEnv * env, jobject caller)
  {
	if (game_status & flag_quit)
      return -2;
    
    if (game_status & flag_mated)
      return 18;
    if (game_status & flag_resigned)
    {
      if (root_turn) //white resigned
      	return 19;
      else
      	return 20; //black resigned
    }
    if (game_status & flag_drawn)
      return 21;
	  
    if (game_status & flag_suspend)
      return 22;
  }
  
    JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_getCurrentPlayer
  (JNIEnv * env, jobject caller)
  {
	//0 indicates black, 1 indicates white
	return root_turn;
  }
  
  JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_getBoardString
  (JNIEnv * env, jobject caller)
  {
     tree_t* ptree = &tree;
    FILE* f = fopen("/sdcard/Android/data/com.stelluxstudios.Shogi/files/board_out.txt","w");
    out_board(ptree, f, 0, 0);
    fclose(f);
  }
  
  JNIEXPORT  jint JNICALL Java_com_stelluxstudios_Shogi_Engine_saveToFile
  (JNIEnv * env, jobject caller)
  {
	//char* filename_c = (*env)->GetStringUTFChars(env,filename_java,NULL);
	char* filename_c = "/sdcard/Android/data/com.stelluxstudios.Shogi/files/save.csa";
	//__android_log_write(ANDROID_LOG_ERROR,"savefile",filename_c);  
  
	tree_t* ptree = &tree;
	//record_t* record = malloc(sizeof(record));
	record_t record;
	int iret = record_open( &record, filename_c, mode_write, NULL, NULL);
			//__android_log_write(ANDROID_LOG_ERROR,"savefile","record open returned");  
	//(*env)->ReleaseStringUTFChars(env,filename_java,filename_c);
	//		__android_log_write(ANDROID_LOG_ERROR,"savefile","byte array elements released");  
	if (iret < 0)
	{
	  		//__android_log_write(ANDROID_LOG_ERROR,"savefile","record open failed");  
	  //free(record);
	  		//__android_log_write(ANDROID_LOG_ERROR,"savefile","record freed");  
		return iret;
	}
	//WARNING: out_CSA_header currently has no error handling
	out_CSA_header( ptree, &record);
			//__android_log_write(ANDROID_LOG_ERROR,"savefile","out_csa_header returned");  
	iret = record_close(&record);
			//__android_log_write(ANDROID_LOG_ERROR,"savefile","record closed");  
	
	//free(record);
			//__android_log_write(ANDROID_LOG_ERROR,"savefile","record freed");  
	return iret;
  }
  
  JNIEXPORT  jint JNICALL Java_com_stelluxstudios_Shogi_Engine_loadFromFile
  (JNIEnv * env, jobject caller)
  {
	//char* filename_c = (*env)->GetStringUTFChars(env,filename_java,NULL);
	char* filename_c = "/sdcard/Android/data/com.stelluxstudios.Shogi/files/save.csa";

	tree_t* ptree = &tree;
	int flag = flag_history | flag_rep | flag_detect_hang | flag_rejections;
	int iret = read_record(ptree, filename_c, INT_MAX, flag );
	//(*env)->ReleaseStringUTFChars(env,filename_java,filename_c);
	return iret;
  }
