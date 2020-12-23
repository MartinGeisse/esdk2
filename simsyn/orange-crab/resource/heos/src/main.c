
#include "term.h"

volatile unsigned int * const KEYBOARD = (unsigned int * const)0x02000000;

static void printHexDigit(int digit) {
    if (digit < 10) {
        termPrintChar(digit + '0');
    } else {
        termPrintChar(digit - 10 + 'a');
    }
}

int main(void) {
    termInitialize();
    termPrintlnString("Hello, world!");
    termPrintlnString("another line");
    while (1) {
        unsigned int code = *KEYBOARD;
        if (code != 0) {
            printHexDigit(code >> 4);
            printHexDigit(code & 15);
            termPrintChar(' ');
            termPrintChar(' ');
        }
    }
    return 0;
}
