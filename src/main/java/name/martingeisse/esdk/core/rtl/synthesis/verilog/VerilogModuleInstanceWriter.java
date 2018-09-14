package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public class VerilogModuleInstanceWriter {

	private final VerilogWriter out;
	private boolean firstPort;

	public VerilogModuleInstanceWriter(VerilogWriter out) {
		this.out = out;
	}

	public void beginInstance(String moduleName) {
		out.indent();
		out.getOut().print(moduleName);
		out.getOut().print(" ");
		out.getOut().print(out.newInstanceName());
		out.getOut().print("(");
		out.startIndentation();
		firstPort = true;
	}

	public void assignPort(String portName, RtlSignal signal) {
		if (firstPort) {
			firstPort = false;
			out.getOut().println();
		} else {
			out.getOut().println(',');
		}
		out.indent();
		out.getOut().print('.');
		out.getOut().print(portName);
		out.getOut().print('(');
		out.printExpression(signal);
		out.getOut().print(')');
	}

	public void endInstance() {
		out.getOut().println();
		out.endIndentation();
		out.indent();
		out.getOut().println(");");
	}

}
