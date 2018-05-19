/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlOutputPin extends RtlPin {

	private RtlSignal outputSignal;

	public RtlOutputPin(RtlDesign design) {
		super(design);
	}

	public RtlSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlSignal outputSignal) {
		this.outputSignal = outputSignal;
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "output";
	}

}
