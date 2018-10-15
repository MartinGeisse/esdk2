package name.martingeisse.esdk.library.util.log;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 * This module accepts data as a stream of bytes which gets logged to System.out during simulation. Synthesis turns
 * it into a no-op to allow working with an unmodified design.
 * <p>
 * Data is typed. Together with every byte, a type signal determines how the data is interpreted. The type signal
 * does not have a fixed expected width, so additional type numbers can be allocated just by making that signal wider.
 * The type signal is interpreted as an unsigned integer, then that integer gets interpreted by a
 * {@link LogDataInterpretation}. See {@link DefaultLogDataInterpretation} for a useful default interpretation strategy.
 */
public class SimulationLogger extends RtlClockedItem {

	private final LogDataInterpretation dataInterpretation;

	private RtlBitSignal enableSignal;
	private RtlVectorSignal dataSignal;
	private RtlVectorSignal typeSignal;

	private boolean sampledEnable;
	private int sampledData;
	private int sampledType;

	public SimulationLogger(RtlClockNetwork clockNetwork, LogDataInterpretation dataInterpretation) {
		super(clockNetwork);
		if (dataInterpretation == null) {
			throw new IllegalArgumentException("dataInterpretation is null");
		}
		this.dataInterpretation = dataInterpretation;
	}

	public LogDataInterpretation getDataInterpretation() {
		return dataInterpretation;
	}

	public RtlBitSignal getEnableSignal() {
		return enableSignal;
	}

	public void setEnableSignal(RtlBitSignal enableSignal) {
		this.enableSignal = enableSignal;
	}

	public RtlVectorSignal getDataSignal() {
		return dataSignal;
	}

	public void setDataSignal(RtlVectorSignal dataSignal) {
		if (dataSignal != null && dataSignal.getWidth() != 8) {
			throw new IllegalArgumentException("data must have a width of 8 bits, was " + dataSignal.getWidth());
		}
		this.dataSignal = dataSignal;
	}

	public RtlVectorSignal getTypeSignal() {
		return typeSignal;
	}

	public void setTypeSignal(RtlVectorSignal typeSignal) {
		this.typeSignal = typeSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		if (enableSignal == null) {
			enableSignal = new RtlBitConstant(getRealm(), true);
		}
		if (dataSignal == null) {
			throw new IllegalStateException("no data signal");
		}
		if (typeSignal == null) {
			typeSignal = RtlVectorConstant.ofUnsigned(getRealm(), 0, 0);
		}
	}

	@Override
	public void computeNextState() {
		sampledEnable = enableSignal.getValue();
		sampledData = dataSignal.getValue().getAsUnsignedInt();
		sampledType = typeSignal.getValue().getAsUnsignedInt();
	}

	@Override
	public void updateState() {
		if (sampledEnable) {
			dataInterpretation.consume(sampledData, sampledType);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
