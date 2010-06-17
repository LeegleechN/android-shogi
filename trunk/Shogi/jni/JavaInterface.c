#include "bonanza.h"
#include "com_stelluxstudios_Shogi_Engine.h"
#include <android/log.h>

JNIEXPORT jint JNICALL Java_com_stelluxstudios_Shogi_Engine_magicNumber
  (JNIEnv * env, jobject caller)
  {
    return 57357;
  }
  
  JNIEXPORT void JNICALL Java_com_stelluxstudios_Shogi_Engine_newGame
  (JNIEnv * env, jobject caller)
  {
     tree_t* ptree = &tree;
     int ret = ini(ptree);
     char buf[128];
     sprintf(buf,"initialized ptree: ret %d",ret);
     __android_log_write(ANDROID_LOG_ERROR,"bonanza",buf);
     return;
  }
