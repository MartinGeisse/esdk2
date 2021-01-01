
#include "term.h"
#include "keyboard.h"

void delay(int n);

void memoryTest(void) {
    termPrintlnString("performing memory test");

    termPrintString("write phase... ");
    volatile unsigned int *memory = (volatile unsigned int *)0x80000000;
    int x = 1;
    for (int i = 0; i < 0x02000000; i++) { // this is exactly the declared size of the memory, and exactly the size when the test succeeds
        memory[i] = x;
        x = (x << 2) + x + 1;
    }
    termPrintlnString("done.");

    termPrintlnUnsignedInt(memory[0]);
    termPrintlnUnsignedInt(memory[1]);
    termPrintlnUnsignedInt(memory[2]);
    termPrintlnUnsignedInt(memory[3]);
}
