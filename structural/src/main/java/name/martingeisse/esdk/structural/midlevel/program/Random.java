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

    public static int getRandom(int mod) {

        int currentNumber = (Devices.memory[MemoryMap.RNG_CURRENT_0] & 0xff);
        currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_1] & 0xff) << 8;
        currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_2] & 0xff) << 16;
        currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_3] & 0xff) << 24;

        currentNumber = currentNumber * 1664525;

        Devices.memory[MemoryMap.RNG_CURRENT_0] = (byte)currentNumber;
        Devices.memory[MemoryMap.RNG_CURRENT_1] = (byte)(currentNumber >> 8);
        Devices.memory[MemoryMap.RNG_CURRENT_2] = (byte)(currentNumber >> 16);
        Devices.memory[MemoryMap.RNG_CURRENT_3] = (byte)(currentNumber >> 24);

        // add 1013904223
        CpuProgramFragments.INSTANCE.getRandom();


        // compute remainder
        int result = currentNumber % mod;
        if (result < 0) {
            result += mod;
        }
        return result;

    }

    private Random() {
    }

}
