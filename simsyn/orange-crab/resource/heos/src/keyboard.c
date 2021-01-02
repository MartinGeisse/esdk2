
#include "keyboard.h"

static volatile unsigned int * const KEYBOARD = (unsigned int * const)0x40000000;

int keyStates[256];

void resetKeyStates(void) {
    for (int i = 0; i < 256; i++) {
        keyStates[i] = 0;
    }
}

void pollKeyboard(void) {
    while (1) {
        int keyCode = *KEYBOARD;
        if (keyCode == 0) {
            break;
        }
        int newState = 1;
        if (keyCode == 0xf0) {
            newState = 0;
            do {
                keyCode = *KEYBOARD;
            } while (keyCode == 0);
        }
        keyStates[keyCode] = newState;
    }
}
