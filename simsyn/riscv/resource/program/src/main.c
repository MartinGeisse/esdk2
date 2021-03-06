
#include "system.h"
#include "terminal.h"
#include "draw.h"
#include "simdev.h"
#include "cpu.h"

static int analogValue = 0;

static void draw(int x, int y, int size) {
    if (size >= 4) {
        int s1 = size >> 2;
        int s2 = s1 + s1;
        int s3 = s2 + s1;
        draw(x, y, s1);
        draw(x + s2, y, s1);
        draw(x + s1, y + s1, s1);
        draw(x + s3, y + s1, s1);
        draw(x, y + s2, s1);
        draw(x + s2, y + s2, s1);
        draw(x + s1, y + s3, s1);
        draw(x + s3, y + s3, s1);
    } else {
        unsigned char *pixelPointer = (unsigned char *)0x80000000;
        unsigned char *rowPointer = pixelPointer + (y << 10);
        rowPointer[x] = 2;
    }
}

void main() {
    simdevMessage("Hello World!");
    simdevShowInt("Test Value", 99);

    int a = 4;
    int b = 12;
    int c = 15;
    int d = 3;
    simdevShowInt("Test Value", a * b);
    simdevShowInt("Test Value", c / d);

    // wait for SDRAM reset, but only on real hardware
    if (!simdevIsSimulation()) {
        delay(500);
    }

    // test for exceptions
    int code[2] = {0xffffffff, 0x00008067};
    void (*func)() = code;
    func();

    // clearScreen(0);
    int x = 50, y = 240, dx = 1, dy = 0;
    volatile unsigned char *screen = (volatile unsigned char *)0x80000000;
    while (1) {
        if (!simdevIsSimulation()) {
            delay(20);
        }

        volatile unsigned char *pixel = screen + y * 1024 + x;
        if (*pixel != 0) {
            dx = dy = 0;
            continue;
        }
        *pixel = 2;

        int buttons = *(volatile int*)0x10000;
        if (buttons & 8) {
            dx = 0;
            dy = -1;
        } else if (buttons & 4) {
            dx = 1;
            dy = 0;
        } else if (buttons & 2) {
            dx = 0;
            dy = 1;
        } else if (buttons & 1) {
            dx = -1;
            dy = 0;
        }

        x += dx;
        y += dy;

    }






    // clearScreen(0);
    // draw(0, 0, 256);

//     int *basePointer = (int*)0x80000000;

    //
    /*
    terminalWriteChar('\n');
    terminalWriteChar('\n');
    terminalWrite("Hello world!\n");
    terminalWrite("FOO BAR FUPP\n");
    for (int i = 0; i < 80; i++) {
        terminalPlaceChar(i, 0, 1);
        terminalPlaceChar(i, 29, 1);
    }
    for (int i = 0; i < 30; i++) {
        terminalPlaceChar(0, i, 1);
        terminalPlaceChar(79, i, 1);
    }
    */



//    int *startPointer = basePointer + 1024 * 1024;
//    int *endPointer = basePointer + 16 * 1024 * 1024;
//
//    {
//        int value = 9;
//        for (int *p = startPointer; p < endPointer; p++) {
//
//            // write test value to SDRAM
//            *p = value;
//
//            // newValue = oldValue * 5 + 1
//            value = (value << 2) + value + 1;
//
//            // show progress
//            if ((((int)p) & 0x007fffff) == 0) {
//                terminalWriteChar('.');
//            }
//
//        }
//    }
//    terminalWriteChar('\n');
//
//    {
//        int value = 9;
//        for (int *p = startPointer; p < endPointer; p++) {
//
//            // read back test value from SDRAM
//            if (*p != value) {
//                terminalWriteChar('E');
//            }
//
//            // newValue = oldValue * 5 + 1
//            value = (value << 2) + value + 1;
//
//            // show progress
//            if ((((int)p) & 0x007fffff) == 0) {
//                terminalWriteChar('.');
//            }
//
//        }
//    }
//    terminalWriteChar('\n');

}

void exception(int instructionAddress) {
    simdevMessage("EXCEPTION!");
    simdevShowInt("Exception code", cpuGetExceptionCode());
    simdevShowInt("location", instructionAddress);
    simdevShowInt("instruction", *(unsigned int *)instructionAddress);
}
