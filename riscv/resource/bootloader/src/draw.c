
#include "divrem.h"

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

static unsigned char (*font)[16];

void selectDrawPlane(int plane) {
    plane = plane & 1;
    drawPlaneIndex = plane;
    drawPlane = (unsigned char *)(PLANE0_BASE_ADDRESS + (plane << 19));
}

void selectDisplayPlane(int plane) {
    *(int*)DISPLAY_CONTROL_BASE_ADDRESS = plane;
}

void clearScreen(unsigned char color) {
    int fourPixels = color | (color << 8) | (color << 16) | (color << 24);
    int *rowPointer = (int*)drawPlane;
    int *screenEnd = rowPointer + 256 * 480;
    *(int *)RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS = 160;
    while (rowPointer < screenEnd) {
        *(int*)(((int)rowPointer) | RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT | RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN) = fourPixels;
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

void setFont(unsigned char (*newFont)[16]) {
    font = newFont;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// drawing primitives
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void drawPixel(int x, int y) {
    setPixel(x, y, drawColor);
}

// note: expects x1 <= x2
static void drawHorizontalLine(int x1, int x2, int y) {

    // clip vertically
    if (y < 0 || y >= 480) {
        return;
    }

    // clip horizontally
    if (x1 < 0) {
        x1 = 0;
    }
    if (x2 > 640) {
        x2 = 640;
    }

    // convert x-values to pointers
    unsigned char *screenRow = drawPlane + (y << 10);
    unsigned char *p1 = screenRow + x1;
    unsigned char *p2 = screenRow + x2;

    // Avoid complications with word alignment in short rows. Also, we compute the necessary parameters for handing
    // the actual filling over to the RAM agent, but we only start it after drawing the finishing pixels. This allows
    // the RAM agent to run in parallel while this method returns, possibly to other code in small RAM (especially
    // triangle drawing).
    if (p2 - p1 < 8) {

        // just draw pixel-wise
        while (p1 < p2) {
            *p1 = drawColor;
            p1++;
        }

    } else {

        // draw initial word fraction
        while ((int)p1 & 3) {
            *p1 = drawColor;
            p1++;
        }

        // prepare drawing full words
        int start = (int)p1;
        int words = (p2 - p1) >> 2;
        p1 = (unsigned char *)(((int)p2) & ~3);

        // draw final word fraction (or whole line if less than 8 pixels)
        while (p1 < p2) {
            *p1 = drawColor;
            p1++;
        }

        // draw full words
        *(int *)RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS = words;
        *(int*)(start | RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT | RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN) = drawColorWord;

    }

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

    // make sure that x1 <= x2, swap points if not
    if (x1 > x2) {
        x1 ^= x2;
        x2 ^= x1;
        x1 ^= x2;
        y1 ^= y2;
        y2 ^= y1;
        y1 ^= y2;
    }

    // compute deltas (dx >= 0), signy (-1 for negative, +1 for zero or positive) and absdy
    int dx = x2 - x1;
    int dy = y2 - y1;
    int signy, absdy;
    if (dy < 0) {
        signy = -1;
        absdy = -dy;
    } else {
        signy = +1;
        absdy = dy;
    }

    // since we have to draw both endpoints, drawing the first endpoint out of the loop makes the loop condition simpler
    setPixel(x1, y1, drawColor);

    // We use different algorithmms for horizontal and vertical cases to make them simpler.
    int x = x1, y = y1;
    if (dy < -dx || dy > dx) {

        // vertical case
        int fraction = 0;
        while (y != y2) {
            y += signy;
            fraction += dx;
            if (fraction >= (absdy >> 1)) {
                fraction -= absdy;
                x++;
            }
            setPixel(x, y, drawColor);
        }

    } else {

        // horizontal case
        int fraction = 0;
        while (x != x2) {
            x++;
            fraction += absdy;
            if (fraction >= (dx >> 1)) {
                fraction -= dx;
                y += signy;
            }
            setPixel(x, y, drawColor);
        }

    }

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

        // draw row
        drawHorizontalLine(x1a, x1b, y1);

        // adjust left endpoint
        partialXa += deltaXa;
        while (partialXa >= deltaY) {
            partialXa -= deltaY;
            x1a++;
        }
        while (partialXa <= -deltaY) {
            partialXa += deltaY;
            x1a--;
        }

        // adjust right endpoint
        partialXb += deltaXb;
        while (partialXb >= deltaY) {
            partialXb -= deltaY;
            x1b++;
        }
        while (partialXb <= -deltaY) {
            partialXb += deltaY;
            x1b--;
        }

        // move to next row
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
    int splitX = x1 + div((x3 - x1) * (y2 - y1), y3 - y1);

    // draw the two halves
    drawHalfTriangle(x2, splitX, y2, x1, y1, -1);
    drawHalfTriangle(x2, splitX, y2, x3, y3, 1);

}

void drawCharacter(int x, int y, char c) {
    unsigned char *thisCharacterData = font[(unsigned char)c];
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

void scroll(int amount, int fillColor) {

    // move the scrolled part
    int y0 = 480 - amount;
    for (int y = 0; y < y0; y++) {
        int *writeRow = (int*)(drawPlane + (y << 10));
        int *readRow = (int*)(drawPlane + ((y + amount) << 10));
        for (int i = 0; i < 160; i++) {
            writeRow[i] = readRow[i];
        }
    }

    // fill the new part (using a word that contains the fill color four times)
    int fillColorWord = (fillColor << 8) | fillColor;
    fillColorWord = (fillColorWord << 16) | fillColorWord;
    for (int y = y0; y < 480; y++) {
        int *writeRow = (int*)(drawPlane + (y << 10));
        for (int i = 0; i < 160; i++) {
            writeRow[i] = fillColorWord;
        }
    }

}
