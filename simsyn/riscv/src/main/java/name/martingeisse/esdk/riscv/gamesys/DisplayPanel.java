package name.martingeisse.esdk.riscv.gamesys;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 */
public class DisplayPanel extends JPanel {

    private final int[] ram;
    private final BufferedImage image;
    private final int[] imagePixels;
    private int displayPlane;

    public DisplayPanel(int[] ram) {
        super(false);
        this.ram = ram;
        this.image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        this.imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        setSize(640, 480);
        setPreferredSize(new Dimension(640, 480));
    }

    public void setDisplayPlane(int displayPlane) {
        this.displayPlane = displayPlane;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int planeBaseWordAddress = (displayPlane == 0 ? Constants.FRAMEBUFFER_0_WORD_ADDRESS : Constants.FRAMEBUFFER_1_WORD_ADDRESS);
        for (int y = 0; y < 480; y++) {
            int baseWordAddress = planeBaseWordAddress + y * 256;
            int basePixelIndex = y * 640;
            for (int x = 0; x < 640; x += 4) {
                int wordAddress = baseWordAddress + (x >> 2);
                int dataWord = ram[wordAddress];
                int pixelIndex = basePixelIndex + x;
                imagePixels[pixelIndex] = expandRgb(dataWord);
                imagePixels[pixelIndex + 1] = expandRgb(dataWord >> 8);
                imagePixels[pixelIndex + 2] = expandRgb(dataWord >> 16);
                imagePixels[pixelIndex + 3] = expandRgb(dataWord >> 24);
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
