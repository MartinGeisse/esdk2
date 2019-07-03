package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlItem;

/**
 * This interface is implemented by things that can have a name in Verilog. It allows to statically type-check that
 * no wrong objects are passed to name assignment or name printing.
 */
public interface VerilogNamed {

    /**
     * If possible, returns a design item that correspons to this object. This is used to generate more readable
     * assigned names. If this method returns null, a more anonymous generated name is used.
     */
    RtlItem getVerilogNameSuggestionProvider();

}
