
#include "draw.h"
#include "term.h"
#include "chargen.h"
#include "profiling.h"

void sierpinskyMain();

void main(void) {
    setFont(CHARACTER_DATA);
    clearScreen(0);
    termInitialize();
    termPrintlnString("Gamesys OS ready.");
    sierpinskyMain();
}
