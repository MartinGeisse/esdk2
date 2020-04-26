
#ifndef __BOOTLOADER_INTERNAL_DRAW_H__
#define __BOOTLOADER_INTERNAL_DRAW_H__

void drawLineInternal(int x1, int y1, int x2, int y2, int planeIndex, int color);
void drawHorizontalLineInternal(int x1, int x2, int y, int planeIndex, int color);
void drawHalfTriangleInternal(int x1a, int x1b, int y1, int x2, int y2, int dy, int planeIndex, int color);

#endif
