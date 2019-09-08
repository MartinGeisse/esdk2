
#ifndef __VECTOR3_H__
#define __VECTOR3_H__

#include "fixed.h"

struct Vector3 {

    int x, y, z;

    Vector3(int x, int y, int z): x(x), y(y), z(z) {
    }

    Vector3(const Vector3 &other): x(other.x), y(other.y), z(other.z) {
    }

    Vector3 operator+(const Vector3 &other) {
        return Vector3(x + other.x, y + other.y, z + other.z);
    }

    Vector3 operator-(const Vector3 &other) {
        return Vector3(x - other.x, y - other.y, z - other.z);
    }

    int operator*(const Vector3 &other) {
        return x * other.x + y * other.y + z * other.z;
    }

    Vector3 operator%(const Vector3 &other) {
        return Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    int norm() {

    }

};

#endif
