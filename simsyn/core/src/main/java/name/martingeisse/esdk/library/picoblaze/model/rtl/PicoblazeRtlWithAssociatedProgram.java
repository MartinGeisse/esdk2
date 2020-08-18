package name.martingeisse.esdk.library.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.library.picoblaze.model.ProgramRomUtil;

/**
 *
 */
public class PicoblazeRtlWithAssociatedProgram extends PicoblazeRtl {

	private final RtlMemory rom;
	private final RtlSynchronousMemoryPort romPort;

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
		rom = ProgramRomUtil.loadAssociatedProgramRom(clockNetwork.getRealm(), anchorClass, programSuffix);
		romPort = rom.createSynchronousPort(clockNetwork, RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS);
		romPort.setAddressSignal(getInstructionAddress());
		setInstructionSignal(romPort.getReadDataSignal());
	}

}
