package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlMultiportMemory;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.picoblaze.model.ProgramRomUtil;

/**
 *
 */
public class PicoblazeRtlWithAssociatedProgram extends PicoblazeRtl {

	private final RtlMultiportMemory rom;
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
