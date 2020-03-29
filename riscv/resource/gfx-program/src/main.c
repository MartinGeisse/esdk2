
#include "system/system.h"
#include "system/draw.h"
#include "system/simdev.h"
#include "system/cpu.h"

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

    int growing = 1;
    int size = 50;
    while (1) {
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
    }

    // simdevMessage("DONE!");

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
