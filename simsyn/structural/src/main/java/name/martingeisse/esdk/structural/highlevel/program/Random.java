package name.martingeisse.esdk.structural.highlevel.program;

public final class Random {

    private static int autoSeeder;
    private static int currentNumber;

    public static void randomAutoSeederTick() {
        autoSeeder++;
    }

    public static void autoSeedRandom() {
        currentNumber = autoSeeder;
    }

    public static void seedRandom(int seed) {
        currentNumber = seed;
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
