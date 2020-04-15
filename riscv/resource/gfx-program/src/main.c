
#include "system/system.h"
#include "system/draw.h"
#include "system/simdev.h"
#include "system/cpu.h"

#define SQUARE_COUNT 7
#define SPEED 10
static int xs[SQUARE_COUNT];
static int ys[SQUARE_COUNT];
static int dxs[SQUARE_COUNT];
static int dys[SQUARE_COUNT];
static int sizes[SQUARE_COUNT];

static void moveSquare(int *pp, int *dp, int max) {
    *pp += *dp;
    if (*pp < 0) {
        *dp = SPEED;
    }
    if (*pp > max) {
        *dp = -SPEED;
    }
}

void main() {

//    simdevShowInt("a", mul(3, 0));
//    simdevShowInt("b", mul(0, 3));
//    simdevShowInt("c", mul(3, 5));
//    simdevShowInt("d", mul(5, 3));
//    simdevShowInt("e", mul(-7, 8));
//    simdevShowInt("f", mul( 9, -5));

//    simdevShowInt("a", div(15, 3));
//    simdevShowInt("a", div(15, 5));
//    simdevShowInt("a", div(15, -3));
//    simdevShowInt("a", div(15, -5));
//    simdevShowInt("a", div(-15, 3));
//    simdevShowInt("a", div(-15, 5));
//    simdevShowInt("a", div(-15, -3));
//    simdevShowInt("a", div(-15, -5));
//    simdevShowInt("a", div(10, 0));

    // wait for SDRAM reset, but only on real hardware
    if (!simdevIsSimulation()) {
        delay(500);
    }

    // test code
//    clearScreen(1);
//    drawTriangle(50, 150, 300, 70, 200, 200);

/*
    int growing = 1;
    int size = 50;
    int drawPlane = 0;
    while (1) {
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(1);
        int x1 = 320 - size;
        int x2 = 320 + size;
        int y1 = 240 - size;
        int y2 = 240 + size;
        drawTriangle(x1, y1, x2, y1, x1, y2);
        drawTriangle(x1, y2, x2, y1, x2, y2);
        if (growing) {
            size = size + (size >> 1);
            if (size > 100) {
                growing = 0;
            }
        } else {
            size = size - (size >> 2);
            if (size < 30) {
                growing = 1;
            }
        }
        delay(500);
    }
*/

/*
    for (int i = 0, size = 30; i < SQUARE_COUNT; i++, size += 23) {
        xs[i] = 0;
        ys[i] = 0;
        dxs[i] = SPEED;
        dys[i] = SPEED;
        sizes[i] = size;
    }

    int drawPlane = 0;
    while (1) {
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);
        for (int i = SQUARE_COUNT - 1; i >= 0; i--) {
            setDrawColor(i + 1);
            drawAxisAlignedRectangle(xs[i], ys[i], sizes[i], sizes[i]);
            moveSquare(xs + i, dxs + i, 640 - sizes[i]);
            moveSquare(ys + i, dys + i, 480 - sizes[i]);
        }
    }
*/

    clearScreen(0);
    setDrawColor(1);
    drawLine(200, 200, 300, 200);
    drawLine(200, 200, 300, 150);
    drawLine(200, 200, 300, 100);
    setDrawColor(2);
    drawLine(200, 200, 250, 100);
    drawLine(200, 200, 200, 100);
    setDrawColor(3);
    drawLine(200, 200, 150, 100);
    drawLine(200, 200, 100, 100);
    drawLine(200, 200, 100, 150);
    setDrawColor(4);
    drawLine(200, 200, 100, 200);
    drawLine(200, 200, 100, 250);
    setDrawColor(5);
    drawLine(200, 200, 100, 300);
    drawLine(200, 200, 150, 300);
    setDrawColor(6);
    drawLine(200, 200, 200, 300);
    drawLine(200, 200, 250, 300);
    setDrawColor(7);
    drawLine(200, 200, 300, 300);
    drawLine(200, 200, 300, 250);
    while (1);

/*
    volatile int *signalLogger = (int*)0x00008000;
    *signalLogger = 3;
    *signalLogger = 8;
    int drawPlane = 0;
    while (1) {
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);

        *signalLogger = 10;
        *signalLogger = 8;
        for (int i = 0; i < 512; i++) {
            int loggedValue = *signalLogger;
            for (int mask = 1, y = 0; mask <= 4096; mask = mask + mask, y = y + 20) {
                int high = (loggedValue & mask) != 0;
                drawPixel(i, y, high ? 2 : 1);
                drawPixel(i, y + 5, high ? 1 : 2);
            }
        }
    }
*/


/*
    int color = 0;
    while (1) {
        simdevGlFlipScreen();
        simdevGlClearScreen(color);
        color++;
    }
*/

    // simdevMessage("DONE!");

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
