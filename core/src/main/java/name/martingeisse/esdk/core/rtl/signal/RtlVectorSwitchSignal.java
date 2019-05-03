package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlVectorSwitchSignal extends RtlSwitchSignal<RtlVectorSignal> implements RtlVectorSignal {

	private final int width;

	public RtlVectorSwitchSignal(RtlRealm realm, RtlVectorSignal selector, int width) {
		super(realm, selector);
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	protected void validateOnAdd(RtlVectorSignal branch) {
		if (branch.getWidth() != width) {
			throw new IllegalArgumentException("switch statement width is " + width + ", but branch width is " + branch.getWidth());
		}
	}

	@Override
	public VectorValue getValue() {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
