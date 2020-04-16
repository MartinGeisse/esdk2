
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

// movement keys
static int keyW = 0, keyX = 0, keyA = 0, keyD = 0, keyE = 0, keyC = 0;

// rotation keys
static int keyQ = 0, keyR = 0, keyUp = 0, keyDown = 0, keyLeft = 0, keyRight = 0;

static int keyReleasePrefix = 0;

void checkKeyboard() {
    int incomingCode = *(int*)0x00004000;
    if (incomingCode == 0 || incomingCode == 0xe0 || incomingCode == 0xe1) {
        return;
    }
    if (incomingCode == 0xf0) {
        keyReleasePrefix = 1;
        return;
    }
    int newState = !keyReleasePrefix;
    keyReleasePrefix = 0;
    switch (incomingCode) {

        case 0x1d:
            keyW = newState;
            break;

        case 0x22:
            keyX = newState;
            break;

        case 0x1c:
            keyA = newState;
            break;

        case 0x23:
            keyD = newState;
            break;

        case 0x24:
            keyE = newState;
            break;

        case 0x21:
            keyC = newState;
            break;

        case 0x15:
            keyQ = newState;
            break;

        case 0x2d:
            keyR = newState;
            break;

        case 0x75:
            keyUp = newState;
            break;

        case 0x72:
            keyDown = newState;
            break;

        case 0x6b:
            keyLeft = newState;
            break;

        case 0x74:
            keyRight = newState;
            break;

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

    int drawPlane = 0, x = 200, y = 200;
    while (1) {

        // ...
        checkKeyboard();

        // flip and clear screen
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);

        // ...
        checkKeyboard();

        // draw border
        setDrawColor(5);
        checkKeyboard();
        drawLine(0, 0, 639, 0);
        checkKeyboard();
        drawLine(639, 0, 639, 479);
        checkKeyboard();
        drawLine(639, 479, 0, 479);
        checkKeyboard();
        drawLine(0, 479, 0, 0);
        checkKeyboard();

        // draw test lines
        setDrawColor(1);
        checkKeyboard();
        drawLine(x, y, x + 100, y);
        checkKeyboard();
        drawLine(x, y, x + 100, y - 50);
        checkKeyboard();
        drawLine(x, y, x + 100, y - 100);
        checkKeyboard();
        setDrawColor(2);
        checkKeyboard();
        drawLine(x, y, x + 50, y - 100);
        checkKeyboard();
        drawLine(x, y, x, y - 100);
        checkKeyboard();
        setDrawColor(3);
        checkKeyboard();
        drawLine(x, y, x - 50, y - 100);
        checkKeyboard();
        drawLine(x, y, x - 100, y - 100);
        checkKeyboard();
        drawLine(x, y, x - 100, y - 50);
        checkKeyboard();
        setDrawColor(4);
        checkKeyboard();
        drawLine(x, y, x - 100, y);
        checkKeyboard();
        drawLine(x, y, x - 100, y + 50);
        checkKeyboard();
        setDrawColor(5);
        checkKeyboard();
        drawLine(x, y, x - 100, y + 100);
        checkKeyboard();
        drawLine(x, y, x - 50, y + 100);
        checkKeyboard();
        setDrawColor(6);
        checkKeyboard();
        drawLine(x, y, x, y + 100);
        checkKeyboard();
        drawLine(x, y, x + 50, y + 100);
        checkKeyboard();
        setDrawColor(7);
        checkKeyboard();
        drawLine(x, y, x + 100, y + 100);
        checkKeyboard();
        drawLine(x, y, x + 100, y + 50);
        checkKeyboard();

        // draw test triangle
        drawTriangle(x + 200, y, x + 300, y - 50, x + 350, y + 100);
        checkKeyboard();

        // movement
        if (keyLeft) {
            x -= 5;
        }
        if (keyRight) {
            x += 5;
        }
        if (keyUp) {
            y -= 5;
        }
        if (keyDown) {
            y += 5;
        }

    }

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
