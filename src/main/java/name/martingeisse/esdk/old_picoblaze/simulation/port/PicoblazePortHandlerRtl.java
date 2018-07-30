package name.martingeisse.esdk.old_picoblaze.simulation.port;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class PicoblazePortHandlerRtl implements PicoblazePortHandler {

	private final RtlVectorSignal readData;

	public PicoblazePortHandlerRtl(RtlVectorSignal readData) {
		if (readData.getWidth() != 8) {
			throw new IllegalArgumentException("read data width must be 8, is " + readData.getWidth());
		}
		this.readData = readData;
	}

	@Override
	public int handleInput(int address) {
		return readData.getValue().getAsUnsignedInt();
	}

	@Override
	public void handleOutput(int address, int value) {
	}

}
