package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public interface VgaConnector extends RtlItemOwned {
	void setR(RtlBitSignal r);
	void setG(RtlBitSignal g);
	void setB(RtlBitSignal b);
	void setHsync(RtlBitSignal hsync);
	void setVsync(RtlBitSignal vsync);

	class Implementation extends RtlItem implements VgaConnector {

		private RtlBitSignal r, g, b, hsync, vsync;

		public Implementation(RtlRealm realm) {
			super(realm);
		}

		public RtlBitSignal getR() {
			return r;
		}

		public void setR(RtlBitSignal r) {
			this.r = r;
		}

		public RtlBitSignal getG() {
			return g;
		}

		public void setG(RtlBitSignal g) {
			this.g = g;
		}

		public RtlBitSignal getB() {
			return b;
		}

		public void setB(RtlBitSignal b) {
			this.b = b;
		}

		public RtlBitSignal getHsync() {
			return hsync;
		}

		public void setHsync(RtlBitSignal hsync) {
			this.hsync = hsync;
		}

		public RtlBitSignal getVsync() {
			return vsync;
		}

		public void setVsync(RtlBitSignal vsync) {
			this.vsync = vsync;
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

	}

}
