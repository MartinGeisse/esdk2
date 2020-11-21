
#include "draw.h"
#include "term.h"
#include "chargen.h"

void netboot(void) {
    setFont(CHARACTER_DATA);
    setDrawColor(1);
    clearScreen(0);
    termInitialize();
    termPrintlnString("--- netboot ---");
    while (1);
}
