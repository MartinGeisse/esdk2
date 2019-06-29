package name.martingeisse.esdk.core.rtl.synthesis.verilog.expression;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 * Base class for "fake" writers that re-use the verilog writing routines to analyze signal usage. This avoids
 * duplicate code and helps to avoid subtle bugs due to the analysis being wrong.
 */
public abstract class FakeVerilogExpressionWriter implements VerilogExpressionWriter {

    @Override
    public final VerilogExpressionWriter print(String s) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(int i) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(char c) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter printSignal(RtlSignal signal, VerilogExpressionNesting nesting) {
        visitSignal(signal, nesting);
        return this;
    }

    @Override
    public final VerilogExpressionWriter printMemory(RtlProceduralMemory memory) {
        visitMemory(memory);
        return this;
    }

    protected abstract void visitSignal(RtlSignal signal, VerilogExpressionNesting nesting);
    protected abstract void visitMemory(RtlProceduralMemory memory);

}
