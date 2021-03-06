package name.martingeisse.esdk.riscv.rtl.pixel;

import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 */
public class SimulatedPixelDisplayPanel extends JPanel {

    private final SimulatedRam.Implementation simulatedRam;
    private final BufferedImage image;
    private final int[] imagePixels;
    private int displayPlane;

    public SimulatedPixelDisplayPanel(SimulatedRam.Implementation simulatedRam) {
        super(false);
        this.simulatedRam = simulatedRam;
        this.image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        this.imagePixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        setSize(640, 480);
        setPreferredSize(new Dimension(640, 480));
    }

    public void setDisplayPlane(int displayPlane) {
        this.displayPlane = displayPlane;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Matrix matrix0 = simulatedRam._memory0.getMatrix();
        Matrix matrix1 = simulatedRam._memory1.getMatrix();
        Matrix matrix2 = simulatedRam._memory2.getMatrix();
        Matrix matrix3 = simulatedRam._memory3.getMatrix();
        int planeBaseAddress = displayPlane * 1024 * 256;
        for (int y = 0; y < 480; y++) {
            int baseWordAddress = planeBaseAddress + y * 256;
            int basePixelIndex = y * 640;
            for (int x = 0; x < 640; x += 4) {
                int wordAddress = baseWordAddress + (x >> 2);
                int pixelIndex = basePixelIndex + x;
                imagePixels[pixelIndex] = expandRgb(matrix0.getRow(wordAddress).getAsUnsignedInt());
                imagePixels[pixelIndex + 1] = expandRgb(matrix1.getRow(wordAddress).getAsUnsignedInt());
                imagePixels[pixelIndex + 2] = expandRgb(matrix2.getRow(wordAddress).getAsUnsignedInt());
                imagePixels[pixelIndex + 3] = expandRgb(matrix3.getRow(wordAddress).getAsUnsignedInt());
            }
        }
        g.drawImage(image, 0, 0, null);
    }

    private static int expandRgb(int value) {
        return (((value & 4) != 0) ? 0xffff0000 : 0xff000000) |
                (((value & 2) != 0) ? 0xff00ff00 : 0xff000000) |
                (((value & 1) != 0) ? 0xff0000ff : 0xff000000);
    }

}
