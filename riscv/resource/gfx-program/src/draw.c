
#include "simdev.h"

#define SCREEN_WORDS ((int *)0x80000000)
#define SCREEN ((unsigned char *)0x80000000)

static unsigned char drawColor = 7;

void clearScreen(unsigned char color) {
    int isSimulation = simdevIsSimulation();
    int fourPixels = color | (color << 8) | (color << 16) | (color << 24);
    int *rowPointer = SCREEN_WORDS;
    int *screenEnd = rowPointer + 256 * 480;
    while (rowPointer < screenEnd) {
        if (isSimulation) {
            simdevFillWordsShowInt(rowPointer, fourPixels, 160);
        } else {
            int *pixelPointer = rowPointer;
            int *rowEnd = pixelPointer + 160;
            while (pixelPointer < rowEnd) {
                *pixelPointer = fourPixels;
                pixelPointer++;
            }
        }
        rowPointer += 256;
    }
}

void drawPixel(int x, int y, unsigned char color) {
    SCREEN[(y << 10) + x] = color;
}


static int mul(int x, int y) {
    int result = 0;
    for (int bit = 1; bit != 0; bit <<= 1) {
        if (y & bit) {
            result += x;
        }
        x <<= 1;
    }
    return result;
}

static unsigned int udivrem(unsigned int x, unsigned int y, int rem) {
    unsigned int quotient = 0, remainder = 0;
    for (int i = 0; i < 32; i++) {
        remainder = (remainder << 1) | (x >> 31);
        x = x << 1;
        quotient = (quotient << 1);
        if (remainder >= y) {
            remainder -= y;
            quotient++;
        }
    }
    return rem ? remainder : quotient;
}

static unsigned int udiv(unsigned int x, unsigned int y) {
    return udivrem(x, y, 0);
}

static unsigned int urem(unsigned int x, unsigned int y) {
    return udivrem(x, y, 1);
}

static int div(int x, int y) {
    int negative = 0;
    if (x < 0) {
        x = -x;
        negative = !negative;
    }
    if (y < 0) {
        y = -y;
        negative = !negative;
    }
    int result = udiv(x, y);
    return negative ? -result : result;
}

static void drawHalfTriangle(int x1a, int x1b, int y1, int x2, int y2, int dy) {

    // sort vertically aligned points by X
    if (x1a > x1b) {
        int temp = x1a;
        x1a = x1b;
        x1b = temp;
    }

    // draw loop
    int deltaXa = x2 - x1a;
    int deltaXb = x2 - x1b;
    int deltaY = y2 - y1;
    if (deltaY < 0) {
        deltaY = -deltaY;
    }
    int partialXa = 0, partialXb = 0;
    while (y1 != y2) {
        unsigned char *screenRow = SCREEN + y1 * 1024;
        for (int x = x1a; x < x1b; x++) {
            screenRow[x] = drawColor;
        }
        partialXa += deltaXa;
        while (partialXa >= deltaY) {
            partialXa -= deltaY;
            x1a++;
        }
        while (partialXa <= -deltaY) {
            partialXa += deltaY;
            x1a--;
        }
        partialXb += deltaXb;
        while (partialXb >= deltaY) {
            partialXb -= deltaY;
            x1b++;
        }
        while (partialXb <= -deltaY) {
            partialXb += deltaY;
            x1b--;
        }
        y1 += dy;
    }

}

void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {

    // sort by Y
    int temp;
    if (y1 > y2) {
        temp = x1;
        x1 = x2;
        x2 = temp;
        temp = y1;
        y1 = y2;
        y2 = temp;
    }
    if (y2 > y3) {
        temp = x2;
        x2 = x3;
        x3 = temp;
        temp = y2;
        y2 = y3;
        y3 = temp;
    }
    if (y1 > y2) {
        temp = x1;
        x1 = x2;
        x2 = temp;
        temp = y1;
        y1 = y2;
        y2 = temp;
    }

    // edge case: all three points have the same Y coordinate. Skip this triangle so we don't divide by 0.
    if (y1 == y3) {
        return;
    }

    // find the split point of the point 1 / point 3 line
    int splitX = x1 + div(mul(x3 - x1, y2 - y1), y3 - y1);

    // draw the two halves
    drawHalfTriangle(x2, splitX, y2, x1, y1, -1);
    drawHalfTriangle(x2, splitX, y2, x3, y3, 1);

}
