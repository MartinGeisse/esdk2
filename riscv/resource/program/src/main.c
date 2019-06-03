
#include "system.h"
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

    int *basePointer = (int*)0x80000000;

    // wait for SDRAM reset
    delay(500);

    int *pointer = basePointer;
    int value = 5;
    for (int i = 0; i < 10; i++) {
        if ((i & 3) == 0) {
            terminalWrite("\n");
        }
        terminalWriteInt(value);
        terminalWrite(" ");
        pointer[i] = value;
        pointer += 1; // TODO this is wrong because we already use indexing, but it doesn't explain the current error
        value += 10;
    }
    terminalWrite("\n");
    terminalWrite("\n");
    pointer = basePointer;
    for (int i = 0; i < 10; i++) {
        if ((i & 3) == 0) {
            terminalWrite("\n");
        }
        terminalWriteInt(pointer[i]);
        terminalWrite(" ");
        pointer += 1; // TODO this is wrong because we already use indexing, but it doesn't explain the current error
    }

    terminalWrite("\nPROGRAM DONE\n");

    /*
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
    */

    // counter to test reset
    /*
    terminalSetCursor(0, 0);
    terminalWrite("                ");
    int counter = 0;
    while (1) {
        terminalSetCursor(0, 0);
        terminalWriteInt(counter);
        counter++;
        delay(1);
    }
    */

}
