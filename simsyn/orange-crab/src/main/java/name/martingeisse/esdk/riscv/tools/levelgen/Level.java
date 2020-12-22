package name.martingeisse.esdk.riscv.tools.levelgen;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private final List<Sector> sectors = new ArrayList<>();

    public List<Sector> getSectors() {
        return sectors;
    }

    public Sector newSector() {
        Sector sector = new Sector();
        sectors.add(sector);
        return sector;
    }

}
