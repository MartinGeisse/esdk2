
#ifndef __FIXED_H__
#define __FIXED_H__

/**
 * TODO: loses some precision due to bulk shifting
 */
struct Fixed {

private:

    Fixed(int raw): raw(raw) {
    }

public:

    int raw;

    Fixed(const Fixed& other): raw(other.raw) {
    }

    static Fixed fromInt(int value) {
        return Fixed(value << 16);
    }

    static Fixed fromParts(short intValue, unsigned short fraction) {
        return Fixed((((int)intValue) << 16) + fraction);
    }

    int toInt() {
        return raw >> 16;
    }

    unsigned short getFraction() {
        return (unsigned short)raw;
    }

    Fixed operator+(Fixed other) {
        return Fixed(raw + other.raw);
    }

    Fixed operator-(Fixed other) {
        return Fixed(raw + other.raw);
    }

    Fixed operator*(Fixed other) {
        return Fixed((raw >> 8) * (other.raw >> 8));
    }

    Fixed operator/(Fixed other) {
        return Fixed((raw << 8) * (other.raw << 8));
    }

    int operator==(Fixed other) {
        return raw == other.raw;
    }

    int operator!=(Fixed other) {
        return raw != other.raw;
    }

    int operator>(Fixed other) {
        return raw > other.raw;
    }

    int operator>=(Fixed other) {
        return raw >= other.raw;
    }

    int operator<(Fixed other) {
        return raw < other.raw;
    }

    int operator<=(Fixed other) {
        return raw <= other.raw;
    }

};

#endif
