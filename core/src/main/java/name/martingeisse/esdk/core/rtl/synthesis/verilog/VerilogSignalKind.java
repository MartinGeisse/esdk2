package name.martingeisse.esdk.core.rtl.synthesis.verilog;

/**
 *
 */
public enum VerilogSignalKind {

	WIRE, REG;

	public String getKeyword() {
		return name().toLowerCase();
	}

}
