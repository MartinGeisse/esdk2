package name.martingeisse.esdk.structural.midlevel.program;

import name.martingeisse.esdk.structural.midlevel.CpuProgramFragments;
import name.martingeisse.esdk.structural.midlevel.Devices;
import name.martingeisse.esdk.structural.midlevel.MemoryMap;

@SuppressWarnings("ExplicitArrayFilling")
public final class Engine {

    private Engine() {
    }


    public static void delayFrame() {
        Devices.delay();
    }

    public static void delayFrames(int frameCount) {
        for (int i = 0; i < frameCount; i++) {
            delayFrame();
        }
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

    public static void clearPreview() {
        Draw.drawPieceInPreview(0, Devices.memory[MemoryMap.PREVIEW_PIECE_0] & 0xff, 0);
        Draw.drawPieceInPreview(1, Devices.memory[MemoryMap.PREVIEW_PIECE_1] & 0xff, 0);
        Draw.drawPieceInPreview(2, Devices.memory[MemoryMap.PREVIEW_PIECE_2] & 0xff, 0);
    }

    public static void drawPreview() {
        Draw.drawPieceInPreview(0, Devices.memory[MemoryMap.PREVIEW_PIECE_0] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_0] & 0xff);
        Draw.drawPieceInPreview(1, Devices.memory[MemoryMap.PREVIEW_PIECE_1] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_1] & 0xff);
        Draw.drawPieceInPreview(2, Devices.memory[MemoryMap.PREVIEW_PIECE_2] & 0xff, Devices.memory[MemoryMap.PREVIEW_COLOR_2] & 0xff);
    }


}
