package name.martingeisse.esdk.structural.highlevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 */
public class LedMatrixPanel extends JPanel {

    private static final Color[] COLOR_TABLE = {
            Color.BLACK,
            Color.BLUE,
            Color.GREEN,
            Color.CYAN,
            Color.RED,
            Color.MAGENTA,
            Color.YELLOW,
            Color.WHITE
    };

    private static final Color CASE_COLOR = new Color(32, 32, 32);

    private static final int DOT_SIZE = 20;
    private static final int DOT_PADDING = 2;

    private final byte[] matrix = new byte[40 * 30];
    private final boolean[] buttonStates = new boolean[Constants.BUTTON_INDEX_COUNT];

    public LedMatrixPanel() {
        super(false);
        setSize(DOT_SIZE * 40, DOT_SIZE * 30);
        setPreferredSize(new Dimension(DOT_SIZE * 40, DOT_SIZE * 30));
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                handle(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handle(e.getKeyCode(), false);
            }

            private void handle(int code, boolean state) {
                switch (code) {

                    case KeyEvent.VK_LEFT:
                        buttonStates[Constants.BUTTON_INDEX_LEFT] = state;
                        break;

                    case KeyEvent.VK_RIGHT:
                        buttonStates[Constants.BUTTON_INDEX_RIGHT] = state;
                        break;

                    case KeyEvent.VK_DOWN:
                        buttonStates[Constants.BUTTON_INDEX_DOWN] = state;
                        break;

                    case KeyEvent.VK_A:
                        buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW] = state;
                        break;

                    case KeyEvent.VK_S:
                        buttonStates[Constants.BUTTON_INDEX_ROTATE_CW] = state;
                        break;

                }
            }

        });
    }

    public byte[] getMatrix() {
        return matrix;
    }

    public boolean[] getButtonStates() {
        return buttonStates;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(CASE_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 40; x++) {
                byte colorCode = matrix[y * 40 + x];
                g.setColor(COLOR_TABLE[colorCode]);
                g.fillArc(x * DOT_SIZE + DOT_PADDING, y * DOT_SIZE + DOT_PADDING,
                        DOT_SIZE - 2 * DOT_PADDING, DOT_SIZE - 2 * DOT_PADDING, 0, 360);
            }
        }
    }

}
