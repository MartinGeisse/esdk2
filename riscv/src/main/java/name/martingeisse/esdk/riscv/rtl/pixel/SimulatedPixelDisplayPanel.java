package name.martingeisse.esdk.riscv.rtl.pixel;

import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class SimulatedPixelDisplayPanel extends JPanel {

    private final SimulatedRam.Implementation simulatedRam;
    private final BufferedImage image;

    public SimulatedPixelDisplayPanel(SimulatedRam.Implementation simulatedRam) {
        super(false);
        this.simulatedRam = simulatedRam;
        this.image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        setSize(640, 480);
        setPreferredSize(new Dimension(640, 480));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Matrix matrix0 = simulatedRam._memory0.getMatrix();
        Matrix matrix1 = simulatedRam._memory1.getMatrix();
        Matrix matrix2 = simulatedRam._memory2.getMatrix();
        Matrix matrix3 = simulatedRam._memory3.getMatrix();
        for (int y = 0; y < 480; y++) {
            int baseWordAddress = y * 160;
            for (int x = 0; x < 640; x += 4) {
                int wordAddress = baseWordAddress + (x >> 2);
                image.setRGB(x, y, expandRgb(matrix0.getRow(wordAddress).getAsUnsignedInt()));
                image.setRGB(x + 1, y, expandRgb(matrix1.getRow(wordAddress).getAsUnsignedInt()));
                image.setRGB(x + 2, y, expandRgb(matrix2.getRow(wordAddress).getAsUnsignedInt()));
                image.setRGB(x + 3, y, expandRgb(matrix3.getRow(wordAddress).getAsUnsignedInt()));
            }
        }
        g.drawImage(image, 0, 0, null);
    }

    private static int expandRgb(int value) {
        return (((value & 4) != 0) ? 0x00ff0000 : 0x000000) |
                (((value & 2) != 0) ? 0x0000ff00 : 0x000000) |
                (((value & 1) != 0) ? 0x000000ff : 0x000000);
    }

}
