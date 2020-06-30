package name.martingeisse.esdk.structural.midlevel.program;

@SuppressWarnings("ExplicitArrayFilling")
public final class GameState {

    private GameState() {
    }

    // state of the game area (block color values, with 0 being 'free')
    public static byte[] gameArea = new byte[10 * 20];

    /* Preview pieces. The piece index is stored in the lowest 8 bits (7 to 0)
     * and the color in the next higher 8 bits (15 to 8).
     */
    public static int preview0, preview1, preview2;

    // completed rows (this also indicates the game level by rows/10).
    public static int rows;

    /* Position of the currently moving shape. These values may be negative and/or exceed 9 since they only
     * indicate the position of the upper left corner of the shape's 4x4 box, not of the shape itself.
     * Also, when entering the game area, the actual shape also crosses the y=0 border.
     */
    public static int shapeX, shapeY;

    // shape index (0-6) of the current shape
    public static int shapeIndex;

    // drawing color of the current shape
    public static int shapeColor;

    public static void initializeGameState() {
        clearGameArea();
        preview0 = randomPiece();
        preview1 = randomPiece();
        preview2 = randomPiece();
        rows = 0;
        nextPiece();
    }

    public static void clearGameArea() {
        for (int i = 0; i < gameArea.length; i++) {
            gameArea[i] = 0;
        }
    }

    public static int shiftPreview(int shiftIn) {
        int shiftOut = preview0;
        preview0 = preview1;
        preview1 = preview2;
        preview2 = shiftIn;
        return shiftOut;
    }

    public static int randomPiece() {
        int color = Random.getRandom(7) + 1;
        int piece = Random.getRandom(Shapes.numPieces);
        return piece + (color << 8);
    }

    public static int shiftPreviewRandom() {
        return shiftPreview(randomPiece());
    }

    public static boolean addRows(int num) {
        int oldLevel = rows / 10;
        rows += num;
        return (rows / 10) != oldLevel;
    }

    public static void enterShape(int shapeIndex, int shapeColor) {
        shapeX = 3;
        shapeY = -4;
        GameState.shapeIndex = shapeIndex;
        GameState.shapeColor = shapeColor;
    }

    public static void nextPiece() {
        int pieceAndColor = shiftPreviewRandom();
        int color = pieceAndColor >> 8;
        int piece = pieceAndColor & 0xff;
        int shape = Shapes.normalShapeByPiece[piece];
        enterShape(shape, color);
    }

    public static boolean unblockedShapePosition(int x, int y, int shape) {
        boolean[] matrix = Shapes.shapeOccupationMatrices[shape];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!matrix[j * 4 + i]) {
                    continue;
                }
                int x2 = x + i;
                int y2 = y + j;
                if (x2 < 0 || x2 > 9 || y2 > 19) {
                    return false;
                }
                if (y2 < 0) {
                    continue;
                }
                if (gameArea[y2 * 10 + x2] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean moveCurrentShapeDown() {
        if (!unblockedShapePosition(shapeX, shapeY + 1, shapeIndex)) {
            return false;
        }
        shapeY++;
        return true;
    }

    public static boolean moveCurrentShapeLeft() {
        if (!unblockedShapePosition(shapeX - 1, shapeY, shapeIndex)) {
            return false;
        }
        shapeX--;
        return true;
    }

    public static boolean moveCurrentShapeRight() {
        if (!unblockedShapePosition(shapeX + 1, shapeY, shapeIndex)) {
            return false;
        }
        shapeX++;
        return true;
    }

    public static boolean rotateCurrentShapeClockwise() {
        int newShape = Shapes.shapeRotatedClockwise[shapeIndex];
        if (!unblockedShapePosition(shapeX, shapeY, newShape)) {
            return false;
        }
        shapeIndex = newShape;
        return true;
    }

    public static boolean rotateCurrentShapeCounterClockwise() {
        int newShape = Shapes.shapeRotatedCounterClockwise[shapeIndex];
        if (!unblockedShapePosition(shapeX, shapeY, newShape)) {
            return false;
        }
        shapeIndex = newShape;
        return true;
    }

    public static boolean pasteShape(int x, int y, int shapeIndex, int shapeColor) {
        boolean[] matrix = Shapes.shapeOccupationMatrices[shapeIndex];
        boolean crossedBorder = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!matrix[j * 4 + i]) {
                    continue;
                }
                int x2 = x + i;
                int y2 = y + j;
                if (x2 < 0 || x2 > 9 || y2 < 0 || y2 > 19) {
                    crossedBorder = true;
                    continue;
                }
                gameArea[y2 * 10 + x2] = (byte) shapeColor;
            }
        }
        return crossedBorder;
    }

    public static boolean pasteCurrentShape() {
        return pasteShape(shapeX, shapeY, shapeIndex, shapeColor);
    }

    public static boolean isRowCompleted(int rowIndex) {
        for (int x = 0; x < 10; x++) {
            if (gameArea[10 * rowIndex + x] == 0) {
                return false;
            }
        }
        return true;
    }

    public static void clearRow(int y) {
        for (int x = 0; x < 10; x++) {
            gameArea[10 * y + x] = 0;
        }
    }

    public static void copyRow(int source, int dest) {
        for (int x = 0; x < 10; x++) {
            gameArea[10 * dest + x] = gameArea[10 * source + x];
        }
    }

    public static int findCompletedRows(int firstRowIndex, int maxRowCount, int[] rowIndices) {
        int count = 0;
        for (int i = 0; i < maxRowCount; i++) {
            int row = firstRowIndex + i;
            if (row < 0 || row > 19) {
                continue;
            }
            if (isRowCompleted(row)) {
                rowIndices[count] = row;
                count++;
            }
        }
        return count;
    }

    private static boolean intArrayContains(int[] array, int item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == item) {
                return true;
            }
        }
        return false;
    }

    public static void removeRows(int[] rowIndices) {
        int stack = 19;
        for (int y = 19; y >= 0; y--) {
            if (intArrayContains(rowIndices, y)) {
                continue;
            }
            copyRow(y, stack);
            stack--;
        }
        while (stack >= 0) {
            clearRow(stack);
            stack--;
        }
    }

}
