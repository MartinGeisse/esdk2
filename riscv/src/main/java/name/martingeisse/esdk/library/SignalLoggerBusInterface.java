package name.martingeisse.esdk.library;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public interface SignalLoggerBusInterface extends RtlItemOwned {

	void setBusEnable(RtlBitSignal busEnable);

	void setBusWrite(RtlBitSignal busWrite);

	void setBusWriteData(RtlVectorSignal busWriteData);

	RtlVectorSignal getBusReadData();

	RtlBitSignal getBusAcknowledge();

	class Implementation extends RtlItem implements SignalLoggerBusInterface {

		private RtlBitSignal busEnable;
		private RtlBitSignal busWrite;
		private RtlVectorSignal busWriteData;
		private final RtlVectorSignalConnector busReadData;
		private final RtlBitSignalConnector busAcknowledge;

		public Implementation(RtlRealm realm) {
			super(realm);
			busReadData = new RtlVectorSignalConnector(realm, 32);
			busAcknowledge = new RtlBitSignalConnector(realm);
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		public RtlBitSignal getBusEnable() {
			return busEnable;
		}

		@Override
		public void setBusEnable(RtlBitSignal busEnable) {
			this.busEnable = busEnable;
		}

		public RtlBitSignal getBusWrite() {
			return busWrite;
		}

		@Override
		public void setBusWrite(RtlBitSignal busWrite) {
			this.busWrite = busWrite;
		}

		public RtlVectorSignal getBusWriteData() {
			return busWriteData;
		}

		@Override
		public void setBusWriteData(RtlVectorSignal busWriteData) {
			this.busWriteData = busWriteData;
		}

		@Override
		public RtlVectorSignalConnector getBusReadData() {
			return busReadData;
		}

		public void setBusReadData(RtlVectorSignal busReadData) {
			this.busReadData.setConnected(busReadData);
		}

		@Override
		public RtlBitSignalConnector getBusAcknowledge() {
			return busAcknowledge;
		}

		public void setBusAcknowledge(RtlBitSignal busAcknowledge) {
			this.busAcknowledge.setConnected(busAcknowledge);
		}

	}

}
