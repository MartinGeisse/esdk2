
#include "system.h"
#include "terminal.h"

void main() {

//        resetLogPointer = busWriteData[0];
//        resetReadPointer = busWriteData[1];
//        clearMode = busWriteData[2];
//        loggingActive = busWriteData[3];






/*
    // wait for SDRAM reset
    delay(500);

    // log some data
    int *basePointer = (int*)0x80000000;
    volatile int *signalLogger = (int*)0x8000;
    *signalLogger = 8;
    *basePointer = 15;
    *signalLogger = 0;

    // write log to screen
    for (int i = 0; i < 64; i++) {
        terminalWriteHex(*signalLogger);
        if ((i & 3) == 3) {
            terminalWrite("\n");
        } else {
            terminalWrite(" ");
        }
    }
*/











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
        value += 10;
    }
    terminalWrite("\n\n");
    pointer = basePointer;
    for (int i = 0; i < 10; i++) {
        if ((i & 3) == 0) {
            terminalWrite("\n");
        }
        terminalWriteInt(pointer[i]);
        terminalWrite(" ");
    }

    // write log to screen
    volatile int *signalLogger = (int*)0x8000;
    for (int i = 0; i < 64; i++) {
        terminalWriteHex(*signalLogger);
        if ((i & 3) == 3) {
            terminalWrite("\n");
        } else {
            terminalWrite(" ");
        }
    }









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
