package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class ComputerDesign extends Design {

    private final RtlRealm realm;
    private final RtlBitSignalConnector clockSignalConnector;
    private final RtlClockNetwork clock;
    private final RtlBitSignalConnector ddrClock0SignalConnector;
    private final RtlClockNetwork ddrClock0;
    private final RtlBitSignalConnector ddrClock90SignalConnector;
    private final RtlClockNetwork ddrClock90;
    private final RtlBitSignalConnector ddrClock180SignalConnector;
    private final RtlClockNetwork ddrClock180;
    private final RtlBitSignalConnector ddrClock270SignalConnector;
    private final RtlClockNetwork ddrClock270;
    private final ComputerModule.Implementation computerModule;

    public ComputerDesign() throws IOException {
        this.realm = new RtlRealm(this);
        this.clockSignalConnector = new RtlBitSignalConnector(realm);
        this.clock = realm.createClockNetwork(clockSignalConnector);
        this.ddrClock0SignalConnector = new RtlBitSignalConnector(realm);
        this.ddrClock0 = realm.createClockNetwork(ddrClock0SignalConnector);
        this.ddrClock90SignalConnector = new RtlBitSignalConnector(realm);
        this.ddrClock90 = realm.createClockNetwork(ddrClock90SignalConnector);
        this.ddrClock180SignalConnector = new RtlBitSignalConnector(realm);
        this.ddrClock180 = realm.createClockNetwork(ddrClock180SignalConnector);
        this.ddrClock270SignalConnector = new RtlBitSignalConnector(realm);
        this.ddrClock270 = realm.createClockNetwork(ddrClock270SignalConnector);
        this.computerModule = createComputerModule();
        // try (FileInputStream in = new FileInputStream("riscv/resource/program/build/program.bin")) {
        try (FileInputStream in = new FileInputStream("riscv/resource/gfx-program/build/program.bin")) {
            int index = 0;
            while (true) {
                int first = in.read();
                if (first < 0) {
                    break;
                }
                computerModule._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
                computerModule._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
                computerModule._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
                computerModule._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
                index++;
            }
        }
    }

    private static int readByteEofSafe(InputStream in) throws IOException {
        int x = in.read();
        return (x < 0 ? 0 : x);
    }

    protected ComputerModule.Implementation createComputerModule() {
        return new ComputerModule.Implementation(realm, clock, ddrClock0, ddrClock180, ddrClock270, ddrClock90);
    }

    public RtlRealm getRealm() {
        return realm;
    }

    public RtlBitSignalConnector getClockSignalConnector() {
        return clockSignalConnector;
    }

    public RtlClockNetwork getClock() {
        return clock;
    }

    public RtlBitSignalConnector getDdrClock0SignalConnector() {
        return ddrClock0SignalConnector;
    }

    public RtlClockNetwork getDdrClock0() {
        return ddrClock0;
    }

    public RtlBitSignalConnector getDdrClock90SignalConnector() {
        return ddrClock90SignalConnector;
    }

    public RtlClockNetwork getDdrClock90() {
        return ddrClock90;
    }

    public RtlBitSignalConnector getDdrClock180SignalConnector() {
        return ddrClock180SignalConnector;
    }

    public RtlClockNetwork getDdrClock180() {
        return ddrClock180;
    }

    public RtlBitSignalConnector getDdrClock270SignalConnector() {
        return ddrClock270SignalConnector;
    }

    public RtlClockNetwork getDdrClock270() {
        return ddrClock270;
    }

    public ComputerModule.Implementation getComputerModule() {
        return computerModule;
    }

}
