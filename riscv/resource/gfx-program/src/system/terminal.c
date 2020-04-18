
#include "cpu.h"
#include "draw.h"

static int x, y;

void termInitialize() {
    x = y = 0;
}

void termPrintString(const char *s) {
    while (1) {
        char c = *s;
        if (c == 0) {
            return;
        }
        drawCharacter(x, y, c);
        x += 8;
        s++;
    }
}

void termPrintChar(char c) {
    drawCharacter(x, y, c);
    x += 8;
}

void termPrintInt(int i) {
    if (i < 0) {
        termPrintChar('-');
        i = -i;
    }
    termPrintUnsignedInt(i);
}

void termPrintUnsignedInt(unsigned int i) {

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
    y += 16;
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
