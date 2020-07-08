package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.Constants;
import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

import java.util.Arrays;

@SuppressWarnings("ExplicitArrayFilling")
public final class Engine {

    private Engine() {
    }

    private static final int delayByLevel[] = {
            30, 27, 24, 21, 18, 15, 12, 8, 5, 2
    };

    private static final int delayLevels = 10;

    private static final int[] flashRowsEffectColors = new int[]{7, 3, 0, 3, 7, 3, 0};

    private static final int flashRowsEffectTotalLength = 35;

    //
    public static int flashRowsEffect;

    public static void delayFrame() {
        Devices.delay();
    }

    public static void delayFrames(int frameCount) {
        for (int i = 0; i < frameCount; i++) {
            delayFrame();
        }
    }

    public static void drawFlashRowsEffect(int[] rows, int frame) {
        Draw.fillGameRows(rows, flashRowsEffectColors[frame / 5]);
    }

    public static void recolorTiles(int color) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                if (Devices.memory[j * 10 + i] != 0) {
                    Devices.memory[j * 10 + i] = (byte) color;
                }
            }
        }
        CpuProgramFragments.INSTANCE.drawGameArea();
    }

    public static void newLevel() {
        int remainingColorLoops = 2;
        while (true) {

            int color = 7;
            while (true) {

                recolorTiles(color);
                delayFrames(2);

                color -= 2;
                if (color < 0) {
                    break;
                }
                color++;
            }

            remainingColorLoops--;
            if (remainingColorLoops < 0) {
                break;
            }
        }
        delayFrames(5);
    }

    private static void clearPreview() {
        Draw.drawPieceInPreview(0, Devices.memory[MemoryMap.PREVIEW_PIECE_0] & 0xff, 0);
        Draw.drawPieceInPreview(1, Devices.memory[MemoryMap.PREVIEW_PIECE_1] & 0xff, 0);
        Draw.drawPieceInPreview(2, Devices.memory[MemoryMap.PREVIEW_PIECE_2] & 0xff, 0);
    }

    private static void drawPreview() {
        Draw.drawPieceInPreview(0, Devices.memory[MemoryMap.PREVIEW_PIECE_0] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_0] & 0xff);
        Draw.drawPieceInPreview(1, Devices.memory[MemoryMap.PREVIEW_PIECE_1] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_1] & 0xff);
        Draw.drawPieceInPreview(2, Devices.memory[MemoryMap.PREVIEW_PIECE_2] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_2] & 0xff);
    }

    public static void engineNewGame() {
        GameState.initializeGameState();
        Devices.memory[MemoryMap.GAME_RUNNING] = 1;
        flashRowsEffect = 0;
        Draw.drawBackground();
        drawPreview();
    }

    public static void gameStep() {
        if (flashRowsEffect > 0) {

            // find completed rows
            int[] completedRows = new int[4];
            int count = GameState.findCompletedRows(GameState.shapeY, 4, completedRows);
            completedRows = Arrays.copyOf(completedRows, count);

            // check for end of flash effect
            if (flashRowsEffect == flashRowsEffectTotalLength) {
                flashRowsEffect = 0;
                GameState.removeRows(completedRows);
                if (GameState.addRows(count)) {
                    newLevel();
                } else {
                    CpuProgramFragments.INSTANCE.drawGameArea();
                }
                clearPreview();
                GameState.nextPiece();
                drawPreview();
            } else {
                drawFlashRowsEffect(completedRows, flashRowsEffect);
                flashRowsEffect++;
            }

        } else {

            // undraw shape and remember position and shape
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff, 0);
            GameState.oldShapeX = GameState.shapeX;
            GameState.oldShapeY = GameState.shapeY;
            GameState.oldShape = Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff;

            // perform movement as if unblocked
            if (Devices.buttonStates[Constants.BUTTON_INDEX_LEFT] && Devices.memory[MemoryMap.MOVEMENT_DELAY_COUNTER] == 0) {
                GameState.shapeX--;
            }
            if (Devices.buttonStates[Constants.BUTTON_INDEX_RIGHT] && Devices.memory[MemoryMap.MOVEMENT_DELAY_COUNTER] == 0) {
                GameState.shapeX++;
            }
            if (Devices.buttonStates[Constants.BUTTON_INDEX_ROTATE_CW]) {
                Devices.buttonStates[Constants.BUTTON_INDEX_ROTATE_CW] = false;
                int newShape = Shapes.shapeRotatedClockwise[Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff];
                Devices.memory[MemoryMap.CURRENT_SHAPE] = (byte)newShape;
            }
            if (Devices.buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW]) {
                Devices.buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW] = false;
                int newShape = Shapes.shapeRotatedCounterClockwise[Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff];
                Devices.memory[MemoryMap.CURRENT_SHAPE] = (byte)newShape;
            }

            // If now unblocked, remember new position and shape. If blocked, restore old position and shape
            if (GameState.unblockedShapePosition()) {
                GameState.oldShapeX = GameState.shapeX;
                GameState.oldShapeY = GameState.shapeY;
                GameState.oldShape = Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff;
            } else {
                GameState.shapeX = GameState.oldShapeX;
                GameState.shapeY = GameState.oldShapeY;
                Devices.memory[MemoryMap.CURRENT_SHAPE] = (byte)GameState.oldShape;
            }

            // perform downward movement
            if (Devices.buttonStates[Constants.BUTTON_INDEX_DOWN] || Devices.memory[MemoryMap.GAME_DELAY_COUNTER] == 0) {
                GameState.shapeY++;
            }

            // if now blocked, restore old position and shape. Also remember that since it means the shape has landed.
            boolean landed = !GameState.unblockedShapePosition();
            if (landed) {
                // TODO only y can change, so restoring x and shape is not necessary
                GameState.shapeX = GameState.oldShapeX;
                GameState.shapeY = GameState.oldShapeY;
                Devices.memory[MemoryMap.CURRENT_SHAPE] = (byte)GameState.oldShape;
            }

            // draw shape at new position
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, Devices.memory[MemoryMap.CURRENT_SHAPE] & 0xff, Devices.memory[MemoryMap.CURRENT_COLOR] & 0xff);

            // handle landing
            if (landed) {
                int[] completedRows = new int[4];
                int count;

                if (GameState.pasteShape()) {
                    Devices.memory[MemoryMap.GAME_RUNNING] = 0;
                    CpuProgramFragments.INSTANCE.drawGameOver();
                    return;
                }

                count = GameState.findCompletedRows(GameState.shapeY, 4, completedRows);
                if (count == 0) {
                    clearPreview();
                    GameState.nextPiece();
                    drawPreview();
                } else {
                    flashRowsEffect = 1;
                }
            }

        }
    }

    public static void mainLoopTick() {
        if (Devices.memory[MemoryMap.GAME_RUNNING] != 0) {
            delayFrame();
            gameStep();

            // game delay depends on the current level
            Devices.memory[MemoryMap.GAME_DELAY_COUNTER]++;
            if (GameState.level > delayLevels || Devices.memory[MemoryMap.GAME_DELAY_COUNTER] >= delayByLevel[GameState.level]) {
                Devices.memory[MemoryMap.GAME_DELAY_COUNTER] = 0;
            }

            // movement delay is fixed to 3 frames
            Devices.memory[MemoryMap.MOVEMENT_DELAY_COUNTER]++;
            if (Devices.memory[MemoryMap.MOVEMENT_DELAY_COUNTER] == 3) {
                Devices.memory[MemoryMap.MOVEMENT_DELAY_COUNTER] = 0;
            }

        } else {
            Draw.drawTitleScreen();
            while (true) {
                boolean anyButtonPressed = false;
                for (int i = 0; i < Devices.buttonStates.length; i++) {
                    anyButtonPressed |= Devices.buttonStates[i];
                }
                if (anyButtonPressed) {
                    CpuProgramFragments.INSTANCE.autoSeedRandom();
                    engineNewGame();
                    break;
                }
                CpuProgramFragments.INSTANCE.randomAutoSeederTick();
            }
        }
    }

}
