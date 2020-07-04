package name.martingeisse.esdk.structural.midlevel;

import name.martingeisse.esdk.structural.midlevel.program.Draw;
import name.martingeisse.esdk.structural.midlevel.program.Engine;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {

        LedMatrixPanel ledMatrixPanel = new LedMatrixPanel();

        JFrame frame = new JFrame("Structural");
        frame.add(ledMatrixPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        ledMatrixPanel.grabFocus();

        Devices.buttonStates = ledMatrixPanel.getButtonStates();
        Devices.frameBuffer = ledMatrixPanel.getMatrix();
        Devices.displayRepaintCallback = ledMatrixPanel::repaint;

        while (true) {
            Engine.mainLoopTick();
            ledMatrixPanel.repaint();
        }

    }

}
