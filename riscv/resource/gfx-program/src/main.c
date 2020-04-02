
#include "system/system.h"
#include "system/draw.h"
#include "system/simdev.h"
#include "system/cpu.h"

#define SQUARE_COUNT 7
#define SPEED 20
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

    // simdevMessage("DONE!");

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
