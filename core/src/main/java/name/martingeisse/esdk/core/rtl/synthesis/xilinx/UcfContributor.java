package name.martingeisse.esdk.core.rtl.synthesis.xilinx;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogNames;

import java.io.PrintWriter;

public interface UcfContributor {

    void contributeToUcf(PrintWriter out, VerilogNames verilogNames);

}
