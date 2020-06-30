package name.martingeisse.esdk.structural.midlevel;

/**
 * Does not include the memory yet -- that is still expressed as Java variables.
 */
public final class Devices {

    private Devices() {
    }

    public static boolean[] buttonStates;
    public static byte[] frameBuffer;
    public static Runnable displayRepaintCallback;
    public static byte[] memory = new byte[256];

    public static void delay() {
        displayRepaintCallback.run();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public static int getButtonStates() {
        int result = 0;
        for (int i = 0; i < Constants.BUTTON_INDEX_COUNT; i++) {
            if (buttonStates[i]) {
                result |= (1 << i);
            }
        }
        return result;
    }

}
