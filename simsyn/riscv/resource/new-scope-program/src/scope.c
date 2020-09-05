
#include "draw.h"
#include "term.h"
#include "chargen.h"

void scopeMain(void) {
    setFont(CHARACTER_DATA);
    setDrawColor(1);
    clearScreen(0);

    volatile int *signalLogger = (int*)0x02000000;
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
                setPixel(i, y, high ? 2 : 1);
                setPixel(i, y + 10, high ? 1 : 2);
            }
        }
    }
}
