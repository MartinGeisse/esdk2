package name.martingeisse.esdk.mypld;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationCruiseControl;
import name.martingeisse.esdk.ui.VisualizerWindow;

public class Example extends Design {

    public final RtlRealm realm;
    public final RtlClockNetwork clock;
    public final VisualizerWindow window;

    public Example() {
        realm = new RtlRealm(this);
        clock = realm.createClockNetwork();
        window = new VisualizerWindow(this, 20, 15, 1);
    }

    public void run() {
        window.show();
        new RtlClockGenerator(clock, 10);
        new RtlSimulationCruiseControl(realm, 1, 100);
        simulate();
    }

}
