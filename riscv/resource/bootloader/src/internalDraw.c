
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// definitions
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#define DISPLAY_CONTROL_BASE_ADDRESS 0x00040000
#define PLANE0_BASE_ADDRESS 0x80000000

#define RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT 0x40000000
#define RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS 0xc0000000
#define RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS
#define RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN 0x04000000
#define RAM_AGENT_COMMAND_ENGINE_WRITE_SPAN_BASE_ADDRESS RAM_AGENT_COMMAND_ENGINE_BASE_ADDRESS

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// low-level helpers
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static void setPixel(unsigned char *drawPlane, int x, int y, unsigned char color) {
    if (x >= 0 && x < 640 && y >= 0 && y < 480) {
        drawPlane[(y << 10) + x] = color;
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// actual functions
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void drawLineInternal(int x1, int y1, int x2, int y2, int planeIndex, int color) {

    // build a pointer to the draw plane based on the index
    unsigned char *drawPlane = (unsigned char *)(PLANE0_BASE_ADDRESS + (planeIndex << 19));

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
    setPixel(drawPlane, x1, y1, color);

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
            setPixel(drawPlane, x, y, color);
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
            setPixel(drawPlane, x, y, color);
        }

    }

}

void drawHorizontalLineInternal(int x1, int x2, int y, int planeIndex, int color) {

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
    unsigned char *drawPlane = (unsigned char *)(PLANE0_BASE_ADDRESS + (planeIndex << 19));
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
            *p1 = color;
            p1++;
        }

    } else {

        // draw initial word fraction
        while ((int)p1 & 3) {
            *p1 = color;
            p1++;
        }

        // prepare drawing full words
        int start = (int)p1;
        int words = (p2 - p1) >> 2;
        p1 = (unsigned char *)(((int)p2) & ~3);

        // draw final word fraction (or whole line if less than 8 pixels)
        while (p1 < p2) {
            *p1 = color;
            p1++;
        }

        // draw full words
        int fourPixels = color | (color << 8) | (color << 16) | (color << 24);
        *(int *)RAM_AGENT_COMMAND_ENGINE_SPAN_LENGTH_REGISTER_ADDRESS = words;
        *(int*)(start | RAM_AGENT_COMMAND_ENGINE_ADDRESS_BIT | RAM_AGENT_COMMAND_ENGINE_COMMAND_CODE_WRITE_SPAN) = fourPixels;

    }

}

void drawHalfTriangleInternal(int x1a, int x1b, int y1, int x2, int y2, int dy, int planeIndex, int color) {

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
        drawHorizontalLineInternal(x1a, x1b, y1, planeIndex, color);

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
