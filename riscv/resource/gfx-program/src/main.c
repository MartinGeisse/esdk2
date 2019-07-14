
#include "system.h"
#include "draw.h"
#include "simdev.h"
#include "cpu.h"

static unsigned char drawColor = 7;
static volatile unsigned char *screen = (volatile unsigned char *)0x80000000;

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
        volatile unsigned char *screenRow = screen + y1 * 1024;
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

static void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {

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
    // TODO division does not work yet!
    int splitX = (x3 - x1) / (y3 - y1) * (y2 - y1);

    // draw the two halves
    drawColor = 2;
    drawHalfTriangle(x2, splitX, y2, x1, y1, -1);
    drawColor = 4;
    drawHalfTriangle(x2, splitX, y2, x3, y3, 1);

}

void main() {

    // wait for SDRAM reset, but only on real hardware
    if (!simdevIsSimulation()) {
        delay(500);
    }

    // test code
    // drawHalfTriangle(100, 200, 50, 120, 190, 1);
    // drawHalfTriangle(100, 200, 190, 120, 50, -1);
    drawTriangle(50, 150, 300, 70, 200, 200);
    simdevMessage("DONE!");

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
