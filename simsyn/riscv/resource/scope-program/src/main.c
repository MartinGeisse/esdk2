
#include "system/system.h"
#include "system/draw.h"
#include "system/simdev.h"
#include "system/cpu.h"

void main() {

    // wait for SDRAM reset, but only on real hardware
    if (!simdevIsSimulation()) {
        delay(500);
    }

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
            for (int mask = 1, y = 0; mask <= 16; mask = mask + mask, y = y + 50) {
                int high = (loggedValue & mask) != 0;
                drawPixel(i, y, high ? 2 : 1);
                drawPixel(i, y + 10, high ? 1 : 2);
            }
        }
    }

}

void exception() {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
}
