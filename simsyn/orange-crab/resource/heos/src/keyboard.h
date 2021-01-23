
#ifndef __KEYBOARD_H__
#define __KEYBOARD_H__

void resetKeyStates(void);
void pollKeyboard(void);

extern int keyStates[256];

#endif
