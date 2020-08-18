package name.martingeisse.esdk.library.util;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitRegister;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorRegister;
import name.martingeisse.esdk.core.rtl.block.statement.RtlBitAssignment;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.block.statement.RtlVectorAssignment;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.function.Function;

public final class RegisterBuilder {

    private RegisterBuilder() {
    }

    public static RtlBitSignal build(boolean initialValue, RtlClockNetwork clock, RtlBitSignal data) {
        return build(initialValue, clock, data, null);
    }

    public static RtlBitSignal build(boolean initialValue, RtlClockNetwork clock, RtlBitSignal data, RtlBitSignal enable) {
        RtlClockedBlock block = new RtlClockedBlock(clock);
        RtlProceduralBitRegister register = block.createBit(initialValue);
        RtlStatement assignment = new RtlBitAssignment(clock.getRealm(), register, data);
        if (enable == null) {
            block.getStatements().addStatement(assignment);
        } else {
            RtlWhenStatement when = new RtlWhenStatement(clock.getRealm(), enable);
            when.getThenBranch().addStatement(assignment);
            block.getStatements().addStatement(when);
        }
        return register;
    }

    public static RtlVectorSignal build(int width, VectorValue initialValue, RtlClockNetwork clock,
                                        RtlVectorSignal data) {
        return build(width, initialValue, clock, data, null);
    }

    public static RtlVectorSignal build(int width, VectorValue initialValue, RtlClockNetwork clock,
                                        RtlVectorSignal data, RtlBitSignal enable) {
        RtlClockedBlock block = new RtlClockedBlock(clock);
        RtlProceduralVectorRegister register = block.createVector(width, initialValue);
        RtlStatement assignment = new RtlVectorAssignment(clock.getRealm(), register, data);
        if (enable == null) {
            block.getStatements().addStatement(assignment);
        } else {
            RtlWhenStatement when = new RtlWhenStatement(clock.getRealm(), enable);
            when.getThenBranch().addStatement(assignment);
            block.getStatements().addStatement(when);
        }
        return register;
    }

    public static RtlVectorSignal build(int width, VectorValue initialValue, RtlClockNetwork clock,
                                        Function<RtlVectorSignal, RtlVectorSignal> dataBuilder) {
        return build(width, initialValue, clock, dataBuilder, null);
    }

        public static RtlVectorSignal build(int width, VectorValue initialValue, RtlClockNetwork clock,
                                        Function<RtlVectorSignal, RtlVectorSignal> dataBuilder,
                                        Function<RtlVectorSignal, RtlBitSignal> enableBuilder) {
        RtlVectorSignalConnector dataConnector = new RtlVectorSignalConnector(clock.getRealm(), width);
        RtlVectorSignal register = build(width, initialValue, clock,
                dataBuilder.apply(dataConnector),
                enableBuilder == null ? null : enableBuilder.apply(dataConnector));
        dataConnector.setConnected(register);
        return register;
    }


}
