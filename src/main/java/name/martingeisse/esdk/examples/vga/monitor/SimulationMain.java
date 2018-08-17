package name.martingeisse.esdk.examples.vga.monitor;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.library.vga.Monitor;
import name.martingeisse.esdk.library.vga.MonitorPanel;

import javax.swing.*;

/**
 *
 */
public class SimulationMain {

	private static int x, y;
	private static boolean xblank, yblank;

	public static void main(String[] args) throws Exception {

		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		new RtlClockGenerator(clock, 10);

		RtlSettableBitSignal r = new RtlSettableBitSignal(realm);
		RtlSettableBitSignal g = new RtlSettableBitSignal(realm);
		RtlSettableBitSignal b = new RtlSettableBitSignal(realm);
		RtlSettableBitSignal hsync = new RtlSettableBitSignal(realm);
		RtlSettableBitSignal vsync = new RtlSettableBitSignal(realm);

		new IntervalItem(design, 5, 10, () -> {
			x++;
			if (x == 100) {
				xblank = true;
			} else if (x == 110) {
				hsync.setValue(false);
			} else if (x == 120) {
				hsync.setValue(true);
			} else if (x == 130) {
				xblank = false;
				x = 0;
				y++;
				if (y == 100) {
					yblank = true;
				} else if (y == 103) {
					vsync.setValue(false);
				} else if (y == 106) {
					vsync.setValue(true);
				} else if (y == 109) {
					yblank = false;
					y = 0;
				}
			}
			boolean active = !(xblank || yblank);
			r.setValue(active && ((x & 16) != 0));
			g.setValue(active && ((y & 16) != 0));
			b.setValue(active && (((x + y) & 32) != 0));
		});

		MonitorPanel monitorPanel = new MonitorPanel(clock, 120, 106, 1);
		Monitor monitor = monitorPanel.getMonitor();
		monitor.setR(r.repeat(8));
		monitor.setG(g.repeat(8));
		monitor.setB(b.repeat(8));
		monitor.setHsync(hsync);
		monitor.setVsync(vsync);

		JFrame frame = new JFrame("VGA Test Pattern");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		design.simulate();
	}

}
