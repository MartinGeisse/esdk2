
#include "Fixed.h"

Fixed fixedSqrt(Fixed xf) {
    int x = xf.value;
    if (x <= 0) {
        return fixedZero;
    }
    int r  = x;
    while (true) {
        int next = (r + x / r) >> 1;
        if (next >= r - 1 && next <= r + 1) {
            return buildFixed(next << 8);
        }
        r = next;
    }
}
