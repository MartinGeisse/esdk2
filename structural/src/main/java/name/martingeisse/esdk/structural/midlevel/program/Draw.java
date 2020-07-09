package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.Constants;
import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;

// TODO reduce to height 30 so coarse pixels can be a POW of fine pixels
public final class Draw {

    private Draw() {
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
                    Devices.frameBuffer[y2 * 40 + x2] = (byte) c;
                }
            }
        }
    }

    public static void drawShapeOnScreen(int x, int y, int shapeIndex, int c) {
        drawClippedShapeOnScreen(x, y, shapeIndex, c, 0, 0, 39, 29);
    }


    public static void drawShapeOnGameArea(int x, int y, int shapeIndex, int c) {
        drawClippedShapeOnScreen(x + Constants.GAME_AREA_X_ON_SCREEN, y + Constants.GAME_AREA_Y_ON_SCREEN,
                shapeIndex, c,
                Constants.GAME_AREA_X_ON_SCREEN, Constants.GAME_AREA_Y_ON_SCREEN,
                Constants.GAME_AREA_X_ON_SCREEN + 9, Constants.GAME_AREA_Y_ON_SCREEN + 19);
    }

    public static void drawShapeInPreview(int previewIndex, int shapeIndex, int c) {
        drawShapeOnScreen(Constants.PREVIEW_X_ON_SCREEN + previewIndex * Constants.PREVIEW_X_DELTA,
                Constants.PREVIEW_Y_ON_SCREEN, shapeIndex, c);
    }

    public static void drawPieceInPreview(int previewIndex, int pieceIndex, int c) {
        drawShapeInPreview(previewIndex, Shapes.normalShapeByPiece[pieceIndex], c);
    }

}
