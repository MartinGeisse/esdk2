
#include "system.h"
#include "terminal.h"

void main() {

    //
    terminalWriteChar('\n');
    terminalWriteChar('\n');
    terminalWrite("Hello world!\n");
    terminalWrite("FOO BAR FUPP\n");
    for (int i = 0; i < 80; i++) {
        terminalPlaceChar(i, 0, 1);
        terminalPlaceChar(i, 29, 1);
    }
    for (int i = 0; i < 30; i++) {
        terminalPlaceChar(0, i, 1);
        terminalPlaceChar(79, i, 1);
    }

    // wait for SDRAM reset
    delay(500);
    int *basePointer = (int*)0x80000000;
    int *endPointer = basePointer + 16 * 1024 * 1024;

    {
        int value = 9;
        for (int *p = basePointer; p < endPointer; p++) {

            // write test value to SDRAM
            *p = value;

            // newValue = oldValue * 5 + 1
            value = (value << 2) + value + 1;

            // show progress
            if ((((int)p) & 0x007fffff) == 0) {
                terminalWriteChar('.');
            }

        }
    }
    terminalWriteChar('\n');

    {
        int value = 9;
        for (int *p = basePointer; p < endPointer; p++) {

            // read back test value from SDRAM
            if (*p != value) {
                terminalWriteChar('E');
            }

            // newValue = oldValue * 5 + 1
            value = (value << 2) + value + 1;

            // show progress
            if ((((int)p) & 0x007fffff) == 0) {
                terminalWriteChar('.');
            }

        }
    }
    terminalWriteChar('\n');

}
