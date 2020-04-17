
#ifndef FIXED_H
#define FIXED_H

struct Fixed {

    int value;

    inline Fixed() {
    }

};

inline Fixed buildFixed(int value) {
    Fixed result;
    result.value = value;
    return result;
}

inline Fixed buildFixed(signed short integral, unsigned short fractional) {
    return buildFixed((((int)integral) << 16) + fractional);
}

inline Fixed intToFixed(signed short integral) {
    return buildFixed(integral, 0);
}

inline int fixedToInt(Fixed f) {
    return f.value >> 16;
}

inline int fixedToFractionalPart(Fixed f) {
    return f.value & 0xffff;
}

inline Fixed operator-(Fixed a) {
    return buildFixed(-a.value);
}

inline Fixed operator+(Fixed a, Fixed b) {
    return buildFixed(a.value + b.value);
}

inline void operator+=(Fixed &a, Fixed b) {
    a.value += b.value;
}

inline Fixed operator-(Fixed a, Fixed b) {
    return buildFixed(a.value - b.value);
}

inline void operator-=(Fixed &a, Fixed b) {
    a.value -= b.value;
}

inline Fixed operator*(Fixed a, Fixed b) {
    int ah = a.value >> 16;
    int al = a.value & 0xffff;
    int bh = b.value >> 16;
    int bl = b.value & 0xffff;
    return buildFixed(
        ((ah * bh) << 16) +
        ah * bl +
        al * bh +
        (((al * bl) >> 16) & 0xffff) // explicit masking because I'm not sure how (signed * unsigned) behaves in C
    );
}

inline void operator*=(Fixed &a, Fixed b) {
    a = a * b;
}

inline Fixed operator/(Fixed a, Fixed b) {

    // Check for division by zero and emulate RISC-V behavior.
    if (b.value == 0) {
        return buildFixed(-1, -1);
    }

    // Handle sign, so the actual division is unsigned / unsigned.
    unsigned int aabs, babs;
    bool negative;
    if (a.value < 0) {
        if (b.value < 0) {
            aabs = -a.value;
            babs = -b.value;
            negative = false;
        } else {
            aabs = -a.value;
            babs = b.value;
            negative = true;
        }
    } else {
        if (b.value < 0) {
            aabs = a.value;
            babs = -b.value;
            negative = true;
        } else {
            aabs = a.value;
            babs = b.value;
            negative = false;
        }
    }

    // This loop takes 48 iterations. I think we might get this down to 32 if we accept that result overflow produces
    // a result that is different from the correct result truncated to 32 bits: The first 16 iterations produce bits
    // 32-47, so if no overflow occurs, these bits are zero and the current working dividend after those 16 bits is
    // still the original dividend.
    unsigned int workingDividend = 0;
    unsigned int result = 0;
    for (int i = 0; i < 48; i++) {
        workingDividend = (workingDividend << 1) | (aabs >> 31);
        aabs = aabs << 1;
        result = result << 1;
        if (workingDividend >= babs) {
            workingDividend -= babs;
            result++;
        }
    }

    // apply sign
    return buildFixed(negative ? -result : result);

}

inline void operator/=(Fixed &a, Fixed b) {
    a = a / b;
}

inline bool operator<(Fixed a, Fixed b) {
    return a.value < b.value;
}

inline bool operator<=(Fixed a, Fixed b) {
    return a.value <= b.value;
}

inline bool operator>(Fixed a, Fixed b) {
    return a.value > b.value;
}

inline bool operator>=(Fixed a, Fixed b) {
    return a.value >= b.value;
}

const Fixed fixedZero = buildFixed(0, 0);
const Fixed fixedOne = buildFixed(1, 0);
const Fixed fixedMinusOne = buildFixed(-1, 0);
const Fixed fixedEpsilon = buildFixed(0, 66); // 1/1000
const Fixed fixedMinusEpsilon = buildFixed(-1, 65536 - 66); // 1/1000

Fixed fixedSqrt(Fixed x);

#endif
