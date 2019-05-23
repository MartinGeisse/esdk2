
static int *cursor = (int*)0x4000;

void terminalPlaceChar(int x, int y, char c) {
    int *basePointer = (int*)0x4000;
    int *pointer = basePointer + 128 * y + x;
    *pointer = c;
}

void terminalWriteChar(char c) {
    if (c == '\n') {
        cursor = (int*)(((int)cursor & ~511) + 512);
    } else {
        *cursor = c;
        cursor++;
    }
}

void terminalWrite(char *s) {
    while (1) {
        char c = *s;
        if (c == 0) {
            return;
        }
        terminalWriteChar(c);
        s++;
    }
}

void terminalWriteInt(int n) {
    TODO
}

void terminalWriteHex(int n) {
    TODO
}
