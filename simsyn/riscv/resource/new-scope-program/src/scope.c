
#include "draw.h"
#include "term.h"
#include "chargen.h"

void scopeMain(void) {
    setFont(CHARACTER_DATA);
    setDrawColor(1);
    clearScreen(0);
    termInitialize();
    termPrintlnString("--- new scope 2 ---");
}
