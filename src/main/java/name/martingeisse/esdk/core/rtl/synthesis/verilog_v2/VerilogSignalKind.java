package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

/**
 *
 */
public enum VerilogSignalKind {

	WIRE, REG;

	public String getKeyword() {
		return name().toLowerCase();
	}

}
