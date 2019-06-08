
#include "system.h"
#include "terminal.h"

void main() {

    // wait for SDRAM reset
    delay(500);
    int *basePointer = (int*)0x80000000;

    int *p1 = basePointer;
    int *p2 = basePointer + 8 * 1024 * 1024;
    int *p3 = basePointer + 16 * 1024 * 1024;

    *p1 = 15;
    *p2 = 25;
    *p3 = 35;

    terminalWriteInt(*p1);
    terminalWrite("\n");
    terminalWriteInt(*p2);
    terminalWrite("\n");
    terminalWriteInt(*p3);
    terminalWrite("\n");

}
