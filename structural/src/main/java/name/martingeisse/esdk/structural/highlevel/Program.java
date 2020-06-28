package name.martingeisse.esdk.structural.highlevel;

import name.martingeisse.esdk.structural.highlevel.program.Draw;

public class Program {

    private final byte[] displayMatrix;
    private final Runnable displayRepaintCallback;
    private final boolean[] buttonStates;

    public Program(byte[] displayMatrix, Runnable displayRepaintCallback, boolean[] buttonStates) {
        this.displayMatrix = displayMatrix;
        this.displayRepaintCallback = displayRepaintCallback;
        this.buttonStates = buttonStates;
    }

    public void run() throws InterruptedException {
        Draw.frameBuffer = displayMatrix;
        int i = 0;
        while (true) {
            Thread.sleep(50);

            // displayMatrix[i] = 0;
            Draw.drawTitleScreen();

            if (buttonStates[Constants.BUTTON_INDEX_LEFT]) {
                i -= 1;
            }
            if (buttonStates[Constants.BUTTON_INDEX_RIGHT]) {
                i += 1;
            }
            if (buttonStates[Constants.BUTTON_INDEX_DOWN]) {
                i += 16;
            }
            if (buttonStates[Constants.BUTTON_INDEX_ROTATE_CW]) {
                i -= 17;
            }
            if (buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW]) {
                i -= 15;
            }

            i = i & 511;
            displayMatrix[i] = 7;
            displayRepaintCallback.run();
        }
    }

}
