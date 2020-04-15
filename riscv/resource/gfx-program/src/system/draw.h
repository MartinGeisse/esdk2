
#ifndef __DRAW_H__
#define __DRAW_H__

void selectDrawPlane(int plane);
void selectDisplayPlane(int plane);

void clearScreen(unsigned char color);
void drawPixel(int x, int y, unsigned char color);

void setDrawColor(int color);
void drawAxisAlignedRectangle(int x, int y, int w, int h);
void drawLine(int x1, int y1, int x2, int y2);
void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3);

#endif
