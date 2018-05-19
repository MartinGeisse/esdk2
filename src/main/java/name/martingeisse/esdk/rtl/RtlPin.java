/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public abstract class RtlPin extends RtlItem {

	private String id;
	private RtlPinConfiguration configuration;

	public RtlPin(RtlDesign design) {
		super(design);
		design.registerPin(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RtlPinConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(RtlPinConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getNetName() {
		return "pin" + id;
	}

	public abstract String getVerilogDirectionKeyword();

	public void writeModuleLocalDeclarations(VerilogWriter out) {
		// TODO
	}

}
