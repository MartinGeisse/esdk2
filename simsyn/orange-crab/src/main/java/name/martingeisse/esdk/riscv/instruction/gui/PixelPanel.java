package name.martingeisse.esdk.riscv.instruction.gui;

import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.riscv.common.terminal.KeyCodeTranslator;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 *
 */
public class PixelPanel extends JPanel {

    private final int[] ram;
    private final BufferedImage image;
    private final LinkedList<Byte> inputBuffer = new LinkedList<>();

    public PixelPanel(int[] ram) {
        super(false);
        setFocusable(true);
        this.ram = ram;
        this.image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        setSize(640, 480);
        setPreferredSize(new Dimension(640, 480));
        addKeyListener(new KeyAdapter() {

            private final KeyCodeTranslator translator = new KeyCodeTranslator();

            @Override
            public void keyPressed(KeyEvent e) {
                handle(translator.translate(e.getKeyCode(), false));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handle(translator.translate(e.getKeyCode(), true));
            }

            private void handle(byte[] bytes) {
                if (bytes != null) {
                    for (byte b : bytes) {
                        inputBuffer.offer(b);
                    }
                }
            }

        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (int y = 0; y < 480; y++) {
            int baseWordAddress = y * 256;
            for (int x = 0; x < 640; x += 4) {
                int wordAddress = baseWordAddress + (x >> 2);
                int wordValue = ram[wordAddress];
                image.setRGB(x, y, expandRgb(wordValue));
                image.setRGB(x + 1, y, expandRgb(wordValue >> 8));
                image.setRGB(x + 2, y, expandRgb(wordValue >> 16));
                image.setRGB(x + 3, y, expandRgb(wordValue >> 24));
            }
        }
        g.drawImage(image, 0, 0, null);
    }

    private static int expandRgb(int value) {
        return (((value & 4) != 0) ? 0xffff0000 : 0xff000000) |
                (((value & 2) != 0) ? 0xff00ff00 : 0xff000000) |
                (((value & 1) != 0) ? 0xff0000ff : 0xff000000);
    }

    public byte readInput() {
        return inputBuffer.isEmpty() ? 0 : inputBuffer.poll();
    }

}
