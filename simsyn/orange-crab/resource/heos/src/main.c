
#include "term.h"
#include "display_signal_logger.h"

void delay(int n);

static volatile unsigned int * const SCREEN = (unsigned int *)0x01000000;
static volatile unsigned int * const KEYBOARD = (unsigned int * const)0x02000000;
static volatile unsigned int * const SIGNAL_LOGGER = (unsigned int * const)0x04000000;

static void printHexDigit(int digit) {
    if (digit < 10) {
        termPrintChar(digit + '0');
    } else {
        termPrintChar(digit - 10 + 'a');
    }
}

static void keycodeLoop() {
    while (1) {
        unsigned int code = *KEYBOARD;
        if (code != 0) {
            printHexDigit(code >> 4);
            printHexDigit(code & 15);
            termPrintChar(' ');
            termPrintChar(' ');
        }
        if (code == 0x76) {
            termPrintln();
            return;
        }
    }
}

int main(void) {

    delay(1000);
    *(unsigned int *)0x80000000 = 0x12345678;
    *(unsigned int *)0x80000004 = 0x1bc8390c;
    *(unsigned int *)0x80000008 = 0x00000000;
    *(unsigned int *)0x8000000c = 0xffffffff;
    // *(unsigned int *)0x80000008 = 0x01010202;
    unsigned int readValue1 = *(unsigned int *)0x80000000;
    unsigned int readValue2 = *(unsigned int *)0x80000004;
    unsigned int readValue3 = *(unsigned int *)0x80000008;
    unsigned int readValue4 = *(unsigned int *)0x8000000c;

    while (1) {

        // clear screen
        termInitialize();
//        for (int y = 0; y < 30; y++) {
//            for (int x = 0; x < 80; x++) {
//                SCREEN[(y << 7) + x] = ' ';
//            }
//        }

        // show menu
        termPrintString("readValue1: ");
        // termPrintlnUnsignedHexInt(readValue1);
        termPrintlnUnsignedHexInt(*(unsigned int *)0x80000000);
        termPrintString("readValue2: ");
        // termPrintlnUnsignedHexInt(readValue2);
        termPrintlnUnsignedHexInt(*(unsigned int *)0x80000004);
        termPrintString("readValue3: ");
        // termPrintlnUnsignedHexInt(readValue3);
        termPrintlnUnsignedHexInt(*(unsigned int *)0x80000008);
        termPrintString("readValue4: ");
        // termPrintlnUnsignedHexInt(readValue4);
        termPrintlnUnsignedHexInt(*(unsigned int *)0x8000000c);

        termPrintlnString("F1 - keycodes");
        termPrintlnString("F2 - signal logger");
        termPrintlnString("ESC - return to this menu");
        termPrintln();

        // wait for kepress
        unsigned int code;
        //do {
            code = *KEYBOARD;
        //} while (code == 0);
        resetKeyStates();
        switch (code) {

            case 0x05:
                keycodeLoop();
                continue;

            case 0x06:
                // *SIGNAL_LOGGER = 1; // reset
                // *SIGNAL_LOGGER = 8; // activate
                delay(100);
                displaySignalLoggerMain();
                continue;

        }

    }

}
