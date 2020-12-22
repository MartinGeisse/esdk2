package name.martingeisse.esdk.riscv.tools.levelgen;

import name.martingeisse.esdk.riscv.tools.levelgen.geometry.Plane3;

public class CollisionPlane {

    private Plane3 plane;
    private Sector targetSector;

    public CollisionPlane(Plane3 plane) {
        this.plane = plane;
    }

    public CollisionPlane(Plane3 plane, Sector targetSector) {
        this.plane = plane;
        this.targetSector = targetSector;
    }

    public Plane3 getPlane() {
        return plane;
    }

    public void setPlane(Plane3 plane) {
        this.plane = plane;
    }

    public Sector getTargetSector() {
        return targetSector;
    }

    public void setTargetSector(Sector targetSector) {
        this.targetSector = targetSector;
    }

}
