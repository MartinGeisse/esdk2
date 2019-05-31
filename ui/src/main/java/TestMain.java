import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;

/**
 *
 */
public class TestMain {

	public static void main(String[] args) {
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork();

		RtlBitSignalConnector connector = new RtlBitSignalConnector(realm);
		RtlBitSignal register = RtlBuilder.bitRegister(clock, connector.not(), false);
		connector.setConnected(register);

		VisualizerWindow window = new VisualizerWindow(design, 20, 15, 1);
		window.add(3, 3, new SimpleBitWidget(register));
		window.show();
		new RtlClockGenerator(clock, 10);
		design.simulate();
	}

}
