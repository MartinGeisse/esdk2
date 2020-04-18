
// must this be extern? but if that was the problem then I should get symbol collisions!
// Right now it rather seems like the compiler doesn't get that it is a pointer.
extern void (*drawLineInternal)(int x1, int y1, int x2, int y2, int planeIndex, int color);
