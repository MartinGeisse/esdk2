package name.martingeisse.esdk.structural.highlevel;

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

        Program program = new Program(ledMatrixPanel.getMatrix(), ledMatrixPanel::repaint, ledMatrixPanel.getButtonStates());
        program.run();

    }

}
