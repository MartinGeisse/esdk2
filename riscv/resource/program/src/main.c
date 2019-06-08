
#include "system.h"
#include "terminal.h"

void main() {

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
