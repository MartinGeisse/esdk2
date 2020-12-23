
#include "term.h"

int main(void) {
    termInitialize();
    termPrintlnString("Hello, world!");
    termPrintlnString("another line");
    return 0;
}
