package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public interface Ps2Connector extends RtlItemOwned {

	RtlBitSignal getClk();
	RtlBitSignal getData();

	class Implementation extends RtlItem implements Ps2Connector {

		private final RtlBitSignalConnector clk, data;

		public Implementation(RtlRealm realm) {
			super(realm);
			clk = new RtlBitSignalConnector(realm);
			clk.setName("ps2Connector.clk");
			data = new RtlBitSignalConnector(realm);
			data.setName("ps2Connector.data");
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

	class Unconnected extends RtlItem implements Ps2Connector {

		private final RtlBitConstant clk, data;

		public Unconnected(RtlRealm realm) {
			super(realm);
			clk = new RtlBitConstant(realm, false);
			clk.setName("ps2Connector.clk");
			data = new RtlBitConstant(realm, false);
			data.setName("ps2Connector.data");
		}

		public RtlBitConstant getClk() {
			return clk;
		}

		public RtlBitConstant getData() {
			return data;
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

	}

}
