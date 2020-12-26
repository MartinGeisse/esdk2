
#include "term.h"
#include "display_signal_logger.h"

void delay(int n);

/*
static volatile unsigned int * const KEYBOARD = (unsigned int * const)0x02000000;

static void printHexDigit(int digit) {
    if (digit < 10) {
        termPrintChar(digit + '0');
    } else {
        termPrintChar(digit - 10 + 'a');
    }
}
*/

static volatile unsigned int * const SIGNAL_LOGGER = (unsigned int * const)0x04000000;

int main(void) {
    termInitialize();

//    termPrintlnString("Hello, world!");
//    termPrintlnString("another line");
//    while (1) {
//        unsigned int code = *KEYBOARD;
//        if (code != 0) {
//            printHexDigit(code >> 4);
//            printHexDigit(code & 15);
//            termPrintChar(' ');
//            termPrintChar(' ');
//        }
//    }

    // log some dummy values
    *SIGNAL_LOGGER = 1; // reset
    *SIGNAL_LOGGER = 8; // activate
    delay(100);

    // display logged values
    displaySignalLoggerMain();

    return 0;
}
