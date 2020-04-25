
extern unsigned int (*udivrem)(unsigned int x, unsigned int y, int rem);
extern unsigned int (*udiv)(unsigned int x, unsigned int y);
extern unsigned int (*urem)(unsigned int x, unsigned int y);
extern int (*div)(int x, int y);

extern void (*drawLineInternal)(int x1, int y1, int x2, int y2, int planeIndex, int color);
extern void (*drawHorizontalLineInternal)(int x1, int x2, int y, int planeIndex, int color);
