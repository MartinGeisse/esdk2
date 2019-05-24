
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

static unsigned int terminalWriteDigit(unsigned int n, unsigned int significance) {
    unsigned int digit = 0;
    while (n >= significance) {
        n -= significance;
        digit++;
    }
    if (digit > 9) {
        terminalWriteChar(digit - 10 + 'a');
    } else {
        terminalWriteChar(digit + '0');
    }
    return n;
}

void terminalWriteInt(int n) {
    if (n == 0) {
        terminalWriteChar('0');
        return;
    }
    if (n < 0) {
        terminalWriteChar('-');
        n = -n;
    }
    if (n < 0) {
        terminalWriteChar('*');
        return;
    }
    n = terminalWriteDigit(n, 1000000000);
    n = terminalWriteDigit(n, 100000000);
    n = terminalWriteDigit(n, 10000000);
    n = terminalWriteDigit(n, 1000000);
    n = terminalWriteDigit(n, 100000);
    n = terminalWriteDigit(n, 10000);
    n = terminalWriteDigit(n, 1000);
    n = terminalWriteDigit(n, 100);
    n = terminalWriteDigit(n, 10);
    terminalWriteDigit(n, 1);
}

void terminalWriteHex(unsigned int n) {
    if (n == 0) {
        terminalWriteChar('0');
        return;
    }
    n = terminalWriteDigit(n, 0x10000000);
    n = terminalWriteDigit(n, 0x1000000);
    n = terminalWriteDigit(n, 0x100000);
    n = terminalWriteDigit(n, 0x10000);
    n = terminalWriteDigit(n, 0x1000);
    n = terminalWriteDigit(n, 0x100);
    n = terminalWriteDigit(n, 0x10);
    terminalWriteDigit(n, 0x1);
}
