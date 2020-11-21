
#include "draw.h"
#include "term.h"
#include "chargen.h"
#include "profiling.h"

void main(void) {
    profReset();
    setFont(CHARACTER_DATA);
    profLog("a");
    setDrawColor(1);
    profLog("b");
    clearScreen(0);
    profLog("c");
    termInitialize();
    profLog("d");
    termPrintlnString("Gamesys OS ready.");
    profLog("e");
    profDisplay();
    while (1);
}
