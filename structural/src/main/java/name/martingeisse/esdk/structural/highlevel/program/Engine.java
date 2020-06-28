package name.martingeisse.esdk.structural.highlevel.program;

import name.martingeisse.esdk.structural.highlevel.Constants;
import name.martingeisse.esdk.structural.highlevel.Program;

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

    // injected
    public static boolean[] buttonStates;

    // this flag signals outwards to stop immediately
    public static boolean gameOver;

    // fast-drop accumulation (for additional score)
    public static int fastDrop;

    //
    public static int flashRowsEffect;

    //
    private static boolean gameRunning = false;

    //
    private static long mainStepCounter = 0;

    public static void delayFrame() {
        Program.delay();
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
                if (GameState.gameArea[j * 10 + i] != 0) {
                    GameState.gameArea[j * 10 + i] = (byte) color;
                }
            }
        }
        Draw.drawGameArea(GameState.gameArea);
    }

    public static void newLevel() {
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 8; j++) {
                recolorTiles(j);
                delayFrames(2);
            }
        }
        recolorTiles(3);
        delayFrames(5);
    }

    public static void gameOverFill() {
        for (int i = 19; i >= 0; i--) {
            Draw.fillGameRow(i, 7);
            delayFrames(5);
        }
    }

    private static void clearPreview() {
        Draw.drawPieceInPreview(0, GameState.preview0 & 0xff, 0);
        Draw.drawPieceInPreview(1, GameState.preview1 & 0xff, 0);
        Draw.drawPieceInPreview(2, GameState.preview2 & 0xff, 0);
    }

    private static void drawPreview() {
        Draw.drawPieceInPreview(0, GameState.preview0 & 0xff, GameState.preview0 >> 8);
        Draw.drawPieceInPreview(1, GameState.preview1 & 0xff, GameState.preview1 >> 8);
        Draw.drawPieceInPreview(2, GameState.preview2 & 0xff, GameState.preview2 >> 8);
    }

    public static void engineNewGame() {
        GameState.initializeGameState();
        gameOver = false;
        fastDrop = 0;
        flashRowsEffect = 0;
        Draw.drawBackground();
        drawPreview();
    }

    public static void engineLeft() {
        if (GameState.moveCurrentShapeLeft()) {
            Draw.drawShapeOnGameArea(GameState.shapeX + 1, GameState.shapeY, GameState.shapeIndex, 0);
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, GameState.shapeIndex, GameState.shapeColor);
        }
    }

    public static void engineRight() {
        if (GameState.moveCurrentShapeRight()) {
            Draw.drawShapeOnGameArea(GameState.shapeX - 1, GameState.shapeY, GameState.shapeIndex, 0);
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, GameState.shapeIndex, GameState.shapeColor);
        }
    }

    public static boolean engineDown() {
        if (GameState.moveCurrentShapeDown()) {
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY - 1, GameState.shapeIndex, 0);
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, GameState.shapeIndex, GameState.shapeColor);
            return false;
        } else {
            int[] completedRows = new int[4];
            int count;

            if (GameState.pasteCurrentShape()) {
                gameOver = true;
                gameOverFill();
                return true;
            }

            GameState.score += fastDrop;
            fastDrop = 0;

            count = GameState.findCompletedRows(GameState.shapeY, 4, completedRows);
            if (count == 0) {
                clearPreview();
                GameState.nextPiece();
                drawPreview();
            } else {
                flashRowsEffect = 1;
            }

            return true;
        }
    }

    public static void engineRotateClockwise() {
        if (GameState.rotateCurrentShapeClockwise()) {
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, Shapes.shapeRotatedCounterClockwise[GameState.shapeIndex], 0);
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, GameState.shapeIndex, GameState.shapeColor);
        }
    }

    public static void engineRotateCounterClockwise() {
        if (GameState.rotateCurrentShapeCounterClockwise()) {
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, Shapes.shapeRotatedClockwise[GameState.shapeIndex], 0);
            Draw.drawShapeOnGameArea(GameState.shapeX, GameState.shapeY, GameState.shapeIndex, GameState.shapeColor);
        }
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
                    Draw.drawGameArea(GameState.gameArea);
                }
                clearPreview();
                GameState.nextPiece();
                drawPreview();
            } else {
                drawFlashRowsEffect(completedRows, flashRowsEffect);
                flashRowsEffect++;
            }

            return;
        }

        if (buttonStates[Constants.BUTTON_INDEX_LEFT]) {
            buttonStates[Constants.BUTTON_INDEX_LEFT] = false;
            engineLeft();
        }

        if (buttonStates[Constants.BUTTON_INDEX_RIGHT]) {
            buttonStates[Constants.BUTTON_INDEX_RIGHT] = false;
            engineRight();
        }

        if (buttonStates[Constants.BUTTON_INDEX_DOWN]) {
            buttonStates[Constants.BUTTON_INDEX_DOWN] = false;
            while (!engineDown());
        } else {
            int level = GameState.rows / 10;
            if ((level > delayLevels) || (mainStepCounter % delayByLevel[level] == 0)) {
                engineDown();
            }
        }

        if (buttonStates[Constants.BUTTON_INDEX_ROTATE_CW]) {
            buttonStates[Constants.BUTTON_INDEX_ROTATE_CW] = false;
            engineRotateClockwise();
        }

        if (buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW]) {
            buttonStates[Constants.BUTTON_INDEX_ROTATE_CCW] = false;
            engineRotateCounterClockwise();
        }

    }

    public static void mainLoopTick() {
        if (gameRunning) {
            if (gameOver) {
                for (int i = 0; i < buttonStates.length; i++) {
                    buttonStates[i] = false;
                }
                gameRunning = false;
            } else {
                delayFrame();
                gameStep();
                mainStepCounter++;
            }
        } else {
            Draw.drawTitleScreen();
            boolean anyButtonPressed = false;
            for (int i = 0; i < buttonStates.length; i++) {
                anyButtonPressed |= buttonStates[i];
            }
            if (anyButtonPressed) {
                for (int i = 0; i < buttonStates.length; i++) {
                    buttonStates[i] = false;
                }
                Random.autoSeedRandom();
                gameRunning = true;
                engineNewGame();
            } else {
                Random.randomAutoSeederTick();
            }
        }
    }

}
