package name.martingeisse.esdk.core.rtl.synthesis.xilinx;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogNames;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

import java.io.PrintWriter;

public class FromThruToConstraint extends RtlItem implements UcfContributor {

    private RtlItem fromSignal;
    private RtlItem[] thruSignals;
    private RtlItem toSignal;
    private int nanoseconds;

    public FromThruToConstraint(RtlRealm realm) {
        super(realm);
    }

    public RtlItem getFromSignal() {
        return fromSignal;
    }

    public void setFromSignal(RtlItem fromSignal) {
        this.fromSignal = fromSignal;
    }

    public FromThruToConstraint from(RtlItem fromSignal) {
        setFromSignal(fromSignal);
        return this;
    }

    public RtlItem[] getThruSignals() {
        return thruSignals;
    }

    public void setThruSignals(RtlItem... thruSignals) {
        this.thruSignals = thruSignals;
    }

    public FromThruToConstraint thru(RtlItem... thruSignals) {
        setThruSignals(thruSignals);
        return this;
    }

    public RtlItem getToSignal() {
        return toSignal;
    }

    public void setToSignal(RtlItem toSignal) {
        this.toSignal = toSignal;
    }

    public FromThruToConstraint to(RtlItem toSignal) {
        setToSignal(toSignal);
        return this;
    }

    public void setNanoseconds(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    public FromThruToConstraint nanoseconds(int nanoseconds) {
        setNanoseconds(nanoseconds);
        return this;
    }

    @Override
    public VerilogContribution getVerilogContribution() {
        return new EmptyVerilogContribution();
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public void contributeToUcf(PrintWriter out, VerilogNames verilogNames) {
        String timespecName = verilogNames.assignGeneratedName(this);
        StringBuilder builder = new StringBuilder();
        builder.append("TIMESPEC \"TS_");
        builder.append(timespecName);
        builder.append("\" = ");
        if (fromSignal != null) {
            builder.append("FROM \"");
            builder.append(verilogNames.getName(fromSignal.getRtlItem()));
            builder.append("\" ");
        }
        if (thruSignals != null) {
            for (RtlItem thruSignal : thruSignals) {
                builder.append("THRU \"");
                builder.append(verilogNames.getName(thruSignal.getRtlItem()));
                builder.append("\" ");
            }
        }
        if (toSignal != null) {
            builder.append("TO \"");
            builder.append(verilogNames.getName(toSignal.getRtlItem()));
            builder.append("\" ");
        }
        builder.append(nanoseconds);
        builder.append(" ns;");
        out.println(builder);
    }

}
