package name.martingeisse.esdk.structural.highlevel.program;

// TODO reduce to height 30 so coarse pixels can be a POW of fine pixels
public final class Draw {

    private Draw() {
    }

    public static byte[] frameBuffer;

    // offet of the game area in screen coordinates
    public static final int GAME_AREA_X_ON_SCREEN = 3;
    public static final int GAME_AREA_Y_ON_SCREEN = 9;

    // offet of the (leftmost) preview box screen coordinates
    public static final int PREVIEW_X_ON_SCREEN = 1;
    public static final int PREVIEW_Y_ON_SCREEN = 1;

    // x offset of one preview box to the next
    public static final int PREVIEW_X_DELTA = 5;

    private static final byte[] titleScreenTemplate = new byte[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 0,
            0, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 0, 0, 4, 4, 4, 4, 0, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 0, 0, 4, 4, 4, 4, 4, 4, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 0, 0, 4, 4, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 0, 4, 4, 0, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 0, 4, 4, 4, 0,
            0, 0, 0, 3, 3, 0, 0, 0, 0, 4, 4, 0, 0, 4, 4, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 5, 5, 5, 5, 0, 0, 0, 0, 6, 6, 6, 6, 0, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 6, 6, 6, 6, 6, 0, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 6, 6, 0, 0, 0, 0, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 6, 6, 6, 6, 6, 0, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 0,
            0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 0,
            0, 0, 5, 5, 5, 5, 0, 0, 0, 0, 6, 6, 6, 6, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    };

    private static final byte[] backgroundTemplate = new byte[]{
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
    };

    public static void clearScreen() {
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 30; y++) {
                frameBuffer[y * 40 + x] = 0;
            }
        }
    }

    public static void drawScreen(byte[] screenTemplate) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 30; y++) {
                frameBuffer[y * 40 + x] = screenTemplate[y * 16 + x];
            }
        }
    }

    public static void drawBackground() {
        drawScreen(backgroundTemplate);
    }

    public static void drawTitleScreen() {
        drawScreen(titleScreenTemplate);
    }

    public static void drawClippedShapeOnScreen(int x, int y, int shapeIndex, int c, int minx, int miny, int maxx, int maxy) {
        boolean[] occupationMatrix = Shapes.shapeOccupationMatrices[shapeIndex];
        for (int dx = 0; dx < 4; dx++) {
            int x2 = x + dx;
            if (x2 < minx || x2 > maxx) {
                continue;
            }
            for (int dy = 0; dy < 4; dy++) {
                int y2 = y + dy;
                if (y2 < miny || y2 > maxy) {
                    continue;
                }
                if (occupationMatrix[dy * 4 + dx]) {
                    frameBuffer[y2 * 40 + x2] = (byte) c;
                }
            }
        }
    }

    public static void drawShapeOnScreen(int x, int y, int shapeIndex, int c) {
        drawClippedShapeOnScreen(x, y, shapeIndex, c, 0, 0, 39, 29);
    }


    public static void drawShapeOnGameArea(int x, int y, int shapeIndex, int c) {
        drawClippedShapeOnScreen(x + GAME_AREA_X_ON_SCREEN, y + GAME_AREA_Y_ON_SCREEN,
                shapeIndex, c,
                GAME_AREA_X_ON_SCREEN, GAME_AREA_Y_ON_SCREEN,
                GAME_AREA_X_ON_SCREEN + 9, GAME_AREA_Y_ON_SCREEN + 19);
    }

    public static void drawShapeInPreview(int previewIndex, int shapeIndex, int c) {
        drawShapeOnScreen(PREVIEW_X_ON_SCREEN + previewIndex * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, shapeIndex, c);
    }

    public static void drawPieceInPreview(int previewIndex, int pieceIndex, int c) {
        drawShapeInPreview(previewIndex, Shapes.normalShapeByPiece[pieceIndex], c);
    }

    public static void fillGameRow(int y, int c) {
        int i;
        for (i = 0; i < 10; i++) {
            frameBuffer[(GAME_AREA_Y_ON_SCREEN + y) * 40 + GAME_AREA_X_ON_SCREEN + i] = (byte) c;
        }
    }

    public static void fillGameRows(int[] rows, int c) {
        for (int row : rows) {
            fillGameRow(row, c);
        }
    }

    public static void drawGameArea(byte[] data) {
        int dx, dy;
        for (dx = 0; dx < 10; dx++) {
            for (dy = 0; dy < 20; dy++) {
                frameBuffer[(GAME_AREA_Y_ON_SCREEN + dy) * 40 + GAME_AREA_X_ON_SCREEN + dx] = data[dy * 10 + dx];
            }
        }
    }

}
