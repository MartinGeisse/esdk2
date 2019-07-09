package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.library.vga.MonitorPanel;

/**
 *
 */
public class MyMonitorPanel extends MonitorPanel {

	public MyMonitorPanel(RtlClockNetwork clock, TextDisplayController.Implementation display) {
		super(clock, 640, 480, 2);
		VgaConnector.Connector vgaConnector = (VgaConnector.Connector)display._vgaConnector;
		getMonitor().setR(vgaConnector.getRSocket().asOneBitVector());
		getMonitor().setG(vgaConnector.getGSocket().asOneBitVector());
		getMonitor().setB(vgaConnector.getBSocket().asOneBitVector());
		getMonitor().setHsync(vgaConnector.getHsyncSocket());
		getMonitor().setVsync(vgaConnector.getVsyncSocket());
	}

}
