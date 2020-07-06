package name.martingeisse.esdk.structural.midlevel;

import name.martingeisse.esdk.structural.midlevel.program.Random;

public class TestMain {

    public static void main(String[] args) {

        {
            int currentNumber = 0;
            for (int i = 0; i < 10; i++) {
                currentNumber = currentNumber * 1664525 + 1013904223;

                long rn = ((long)currentNumber) & 0xffffffffL;

//                System.out.println("random number: " + rn);
//                System.out.println("random number: " + Long.toBinaryString(rn));
                System.out.println("mod7: " + ((rn % 7) + 7) % 7);
            }
        }

        System.out.println();

        for (int i = 0; i < 10; i++) {

            // CpuProgramFragments.INSTANCE.nextRandom();
            int currentNumber = (Devices.memory[MemoryMap.RNG_CURRENT_0] & 0xff);
            currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_1] & 0xff) << 8;
            currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_2] & 0xff) << 16;
            currentNumber |= (Devices.memory[MemoryMap.RNG_CURRENT_3] & 0xff) << 24;
//            System.out.println("random number: " + currentNumber);
//            System.out.println("random number: " + Integer.toBinaryString(currentNumber));

            System.out.println("mod7: " + Random.nextRandomMod7());
        }

    }

}
