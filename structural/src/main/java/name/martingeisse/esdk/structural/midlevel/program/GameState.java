package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

@SuppressWarnings("ExplicitArrayFilling")
public final class GameState {

    private GameState() {
    }

    // completed rows for this level
    public static int rows;
    public static int level;

    /* Position of the currently moving shape. These values may be negative and/or exceed 9 since they only
     * indicate the position of the upper left corner of the shape's 4x4 box, not of the shape itself.
     * Also, when entering the game area, the actual shape also crosses the y=0 border.
     */
    public static int shapeX, shapeY;

    public static int oldShape;
    public static int oldShapeX, oldShapeY;

    public static void initializeGameState() {
        clearGameArea();
        Devices.memory[MemoryMap.PREVIEW_PIECE_0] = (byte)Random.getRandom(Shapes.numPieces);
        Devices.memory[MemoryMap.PREVIEW_COLOR_0] = (byte)(Random.getRandom(7) + 1);
        Devices.memory[MemoryMap.PREVIEW_PIECE_1] = (byte)Random.getRandom(Shapes.numPieces);
        Devices.memory[MemoryMap.PREVIEW_COLOR_1] = (byte)(Random.getRandom(7) + 1);
        Devices.memory[MemoryMap.PREVIEW_PIECE_2] = (byte)Random.getRandom(Shapes.numPieces);
        Devices.memory[MemoryMap.PREVIEW_COLOR_2] = (byte)(Random.getRandom(7) + 1);
        rows = 0;
        level = 0;
        nextPiece();
    }

    public static void clearGameArea() {
        CpuProgramFragments.INSTANCE.clearGameArea();
    }

    public static boolean addRows(int num) {
        rows += num;
        if (rows < 10) {
            return false;
        }
        rows -= 10;
        level++;
        return true;
    }

    public static void nextPiece() {

        // shift color
        Devices.memory[MemoryMap.CURRENT_COLOR] = Devices.memory[MemoryMap.PREVIEW_COLOR_0];
        Devices.memory[MemoryMap.PREVIEW_COLOR_0] = Devices.memory[MemoryMap.PREVIEW_COLOR_1];
        Devices.memory[MemoryMap.PREVIEW_COLOR_1] = Devices.memory[MemoryMap.PREVIEW_COLOR_2];
        Devices.memory[MemoryMap.PREVIEW_COLOR_2] = (byte)(Random.getRandom(7) + 1);

        // shift piece
        Devices.memory[MemoryMap.TEMP_0] = Devices.memory[MemoryMap.PREVIEW_PIECE_0];
        Devices.memory[MemoryMap.PREVIEW_PIECE_0] = Devices.memory[MemoryMap.PREVIEW_PIECE_1];
        Devices.memory[MemoryMap.PREVIEW_PIECE_1] = Devices.memory[MemoryMap.PREVIEW_PIECE_2];
        Devices.memory[MemoryMap.PREVIEW_PIECE_2] = (byte) Random.getRandom(Shapes.numPieces);

        // place the new piece at the top of the game area
        int piece = Devices.memory[MemoryMap.TEMP_0] & 0xff;
        Devices.memory[MemoryMap.CURRENT_SHAPE] = (byte)Shapes.normalShapeByPiece[piece];
        shapeX = 3;
        shapeY = -4;

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
                if (Devices.memory[y2 * 10 + x2] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean moveCurrentShapeDown() {
        if (!unblockedShapePosition(shapeX, shapeY + 1, Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff)) {
            return false;
        }
        shapeY++;
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
                Devices.memory[y2 * 10 + x2] = (byte) shapeColor;
            }
        }
        return crossedBorder;
    }

    public static boolean pasteCurrentShape() {
        return pasteShape(shapeX, shapeY, Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff, Devices.memory[MemoryMap.CURRENT_COLOR] & 0xff);
    }

    public static boolean isRowCompleted(int rowIndex) {
        for (int x = 0; x < 10; x++) {
            if (Devices.memory[10 * rowIndex + x] == 0) {
                return false;
            }
        }
        return true;
    }

    public static void clearRow(int y) {
        for (int x = 0; x < 10; x++) {
            Devices.memory[10 * y + x] = 0;
        }
    }

    public static void copyRow(int source, int dest) {
        for (int x = 0; x < 10; x++) {
            Devices.memory[10 * dest + x] = Devices.memory[10 * source + x];
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
