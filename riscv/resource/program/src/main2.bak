
#include "system.h"
#include "terminal.h"
#include "draw.h"

static int analogValue = 0;

void main() {

    // wait for SDRAM reset
    delay(500);

    while (1) {
        delay(1);
        analogValue+=20;
        int dacCommandWord = 0x00330000 | (analogValue & 0xffff);
        *(int*)0x20000 = dacCommandWord;
    }
}
