
void clearScreen(unsigned char color) {
    int fourPixels = color | (color << 8) | (color << 16) | (color << 24);
    int *rowPointer = (int*)0x80000000;
    int *screenEnd = rowPointer + 256 * 480;
    while (rowPointer < screenEnd) {
        int *pixelPointer = rowPointer;
        int *rowEnd = pixelPointer + 160;
        while (pixelPointer < rowEnd) {
            *pixelPointer = fourPixels;
            pixelPointer++;
        }
        rowPointer += 256;
    }
}
