
#include "system/util.h"
#include "system/draw.h"
#include "system/simdev.h"

int demo();


void main() {

    demo();

    /*
    int drawPlane = 0, x = 200, y = 200;
    while (1) {

        // flip and clear screen
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);

        // draw border
        setDrawColor(5);
        drawLine(0, 0, 639, 0);
        drawLine(639, 0, 639, 479);
        drawLine(639, 479, 0, 479);
        drawLine(0, 479, 0, 0);

        // draw test lines
        setDrawColor(1);
        drawLine(x, y, x + 100, y);
        drawLine(x, y, x + 100, y - 50);
        drawLine(x, y, x + 100, y - 100);
        setDrawColor(2);
        drawLine(x, y, x + 50, y - 100);
        drawLine(x, y, x, y - 100);
        setDrawColor(3);
        drawLine(x, y, x - 50, y - 100);
        drawLine(x, y, x - 100, y - 100);
        drawLine(x, y, x - 100, y - 50);
        setDrawColor(4);
        drawLine(x, y, x - 100, y);
        drawLine(x, y, x - 100, y + 50);
        setDrawColor(5);
        drawLine(x, y, x - 100, y + 100);
        drawLine(x, y, x - 50, y + 100);
        setDrawColor(6);
        drawLine(x, y, x, y + 100);
        drawLine(x, y, x + 50, y + 100);
        setDrawColor(7);
        drawLine(x, y, x + 100, y + 100);
        drawLine(x, y, x + 100, y + 50);

        // draw test triangle
        drawTriangle(x + 200, y, x + 300, y - 50, x + 350, y + 100);

        // movement
        if (KEY_STATE(0x6b)) {
            x -= 5;
        }
        if (KEY_STATE(0x74)) {
            x += 5;
        }
        if (KEY_STATE(0x75)) {
            y -= 5;
        }
        if (KEY_STATE(0x72)) {
            y += 5;
        }

    }
    */

}
