package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.library.vga.MonitorPanel;

/**
 *
 */
public class MyMonitorPanel extends MonitorPanel {

	public MyMonitorPanel(RtlClockNetwork clock, TextDisplayController.Implementation display) {
		super(clock, 640, 480, 2);
		VgaConnector.Implementation vgaConnector = (VgaConnector.Implementation)display._vgaConnector;
		getMonitor().setR(vgaConnector.getR().asOneBitVector());
		getMonitor().setG(vgaConnector.getG().asOneBitVector());
		getMonitor().setB(vgaConnector.getB().asOneBitVector());
		getMonitor().setHsync(vgaConnector.getHsync());
		getMonitor().setVsync(vgaConnector.getVsync());
	}

}
