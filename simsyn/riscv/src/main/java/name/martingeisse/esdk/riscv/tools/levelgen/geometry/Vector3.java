/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.tools.levelgen.geometry;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public final class Vector3 {

    public final double x;
    public final double y;
    public final double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vector3) {
            Vector3 otherVector = (Vector3) other;
            return (x == otherVector.x && y == otherVector.y && z == otherVector.z);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(x).append(y).append(z).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{Vector3 ");
        builder.append("x = ").append(x);
        builder.append(", y = ").append(y);
        builder.append(", z = ").append(z);
        builder.append('}');
        return builder.toString();
    }

    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 multiply(double a) {
        return new Vector3(a * x, a * y, a * z);
    }

    public Vector3 divide(double a) {
        return new Vector3(a / x, a / y, a / z);
    }

    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double normSquared() {
        return x * x + y * y + z * z;
    }

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public Vector3 normalize() {
        return divide(norm());
    }

}
