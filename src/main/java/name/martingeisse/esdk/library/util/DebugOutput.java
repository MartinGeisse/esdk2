package name.martingeisse.esdk.library.util;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 *
 */
public class DebugOutput extends RtlClockedItem {

	private final RtlVectorSignal dataSignal;
	private final RtlBitSignal enableSignal;
	private final Callback callback;
	private int data;
	private boolean enable;

	public DebugOutput(RtlClockNetwork clockNetwork, RtlVectorSignal dataSignal, RtlBitSignal enableSignal, Callback callback) {
		super(clockNetwork);
		this.dataSignal = dataSignal;
		this.enableSignal = enableSignal;
		this.callback = callback;
	}

	@Override
	public void computeNextState() {
		data = dataSignal.getValue().getBitsAsInt();
		enable = enableSignal.getValue();
	}

	@Override
	public void updateState() {
		if (enable) {
			callback.handle(data);
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return EmptyVerilogContribution.INSTANCE;
	}

	public interface Callback {
		void handle(int value);
	}

}
