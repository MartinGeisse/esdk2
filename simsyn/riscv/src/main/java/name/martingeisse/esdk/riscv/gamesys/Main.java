package name.martingeisse.esdk.riscv.gamesys;

import javax.swing.*;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        GamesysCompilerInvoker.invoke();

        GameSystem gameSystem = new GameSystem();
        gameSystem.loadImage(new File("riscv/resource/gamesys/build/program.bin"), 0);

        DisplayPanel displayPanel = new DisplayPanel(gameSystem.ram);
        JFrame frame = new JFrame("Graphics Display");
        frame.add(displayPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        new Timer(500, event -> displayPanel.repaint()).start();

        gameSystem.run();
    }

}
