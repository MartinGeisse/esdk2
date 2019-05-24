
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

static void terminalWriteIntHelper(int n, int hex) {
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
    while (n > 0) {

        //
        int significance = 1;
        while (1) {
            int nextSignificance;
            if (hex) {
                nextSignificance = significance << 4;
            } else {
                nextSignificance = significance << 1;
                nextSignificance = (nextSignificance << 2) + nextSignificance;
            }
            if (nextSignificance < significance) {
                // overflow means we reached the highest possible significance
                break;
            }
            if (nextSignificance > n) {
                // no digits with next or higher significance
                break;
            }
            significance = nextSignificance;
        }
    }
}

void terminalWriteInt(int n) {
    terminalWriteIntBase(n, 0);
}

void terminalWriteHex(int n) {
    terminalWriteIntBase(n, 1);
}
