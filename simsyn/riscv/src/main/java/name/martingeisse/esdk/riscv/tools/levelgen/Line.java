package name.martingeisse.esdk.riscv.tools.levelgen;

import name.martingeisse.esdk.riscv.tools.levelgen.geometry.Vector3;

public class Line {

    private final Vector3 a, b;

    public Line(Vector3 a, Vector3 b) {
        this.a = a;
        this.b = b;
    }

    public Vector3 getA() {
        return a;
    }

    public Vector3 getB() {
        return b;
    }

}
