
#include "draw.h"
#include "term.h"
#include "chargen.h"
#include "profiling.h"

#include "rocksndiamonds/libgame/hash.h"

void sierpinskyMain();

void kernelMain(void) {
    setFont(CHARACTER_DATA);
    clearScreen(0);
    termInitialize();
    termPrintlnString("Gamesys OS ready.");
    sierpinskyMain();
}
