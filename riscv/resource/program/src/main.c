
#include "terminal.h"

void main() {
/*
    terminalWrite("Hello world!\n");
    terminalWrite("This works.\n");
    terminalWriteInt(123);
    terminalWrite("\n");
    terminalWriteInt(1234567890);
    terminalWrite("\n");
    terminalWriteHex(0xdeadbeef);
    terminalWrite("\n");
    terminalWriteHex(32);
    terminalWrite("\n");
*/

    // TODO this is wrong (+ should be *), but why to all three lines show 35!?
    // fix this first (big RAM probably missing from this branch), then fix + to *
    // and test big RAM size
    int *basePointer = (int*)0x80000000;
    int *p1 = basePointer;
    int *p2 = basePointer + 8 * 1024 + 1024;
    int *p3 = basePointer + 16 * 1024 + 1024;

    *p1 = 15;
    *p2 = 25;
    *p3 = 35;

    terminalWriteHex(p1);
    terminalWrite("\n");
    terminalWriteHex(p2);
    terminalWrite("\n");
    terminalWriteHex(p3);
    terminalWrite("\n");

}
