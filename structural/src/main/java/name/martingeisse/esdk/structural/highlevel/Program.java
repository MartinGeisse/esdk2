package name.martingeisse.esdk.structural.highlevel;

import name.martingeisse.esdk.structural.highlevel.program.Draw;
import name.martingeisse.esdk.structural.highlevel.program.Engine;

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
        Engine.buttonStates = buttonStates;
        while (true) {
            Engine.mainLoopTick();
            displayRepaintCallback.run();
            delay();
        }
    }

    public static void delay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

}
