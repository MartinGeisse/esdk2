
#ifndef TERMINAL_H
#define TERMINAL_H

void termInitialize();
void termPrintString(const char *s);
void termPrintChar(char c);
void termPrintInt(int i);
void termPrintUnsignedInt(unsigned int i);
void termPrintHexInt(int i);
void termPrintUnsignedHexInt(unsigned int i);
void termPrintln();
void termPrintlnString(const char *s);
void termPrintlnChar(char c);
void termPrintlnInt(int i);
void termPrintlnUnsignedInt(unsigned int i);
void termPrintlnHexInt(int i);
void termPrintlnUnsignedHexInt(unsigned int i);

#endif
