
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
    while (1) {

        // clear screen
        termInitialize();
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 80; x++) {
                SCREEN[(y << 7) + x] = ' ';
            }
        }

        // show menu
        termPrintlnString("F1 - keycodes");
        termPrintlnString("F2 - signal logger");
        termPrintlnString("ESC - return to this menu");
        termPrintln();

        // wait for kepress
        unsigned int code;
        do {
            code = *KEYBOARD;
        } while (code == 0);
        resetKeyStates();
        switch (code) {

            case 0x05:
                keycodeLoop();
                continue;

            case 0x06:
                *SIGNAL_LOGGER = 1; // reset
                *SIGNAL_LOGGER = 8; // activate
                delay(100);
                displaySignalLoggerMain();
                continue;

        }

    }

}
