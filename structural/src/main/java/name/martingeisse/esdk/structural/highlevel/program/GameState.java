package name.martingeisse.esdk.structural.highlevel.program;

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

    // the player's score
    public static int score;

    /* Position of the currently moving shape. These values may be negative and/or exceed 9 since they only
     * indicate the position of the upper left corner of the shape's 4x4 box, not of the shape itself.
     * Also, when entering the game area, the actual shape also crosses the y=0 border.
     */
    public static int shapeX, shapeY;

    // shape index (0-6) of the current shape
    public static int shapeIndex;

    // drawing color of the current shape
    public static int shapeColor;

    void initializeGameState() {
        clearGameArea();
        preview0 = randomPiece();
        preview1 = randomPiece();
        preview2 = randomPiece();
        rows = 0;
        score = 0;
        nextPiece();
    }

    void clearGameArea() {
        for (int i = 0; i < gameArea.length; i++) {
            gameArea[i] = 0;
        }
    }

    int shiftPreview(int shiftIn) {
        int shiftOut = preview0;
        preview0 = preview1;
        preview1 = preview2;
        preview2 = shiftIn;
        return shiftOut;
    }

    int randomPiece() {
        int color = Random.getRandom() % 7 + 1;
        int piece = Random.getRandom() % Shapes.numPieces;
        return piece + (color << 8);
    }

    int shiftPreviewRandom() {
        return shiftPreview(randomPiece());
    }

    int getRowScore(int level, int num) {
        switch (num) {
            case 1:
                return 40 * (level + 1);
            case 2:
                return 100 * (level + 1);
            case 3:
                return 300 * (level + 1);
            case 4:
                return 1200 * (level + 1);
            default:
                return 0;
        }
    }

    boolean addRows(int num) {
        int oldLevel = rows / 10;
        score += getRowScore(oldLevel, num);
        rows += num;
        return (rows / 10) != oldLevel;
    }

    void enterShape(int shapeIndex, int shapeColor) {
        shapeX = 3;
        shapeY = -4;
        GameState.shapeIndex = shapeIndex;
        GameState.shapeColor = shapeColor;
    }

    void nextPiece() {
        int pieceAndColor = shiftPreviewRandom();
        int color = pieceAndColor >> 8;
        int piece = pieceAndColor & 0xff;
        int shape = Shapes.normalShapeByPiece[piece];
        enterShape(shape, color);
    }

    boolean unblockedShapePosition(int x, int y, int shape) {
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

    boolean moveCurrentShapeDown() {
        if (!unblockedShapePosition(shapeX, shapeY + 1, shapeIndex)) {
            return false;
        }
        shapeY++;
        return true;
    }

    boolean moveCurrentShapeLeft() {
        if (!unblockedShapePosition(shapeX - 1, shapeY, shapeIndex)) {
            return false;
        }
        shapeX--;
        return true;
    }

    boolean moveCurrentShapeRight() {
        if (!unblockedShapePosition(shapeX + 1, shapeY, shapeIndex)) {
            return false;
        }
        shapeX++;
        return true;
    }

    boolean rotateCurrentShapeClockwise() {
        int newShape = Shapes.shapeRotatedClockwise[shapeIndex];
        if (!unblockedShapePosition(shapeX, shapeY, newShape)) {
            return false;
        }
        shapeIndex = newShape;
        return true;
    }

    boolean rotateCurrentShapeCounterClockwise() {
        int newShape = Shapes.shapeRotatedCounterClockwise[shapeIndex];
        if (!unblockedShapePosition(shapeX, shapeY, newShape)) {
            return false;
        }
        shapeIndex = newShape;
        return true;
    }

    boolean pasteShape(int x, int y, int shapeIndex, int shapeColor) {
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

    boolean pasteCurrentShape() {
        return pasteShape(shapeX, shapeY, shapeIndex, shapeColor);
    }

    boolean isRowCompleted(int rowIndex) {
        for (int x = 0; x < 10; x++) {
            if (gameArea[10 * rowIndex + x] == 0) {
                return false;
            }
        }
        return true;
    }

    void clearRow(int y) {
        for (int x = 0; x < 10; x++) {
            gameArea[10 * y + x] = 0;
        }
    }

    void copyRow(int source, int dest) {
        for (int x = 0; x < 10; x++) {
            gameArea[10 * dest + x] = gameArea[10 * source + x];
        }
    }

    int findCompletedRows(int firstRowIndex, int maxRowCount, int[] rowIndices) {
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

    private boolean intArrayContains(int count, int[] array, int item) {
        for (int i = 0; i < count; i++) {
            if (array[i] == item) {
                return true;
            }
        }
        return false;
    }

    void removeRows(int rowCount, int[] rowIndices) {
        int stack = 19;
        for (int y = 19; y >= 0; y--) {
            if (intArrayContains(rowCount, rowIndices, y)) {
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
