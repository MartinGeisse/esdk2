
#include "system.h"
#include "draw.h"
#include "simdev.h"
#include "cpu.h"

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
    // drawHalfTriangle(100, 200, 50, 120, 190, 1);
    // drawHalfTriangle(100, 200, 190, 120, 50, -1);
    clearScreen(1);
    drawTriangle(50, 150, 300, 70, 200, 200);
    simdevMessage("DONE!");

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
