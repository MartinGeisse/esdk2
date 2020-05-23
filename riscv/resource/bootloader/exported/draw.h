
#ifndef __BOOTLOADER_EXPORTED_DRAW_H__
#define __BOOTLOADER_EXPORTED_DRAW_H__

extern void (*selectDrawPlane)(int plane);
extern void (*selectDisplayPlane)(int plane);

extern void (*clearScreen)(unsigned char color);
extern void (*setPixel)(int x, int y, unsigned char color);
extern void (*setFont)(unsigned char (*font)[16]);

extern void (*setDrawColor)(int color);
extern void (*drawPixel)(int x, int y);
extern void (*drawAxisAlignedRectangle)(int x, int y, int w, int h);
extern void (*drawLine)(int x1, int y1, int x2, int y2);
extern void (*drawTriangle)(int x1, int y1, int x2, int y2, int x3, int y3);
extern void (*drawCharacter)(int x, int y, char c);
extern void (*drawText)(int x, int y, const char *text);
extern void (*scroll)(int amount, int fillColor);

#endif
