
#include <divrem.h>
#include <internalDraw.h>
#include "simdev.h"
#include "chargen.h"
#include "profiling.h"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// low-level helpers
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#define DISPLAY_CONTROL_BASE_ADDRESS 0x00040000
#define PLANE0_BASE_ADDRESS 0x80000000

#define RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT 0x40000000
#define RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS 0xc0000000
#define RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS
#define RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN 0x04000000
#define RAM_AGENT_COMMAND_ENGINE_WRITE_SPAN_BASE_ADDRESS RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// basics
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static int drawPlaneIndex = 0;
static unsigned char *drawPlane = (unsigned char *)PLANE0_BASE_ADDRESS;

static unsigned char drawColor = 7;
static unsigned int drawColorWord = 0x07070707;

void selectDrawPlane(int plane) {
    plane = plane & 1;
    drawPlaneIndex = plane;
    drawPlane = (unsigned char *)(PLANE0_BASE_ADDRESS + (plane << 19));
}

void selectDisplayPlane(int plane) {
    if (simdevIsSimulation()) {
        simdevSelectDisplayPlane(plane);
    } else {
        *(int*)DISPLAY_CONTROL_BASE_ADDRESS = plane;
    }
}

void clearScreen(unsigned char color) {
    int isSimulation = simdevIsSimulation();
    int fourPixels = color | (color << 8) | (color << 16) | (color << 24);
    int *rowPointer = (int*)drawPlane;
    int *screenEnd = rowPointer + 256 * 480;
    if (!isSimulation) {
        *(int *)RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS = 160;
    }
    while (rowPointer < screenEnd) {
        if (isSimulation) {
            simdevFillWordsShowInt(rowPointer, fourPixels, 160);
        } else {
            *(int*)(((int)rowPointer) | RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT | RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN) = fourPixels;
        }
        rowPointer += 256;
    }
}

void setPixel(int x, int y, unsigned char color) {
    if (x >= 0 && x < 640 && y >= 0 && y < 480) {
        drawPlane[(y << 10) + x] = color;
    }
}

void setDrawColor(int color) {
    drawColor = color;
    drawColorWord = (color << 8) | color;
    drawColorWord = (drawColorWord << 16) | drawColorWord;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// drawing primitives
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void drawPixel(int x, int y) {
    setPixel(x, y, drawColor);
}

// note: expects x1 <= x2
static void drawHorizontalLine(int x1, int x2, int y) {
    drawHorizontalLineInternal(x1, x2, y, drawPlaneIndex, drawColor);
}

void drawAxisAlignedRectangle(int x, int y, int w, int h) {
    if (x < 0) {
        w += x;
        x = 0;
    }
    if (y < 0) {
        h += y;
        y = 0;
    }
    if (x + w > 640) {
        w = 640 - x;
    }
    if (y + h > 480) {
        h = 480 - y;
    }
    int x2 = x + w;
    int y2 = y + h;
    while (y < y2) {
        drawHorizontalLine(x, x2, y);
        y++;
    }
}

void drawLine(int x1, int y1, int x2, int y2) {
    drawLineInternal(x1, y1, x2, y2, drawPlaneIndex, drawColor);
}

static void drawHalfTriangle(int x1a, int x1b, int y1, int x2, int y2, int dy) {
    drawHalfTriangleInternal(x1a, x1b, y1, x2, y2, dy, drawPlaneIndex, drawColor);
}

void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {

    profLog("DT start");

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
    int splitX = x1 + div((x3 - x1) * (y2 - y1), y3 - y1);

    profLog("DT prep done");

    // draw the two halves
    drawHalfTriangle(x2, splitX, y2, x1, y1, -1);
    drawHalfTriangle(x2, splitX, y2, x3, y3, 1);

    profLog("DT finished");

}

void drawCharacter(int x, int y, char c) {
    unsigned char *thisCharacterData = CHARACTER_DATA[(unsigned char)c];
    for (int dy = 0; dy < 16; dy++) {
        unsigned char row = thisCharacterData[dy];
        for (int dx = 0; dx < 8; dx++) {
            int mask = (1 << dx);
            setPixel(x + dx, y + dy, (row & mask) == 0 ? 0 : drawColor);
        }
    }
}

void drawText(int x, int y, const char *text) {
    while (1) {
        char c = *text;
        if (c == 0) {
            return;
        }
        drawCharacter(x, y, c);
        x += 8;
        text++;
    }
}
