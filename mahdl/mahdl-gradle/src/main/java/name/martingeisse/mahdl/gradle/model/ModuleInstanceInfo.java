package name.martingeisse.mahdl.gradle.model;

import name.martingeisse.mahdl.common.processor.definition.ModuleInstance;

import java.util.List;

/**
 * Note: "local clocks to pass" are sorted by name of the clock port, NOT by name of the local clock signal
 * (that's why it is a list and not a sorted set).
 */
public final class ModuleInstanceInfo {

	private final ModuleInstance moduleInstance;
	private final String canonicalModuleName;
	private final List<String> localClocksToPass;

	public ModuleInstanceInfo(ModuleInstance moduleInstance, String canonicalModuleName, List<String> localClocksToPass) {
		this.moduleInstance = moduleInstance;
		this.canonicalModuleName = canonicalModuleName;
		this.localClocksToPass = localClocksToPass;
	}

	public ModuleInstance getModuleInstance() {
		return moduleInstance;
	}

	public String getCanonicalModuleName() {
		return canonicalModuleName;
	}

	public List<String> getLocalClocksToPass() {
		return localClocksToPass;
	}

}
