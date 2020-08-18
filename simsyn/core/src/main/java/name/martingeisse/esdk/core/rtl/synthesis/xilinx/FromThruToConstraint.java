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

        if (fromSignal != null) {
            builder.append("NET \"").append(verilogNames.getName(fromSignal.getRtlItem()))
                    .append("\" TNM = ").append(timespecName).append("_FROM;\n");
        }
        if (thruSignals != null) {
            for (int i = 0; i < thruSignals.length; i++) {
                builder.append("NET \"").append(verilogNames.getName(thruSignals[i].getRtlItem()))
                        .append("\" TNM = ").append(timespecName).append("_THRU_").append(i).append(";\n");
            }
        }
        if (toSignal != null) {
            builder.append("NET \"").append(verilogNames.getName(toSignal.getRtlItem()))
                    .append("\" TNM = ").append(timespecName).append("_TO;\n");
        }

        builder.append("TIMESPEC \"TS_");
        builder.append(timespecName);
        builder.append("\" = ");
        if (fromSignal != null) {
            builder.append("FROM \"").append(timespecName).append("_FROM\" ");
        }
        if (thruSignals != null) {
            for (int i = 0; i < thruSignals.length; i++) {
                builder.append("THRU \"").append(timespecName).append("_THRU_").append(i).append("\" ");
            }
        }
        if (toSignal != null) {
            builder.append("TO \"").append(timespecName).append("_TO\" ");
        }
        builder.append(nanoseconds);
        builder.append(" ns;");
        out.println(builder);
    }

}
