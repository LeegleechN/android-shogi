#include "bonanza.h"
#include "com_stelluxstudios_Shogi_Engine.h"
#include <android/log.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
  
  JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_newGame
  (JNIEnv * env, jobject caller)
  {
     tree_t* ptree = &tree;
     int ret = ini(ptree);
     char buf[128];
     sprintf(buf,"cmd_new: ret %d",ret);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     return;
  }
  
  JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_makeMove
  (JNIEnv * env, jobject caller)
  {  
    tree_t* ptree = &tree;
     int ret = com_turn_start(ptree,0);
     char buf[128];
     sprintf(buf,"com_turn_start: ret %d",ret);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     
      if (game_status & flag_quit)
    {
      __android_log_write(ANDROID_LOG_ERROR,"bonanza","wtf quitting");  
      __android_log_write(ANDROID_LOG_ERROR,"bonanza",str_error==NULL?"null":str_error);  
      return -2;
    }
    
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
     /*
     int fd = open("/proc/sys/vm/drop_caches",O_WRONLY);
        sprintf(buf,"opened fd: %d",fd);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     char* str = "3";
     int len = strlen(str);
     int written = write(fd,str,len);
     sprintf(buf,"wrote: %d",written);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     close(fd);
     */
     return ret;
  }
  
  JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_getBoardString
  (JNIEnv * env, jobject caller)
  {
     tree_t* ptree = &tree;
    FILE* f = fopen("/sdcard/Android/com.stelluxstudios.Shogi/board_out.txt","w");
    out_board(ptree, f, 0, 0);
    fclose(f);
  }
