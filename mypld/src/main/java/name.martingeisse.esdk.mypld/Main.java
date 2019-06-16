package name.martingeisse.esdk.mypld;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationCruiseControl;
import name.martingeisse.esdk.ui.SimpleBitWidget;
import name.martingeisse.esdk.ui.SimpleButton;
import name.martingeisse.esdk.ui.SimpleToggleButton;
import name.martingeisse.esdk.ui.VisualizerWindow;

public class Main {

    public static void main(String[] args) {
        Design design = new Design();
        RtlRealm realm = new RtlRealm(design);
        RtlClockNetwork clock = realm.createClockNetwork();

        RtlBitSignalConnector fast = new RtlBitSignalConnector(realm);

        RtlBitSignalConnector connector0 = new RtlBitSignalConnector(realm);
        RtlBitSignal register0 = RtlBuilder.bitRegister(clock, connector0.not(), false);
        connector0.setConnected(register0);

        RtlBitSignalConnector connector1 = new RtlBitSignalConnector(realm);
        RtlBitSignal register1 = RtlBuilder.bitRegister(clock, connector1.not(), register0, false);
        connector1.setConnected(register1);

        VisualizerWindow window = new VisualizerWindow(design, 20, 15, 3);
        window.add(2, 3, new SimpleBitWidget(register1));
        window.add(3, 3, new SimpleBitWidget(register0));
        window.add(5, 3, new SimpleBitWidget(fast.conditional(register0, register1)));
        SimpleButton button = new SimpleToggleButton(realm, "fast");
        window.add(3, 7, button);
        fast.setConnected(button.getSignal());

        window.show();
        new RtlClockGenerator(clock, 10);
        new RtlSimulationCruiseControl(realm, 1, 100);
        design.simulate();
    }

}
