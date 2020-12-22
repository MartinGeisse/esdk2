package name.martingeisse.esdk.riscv.tools.levelgen;

import name.martingeisse.esdk.riscv.tools.levelgen.geometry.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Sector {

    private final List<Portal> portals = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final List<CollisionPlane> collisionPlanes = new ArrayList<>();

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void addLine(Vector3 a, Vector3 b) {
        lines.add(new Line(a, b));
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void addPortal(Portal portal) {
        portals.add(portal);
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public List<CollisionPlane> getCollisionPlanes() {
        return collisionPlanes;
    }

    public void addCollisionPlane(CollisionPlane collisionPlane) {
        collisionPlanes.add(collisionPlane);
    }

}
