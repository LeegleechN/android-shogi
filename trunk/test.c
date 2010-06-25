#include <bonanza.h>
#include <stdlib.h>

int main()
{
  char* point = NULL;
  tree_t* ptree = &tree;
  int init = ini(ptree);
  
  int ret;
  for (int i = 0 ; i < 300 ; i++)
  {
    ret = com_turn_start(ptree,0);
    if (game_status & flag_resigned)
      break;
  }
  fin();
}
