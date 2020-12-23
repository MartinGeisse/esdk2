
#include "divrem.h"
#include "term.h"

unsigned int * const SCREEN = (const unsigned int *)0x01000000;
static int x, y;

void termInitialize() {
    x = y = 0;
}

void termPrintChar(char c) {
    SCREEN[(y << 7) + x] = c;
    x++;
    if (x == 64) { // my display cuts off the right part of the screen, so 512 instead of 640
        x = 0;
        y++;
    }
}

void termPrintString(const char *s) {
    while (1) {
        char c = *s;
        if (c == 0) {
            return;
        }
        termPrintChar(c);
        s++;
    }
}

void termPrintInt(int i) {
    if (i < 0) {
        termPrintChar('-');
        i = -i;
    }
    termPrintUnsignedInt(i);
}

void termPrintUnsignedInt(unsigned int i) {

    // special case: since we suppress leading zeroes, actual zero would be invisible without this
    if (i == 0) {
        termPrintChar('0');
        return;
    }

    // start with the first digit (could be optimized)
    int significance = 1000000000;

    // print digits
    int started = 0;
    while (significance > 0) {
        int digit = udiv(i, significance);
        if (started || digit != 0) {
            termPrintChar((char)('0' + digit));
            started = 1;
        }
        i -= digit * significance;
        significance = udiv(significance, 10);
    }

}

void termPrintHexInt(int i) {
    if (i < 0) {
        termPrintChar('-');
        i = -i;
    }
    termPrintUnsignedHexInt(i);
}

void termPrintUnsignedHexInt(unsigned int i) {
    int shiftAmount = 28;
    while (shiftAmount >= 0) {
        int digit = i >> shiftAmount;
        termPrintChar(digit < 10 ? ('0' + digit) : ('a' + digit - 10));
        i = i & ((1 << shiftAmount) - 1);
        shiftAmount -= 4;
    };
}

void termPrintln() {
    x = 0;
    y++;
}

void termPrintlnString(const char *s) {
    termPrintString(s);
    termPrintln();
}

void termPrintlnChar(char c) {
    termPrintChar(c);
    termPrintln();
}

void termPrintlnInt(int i) {
    termPrintInt(i);
    termPrintln();
}

void termPrintlnUnsignedInt(unsigned int i) {
    termPrintUnsignedInt(i);
    termPrintln();
}

void termPrintlnHexInt(int i) {
    termPrintHexInt(i);
    termPrintln();
}

void termPrintlnUnsignedHexInt(unsigned int i) {
    termPrintUnsignedHexInt(i);
    termPrintln();
}
