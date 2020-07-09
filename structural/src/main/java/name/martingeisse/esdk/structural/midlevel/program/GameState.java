package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

@SuppressWarnings("ExplicitArrayFilling")
public final class GameState {

    private GameState() {
    }

    public static boolean addRows(int num) {
        Devices.memory[MemoryMap.ROW_COUNTER] += num;
        if (Devices.memory[MemoryMap.ROW_COUNTER] < 10) {
            return false;
        }
        Devices.memory[MemoryMap.ROW_COUNTER] -= 10;
        Devices.memory[MemoryMap.LEVEL]++;
        return true;
    }

    public static boolean unblockedShapePosition() {
        boolean[] matrix = Shapes.shapeOccupationMatrices[Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!matrix[j * 4 + i]) {
                    continue;
                }
                int x2 = Devices.memory[MemoryMap.CURRENT_X] + i;
                int y2 = Devices.memory[MemoryMap.CURRENT_Y] + j;
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

    public static boolean pasteShape() {
        int shapeIndex = Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff;
        boolean[] matrix = Shapes.shapeOccupationMatrices[shapeIndex];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!matrix[j * 4 + i]) {
                    continue;
                }
                int x2 = Devices.memory[MemoryMap.CURRENT_X] + i;
                int y2 = Devices.memory[MemoryMap.CURRENT_Y] + j;
                if (x2 < 0 || x2 > 9 || y2 < 0 || y2 > 19) {
                    return false;
                }
                Devices.memory[y2 * 10 + x2] = Devices.memory[MemoryMap.CURRENT_COLOR];
            }
        }
        return true;
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
