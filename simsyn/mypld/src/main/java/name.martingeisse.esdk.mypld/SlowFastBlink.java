package name.martingeisse.esdk.mypld;

import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.ui.SimpleBitWidget;
import name.martingeisse.esdk.ui.SimpleButton;
import name.martingeisse.esdk.ui.SimpleToggleButton;

public class SlowFastBlink extends Example {

    public SlowFastBlink() {
        RtlBitSignalConnector fast = new RtlBitSignalConnector(realm);

        RtlBitSignalConnector connector0 = new RtlBitSignalConnector(realm);
        RtlBitSignal register0 = RtlBuilder.bitRegister(clock, connector0.not(), false);
        connector0.setConnected(register0);

        RtlBitSignalConnector connector1 = new RtlBitSignalConnector(realm);
        RtlBitSignal register1 = RtlBuilder.bitRegister(clock, connector1.not(), register0, false);
        connector1.setConnected(register1);

        window.add(2, 3, new SimpleBitWidget(register1));
        window.add(3, 3, new SimpleBitWidget(register0));
        window.add(5, 3, new SimpleBitWidget(fast.conditional(register0, register1)));
        SimpleButton button = new SimpleToggleButton(realm, "fast");
        window.add(3, 7, button);
        fast.setConnected(button.getSignal());

    }

    public static void main(String[] args) {
        new SlowFastBlink().run();
    }

}
