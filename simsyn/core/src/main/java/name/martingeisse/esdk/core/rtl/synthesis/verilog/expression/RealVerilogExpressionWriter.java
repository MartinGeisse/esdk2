package name.martingeisse.esdk.core.rtl.synthesis.verilog.expression;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

import java.io.PrintWriter;

/**
 * Base class for "real" writers that actually write expressions (as opposed to "fake" writers).
 */
public abstract class RealVerilogExpressionWriter implements VerilogExpressionWriter {

    protected final PrintWriter out;

    public RealVerilogExpressionWriter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public final VerilogExpressionWriter print(String s) {
        out.print(s);
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(int i) {
        out.print(i);
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(char c) {
        out.print(c);
        return this;
    }

    @Override
    public final VerilogExpressionWriter printSignal(RtlSignal signal, VerilogExpressionNesting nesting) {
        internalPrintSignal(signal, nesting);
        return this;
    }

    @Override
    public final VerilogExpressionWriter printMemory(RtlProceduralMemory memory) {
        internalPrintMemory(memory);
        return this;
    }

    protected abstract void internalPrintSignal(RtlSignal signal, VerilogExpressionNesting nesting);
    protected abstract void internalPrintMemory(RtlProceduralMemory memory);

}
