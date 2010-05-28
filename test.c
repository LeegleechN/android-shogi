#include <bonanza.h>
#include <stdlib.h>

int main()
{
  char* point = NULL;
  tree_t* ptree = &tree;
  int init = ini(ptree);
  cmd_move(ptree,&point);
  fin();
}
