
#ifndef __BOOTLOADER_EXPORTED_DRAW_H__
#define __BOOTLOADER_EXPORTED_DRAW_H__

extern void (*drawLineInternal)(int x1, int y1, int x2, int y2, int planeIndex, int color);
extern void (*drawHorizontalLineInternal)(int x1, int x2, int y, int planeIndex, int color);
extern void (*drawHalfTriangleInternal)(int x1a, int x1b, int y1, int x2, int y2, int dy, int planeIndex, int color);

#endif
