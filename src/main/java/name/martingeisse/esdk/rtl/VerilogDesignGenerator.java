/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public class VerilogDesignGenerator {

	private final VerilogWriter out;
	private final RtlDesign design;
	private final String name;

	public VerilogDesignGenerator(VerilogWriter out, RtlDesign design, String name) {
		this.out = out;
		this.design = design;
		this.name = name;
	}

	public void generate() {
		out.writeIntro(name);
		out.writeOutro();
	}

}
