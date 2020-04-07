package name.martingeisse.esdk.riscv.rtl.pixel;

import org.lwjgl.opengl.GL11;

/**
 * Implements the OpenGL functionality of the simulation device, but does not implement an RTL interface itself.
 * Rather, this object provides methods to simulate at transaction level.
 */
public final class SimulatedOpenglHelper {

    private LwjglWindow lwjglWindow;

    public SimulatedOpenglHelper() {
        lwjglWindow = new LwjglWindow();
        lwjglWindow.open(640, 480, "OpenGL Device");
    }

    public void destroy() {
        lwjglWindow.shutdown();
        lwjglWindow = null;
    }

    public void write(int wordAddress, int byteMask, int data) {
        wordAddress = wordAddress & 0xffff;
        switch (wordAddress) {

            // flip frame buffers
            case 0:
                flipScreen();
                break;

                // clear screen
            case 1:
                GL11.glClearColor(decodeRedF(data), decodeGreenF(data), decodeBlueF(data), 1.0f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                break;

        }
    }

    public int read(int wordAddress) {
        // wordAddress = wordAddress & 0xffff;
        return 0;
    }

    public void flipScreen() {
        lwjglWindow.flipScreen();
    }

    private static float decodeRedF(int color) {
        return (color & 4) == 0 ? 0 : 1;
    }

    private static float decodeGreenF(int color) {
        return (color & 2) == 0 ? 0 : 1;
    }

    private static float decodeBlueF(int color) {
        return (color & 1) == 0 ? 0 : 1;
    }

}
