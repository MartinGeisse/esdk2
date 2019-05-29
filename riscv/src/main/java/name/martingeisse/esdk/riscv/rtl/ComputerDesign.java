package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 *
 */
public class ComputerDesign extends Design {

    private final RtlRealm realm;
    private final RtlClockNetwork clock;
    private final ComputerModule.Implementation computerModule;

    public ComputerDesign(Function<RtlRealm, RtlBitSignal> clockPinFactory) throws IOException {
        this.realm = new RtlRealm(this);
        if (clockPinFactory == null) {
            this.clock = realm.createClockNetwork();
        } else {
            this.clock = realm.createClockNetwork(clockPinFactory.apply(realm));
        }
        this.computerModule = createComputerModule();
        try (FileInputStream in = new FileInputStream("riscv/resource/program/build/program.bin")) {
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
        return new ComputerModule.Implementation(realm, clock);
    }

    public RtlRealm getRealm() {
        return realm;
    }

    public RtlClockNetwork getClock() {
        return clock;
    }

    public ComputerModule.Implementation getComputerModule() {
        return computerModule;
    }

}
