package name.martingeisse.esdk.riscv.tools.levelgen.geometry;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public final class Plane3 {

    private final double a, b, c, d;

    public Plane3(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public static Plane3 fromPoints(Vector3 a, Vector3 b, Vector3 c) {
        Vector3 n = c.subtract(a).cross(b.subtract(a)).normalize();
        return new Plane3(n.x, n.y, n.z, -n.dot(a));
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Plane3) {
            Plane3 otherPlane = (Plane3) other;
            return (a == otherPlane.a && b == otherPlane.b && c == otherPlane.c && d == otherPlane.d);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(a).append(b).append(c).append(d).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{Plane3 ");
        builder.append("a = ").append(a);
        builder.append(", b = ").append(b);
        builder.append(", c = ").append(c);
        builder.append(", d = ").append(d);
        builder.append('}');
        return builder.toString();
    }

    public double getScale() {
        return Math.sqrt(a * a + b * b + c * c);
    }

    public Plane3 normalize() {
        double scale = getScale();
        return new Plane3(a / scale, b / scale, c / scale, d / scale);
    }

    public double evaluate(Vector3 v) {
        return a * v.x + b * v.y + c * v.z + d;
    }

    // note: only works if normalized!
    public Vector3 projectPoint(Vector3 p) {
        double v = evaluate(p);
        return new Vector3(p.x - v * a, p.y - v * b, p.z - v * c);
    }

}
