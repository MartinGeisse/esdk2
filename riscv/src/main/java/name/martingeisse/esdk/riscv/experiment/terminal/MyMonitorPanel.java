package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.library.vga.MonitorPanel;

/**
 *
 */
public class MyMonitorPanel extends MonitorPanel {

	public MyMonitorPanel(RtlClockNetwork clock, TextDisplayController display) {
		super(clock, 640, 480, 2);
		getMonitor().setR(display.vgaConnector.getR().asOneBitVector());
		getMonitor().setG(display.vgaConnector.getG().asOneBitVector());
		getMonitor().setB(display.vgaConnector.getB().asOneBitVector());
		getMonitor().setHsync(display.vgaConnector.getHsync());
		getMonitor().setVsync(display.vgaConnector.getVsync());
	}

}
