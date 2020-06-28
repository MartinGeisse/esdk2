package name.martingeisse.esdk.structural.highlevel;

import name.martingeisse.esdk.structural.highlevel.program.Draw;
import name.martingeisse.esdk.structural.highlevel.program.Engine;

public class Program {

    private static Program INSTANCE = null;

    private final byte[] displayMatrix;
    private final Runnable displayRepaintCallback;
    private final boolean[] buttonStates;

    public Program(byte[] displayMatrix, Runnable displayRepaintCallback, boolean[] buttonStates) {
        this.displayMatrix = displayMatrix;
        this.displayRepaintCallback = displayRepaintCallback;
        this.buttonStates = buttonStates;
    }

    public void run() throws InterruptedException {
        INSTANCE = this;
        try {
            Draw.frameBuffer = displayMatrix;
            Engine.buttonStates = buttonStates;
            while (true) {
                Engine.mainLoopTick();
                delay();
            }
        } finally {
            INSTANCE = null;
        }
    }

    public static void delay() {
        INSTANCE.displayRepaintCallback.run();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

}
