package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

public final class Random {

    public static void randomAutoSeederTick() {
        CpuProgramFragments.INSTANCE.randomAutoSeederTick();
    }

    public static void autoSeedRandom() {
        CpuProgramFragments.INSTANCE.autoSeedRandom();
    }

    // all invocations happen to be mod 7
    public static int nextRandomMod7() {
        CpuProgramFragments.INSTANCE.nextRandom();
        CpuProgramFragments.INSTANCE.randomMod7();
        return Devices.memory[MemoryMap.TEMP_0];
    }

    private Random() {
    }

}
