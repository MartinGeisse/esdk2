package name.martingeisse.esdk.riscv.tools.levelgen;

import name.martingeisse.esdk.riscv.tools.levelgen.geometry.Vector3;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPolygon {

    private final List<Vector3> vertices = new ArrayList<>();

    public List<Vector3> getVertices() {
        return vertices;
    }

    public void addVertex(Vector3 v) {
        vertices.add(v);
    }

    public void addVertex(double x, double y, double z) {
        addVertex(new Vector3(x, y, z));
    }

}
