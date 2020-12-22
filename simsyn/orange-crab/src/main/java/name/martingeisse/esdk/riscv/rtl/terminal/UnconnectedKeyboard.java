package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * Does not return any key codes. Can be used for synthesis.
 */
public class UnconnectedKeyboard extends RtlItem implements KeyboardController {

    private final RtlBitSignal readySignal;
    private final RtlVectorSignal inputDataSignal;

    public UnconnectedKeyboard(RtlRealm realm) {
        super(realm);
        this.readySignal = new RtlBitConstant(realm, false);
        this.inputDataSignal = RtlVectorConstant.of(realm, 8, 0);
    }

    @Override
    public VerilogContribution getVerilogContribution() {
        return new EmptyVerilogContribution();
    }

    @Override
    public void setInputAcknowledge(RtlBitSignal inputAcknowledge) {
    }

    @Override
    public RtlBitSignal getReady() {
        return readySignal;
    }

    @Override
    public RtlVectorSignal getInputData() {
        return inputDataSignal;
    }

}
