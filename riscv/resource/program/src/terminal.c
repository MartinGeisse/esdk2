
void terminalPlaceChar(int x, int y, char c) {
    int *basePointer = (int*)0x4000;
    int *pointer = basePointer + 128 * y + x;
    *pointer = c;
}
