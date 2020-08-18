package name.martingeisse.esdk.core.rtl.synthesis.verilog;

/**
 *
 */
public enum VerilogSignalDeclarationKeyword {

	NONE, WIRE, REG;

	public String getKeyword() {
		if (this == NONE) {
			throw new UnsupportedOperationException();
		}
		return name().toLowerCase();
	}

}
