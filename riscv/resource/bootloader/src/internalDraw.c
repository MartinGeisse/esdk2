
void drawLineInternal(int x1, int y1, int x2, int y2, int planeIndex, int color) {
    // TODO do nothing
}

/*
static void setPixel(unsigned char *drawPlane, int x, int y, unsigned char color) {
    if (x >= 0 && x < 640 && y >= 0 && y < 480) {
        drawPlane[(y << 10) + x] = color;
    }
}

void drawLineInternal(int x1, int y1, int x2, int y2, int planeIndex, int color) {

    // build a pointer to the draw plane based on the index
    unsigned char *drawPlane = (unsigned char *)(0x80000000 + (planeIndex << 19));

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
*/
