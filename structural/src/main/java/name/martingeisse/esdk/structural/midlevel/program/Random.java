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
    public static int getRandomMod7() {
        CpuProgramFragments.INSTANCE.getRandom();
        CpuProgramFragments.INSTANCE.randomMod7();

        int currentNumber = (Devices.memory[MemoryMap.TEMP_0] & 0xff);
        currentNumber |= (Devices.memory[MemoryMap.TEMP_1] & 0xff) << 8;
        currentNumber |= (Devices.memory[MemoryMap.TEMP_2] & 0xff) << 16;
        currentNumber |= (Devices.memory[MemoryMap.TEMP_3] & 0xff) << 24;
        return currentNumber;
    }

    private Random() {
    }

}
