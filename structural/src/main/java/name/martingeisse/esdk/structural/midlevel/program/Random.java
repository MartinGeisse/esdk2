package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

public final class Random {

    private static int currentNumber;

    public static void randomAutoSeederTick() {
        CpuProgramFragments.INSTANCE.randomAutoSeederTick();
    }

    public static void autoSeedRandom() {
        currentNumber = (Devices.memory[MemoryMap.RNG_SEEDER_0] & 0xff);
        currentNumber |= (Devices.memory[MemoryMap.RNG_SEEDER_1] & 0xff) << 8;
        currentNumber |= (Devices.memory[MemoryMap.RNG_SEEDER_2] & 0xff) << 16;
        currentNumber |= (Devices.memory[MemoryMap.RNG_SEEDER_3] & 0xff) << 24;
        System.out.println(currentNumber);
    }

    public static int getRandom(int mod) {
        currentNumber = currentNumber * 1664525 + 1013904223;
        int result = currentNumber % mod;
        if (result < 0) {
            result += mod;
        }
        return result;
    }

    private Random() {
    }

}
