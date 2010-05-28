#include <bonanza.h>
#include <stdlib.h>

int main()
{
  char* point = NULL;
  tree_t* tree = (tree_t*)malloc(sizeof(tree_t));
  int init = ini(tree);
  cmd_move(tree,&point);
  fin();
}
