package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousRom;
import name.martingeisse.esdk.picoblaze.model.ProgramRomUtil;

/**
 *
 */
public class PicoblazeRtlWithAssociatedProgram extends PicoblazeRtl {

	private final RtlSynchronousRom rom;

	public PicoblazeRtlWithAssociatedProgram(RtlClockNetwork clockNetwork) {
		this(clockNetwork, null, null);
	}

	public PicoblazeRtlWithAssociatedProgram(RtlClockNetwork clockNetwork, Class<?> anchorClass) {
		this(clockNetwork, anchorClass, null);
	}

	public PicoblazeRtlWithAssociatedProgram(RtlClockNetwork clockNetwork, String programSuffix) {
		this(clockNetwork, null, programSuffix);
	}

	public PicoblazeRtlWithAssociatedProgram(RtlClockNetwork clockNetwork, Class<?> anchorClass, String programSuffix) {
		super(clockNetwork);
		if (anchorClass == null) {
			anchorClass = getClass();
		}
		rom = ProgramRomUtil.loadAssociatedProgramRom(clockNetwork, anchorClass, programSuffix);
		rom.setAddressSignal(getInstructionAddress());
		setInstructionSignal(rom.getReadDataSignal());
	}

}
