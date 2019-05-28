package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public interface Ps2Connector extends RtlItemOwned {

	RtlBitSignalConnector getClk();
	RtlBitSignalConnector getData();

	class Implementation extends RtlItem implements Ps2Connector {

		private final RtlBitSignalConnector clk, data;

		public Implementation(RtlRealm realm) {
			super(realm);
			clk = new RtlBitSignalConnector(realm);
			data = new RtlBitSignalConnector(realm);
		}

		public RtlBitSignalConnector getClk() {
			return clk;
		}

		public void setClk(RtlBitSignal clk) {
			this.clk.setConnected(clk);
		}

		public RtlBitSignalConnector getData() {
			return data;
		}

		public void setData(RtlBitSignal data) {
			this.data.setConnected(data);
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

	}

}
