package name.martingeisse.esdk.mypld;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.ui.SimpleBitWidget;

public class OneDigitDecimalCounter extends Example {

    public OneDigitDecimalCounter() {

        RtlBitSignalConnector bit0connector = new RtlBitSignalConnector(realm);
        RtlBitSignalConnector bit1connector = new RtlBitSignalConnector(realm);
        RtlBitSignalConnector bit2connector = new RtlBitSignalConnector(realm);
        RtlBitSignalConnector bit3connector = new RtlBitSignalConnector(realm);

        bit0connector.setConnected(SimulatedLogicUnit.buildClocked(clock, 0x1, bit0connector));
        bit1connector.setConnected(SimulatedLogicUnit.buildClocked(clock, 0x6, bit0connector, bit1connector));
        bit2connector.setConnected(SimulatedLogicUnit.buildClocked(clock, 0x78, bit0connector, bit1connector, bit2connector));
        bit3connector.setConnected(SimulatedLogicUnit.buildClocked(clock, 0x7f80, bit0connector, bit1connector, bit2connector, bit3connector));

        window.add(1, 3, new SimpleBitWidget(bit3connector));
        window.add(2, 3, new SimpleBitWidget(bit2connector));
        window.add(3, 3, new SimpleBitWidget(bit1connector));
        window.add(4, 3, new SimpleBitWidget(bit0connector));

        RtlBitSignal segmentT = SimulatedLogicUnit.buildClocked(clock, 0xc7ed, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentTL = SimulatedLogicUnit.buildClocked(clock, 0xcf71, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentTR = SimulatedLogicUnit.buildClocked(clock, 0x279f, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentM = SimulatedLogicUnit.buildClocked(clock, 0xff7c, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentBL = SimulatedLogicUnit.buildClocked(clock, 0xfd45, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentBR = SimulatedLogicUnit.buildClocked(clock, 0x2ffb, bit0connector, bit1connector, bit2connector, bit3connector);
        RtlBitSignal segmentB = SimulatedLogicUnit.buildClocked(clock, 0x7b6d, bit0connector, bit1connector, bit2connector, bit3connector);

        window.add(7, 1, new SimpleBitWidget(segmentT));
        window.add(8, 1, new SimpleBitWidget(segmentT));
        window.add(6, 2, new SimpleBitWidget(segmentTL));
        window.add(6, 3, new SimpleBitWidget(segmentTL));
        window.add(6, 4, new SimpleBitWidget(segmentTL));
        window.add(9, 2, new SimpleBitWidget(segmentTR));
        window.add(9, 3, new SimpleBitWidget(segmentTR));
        window.add(9, 4, new SimpleBitWidget(segmentTR));
        window.add(7, 5, new SimpleBitWidget(segmentM));
        window.add(8, 5, new SimpleBitWidget(segmentM));
        window.add(6, 6, new SimpleBitWidget(segmentBL));
        window.add(6, 7, new SimpleBitWidget(segmentBL));
        window.add(6, 8, new SimpleBitWidget(segmentBL));
        window.add(9, 6, new SimpleBitWidget(segmentBR));
        window.add(9, 7, new SimpleBitWidget(segmentBR));
        window.add(9, 8, new SimpleBitWidget(segmentBR));
        window.add(7, 9, new SimpleBitWidget(segmentB));
        window.add(8, 9, new SimpleBitWidget(segmentB));

    }

    public static void main(String[] args) {
        new OneDigitDecimalCounter().run();
    }

}
