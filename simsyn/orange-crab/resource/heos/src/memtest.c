
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

void memoryMaskTest(void) {
    termPrintlnString("performing memory mask test");
    volatile unsigned int *words = (volatile unsigned int *)0x80000000;
    volatile unsigned short *halfwords = (volatile unsigned short *)0x80000000;
    volatile unsigned char *bytes = (volatile unsigned char *)0x80000000;
    words[0] = 0x12345678;
    words[1] = 0x12345678;
    words[2] = 0x12345678;
    words[3] = 0x12345678;
    words[4] = 0x12345678;
    words[5] = 0x12345678;
    words[6] = 0x12345678;
    halfwords[2 + 0] = 0xabcd;
    halfwords[4 + 1] = 0xdcba;
    bytes[12 + 0] = 0xaa;
    bytes[16 + 1] = 0xbb;
    bytes[20 + 2] = 0xcc;
    bytes[24 + 3] = 0xdd;
}
