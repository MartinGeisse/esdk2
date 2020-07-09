package name.martingeisse.esdk.structural.midlevel;

public class Constants {

    private Constants() {
    }

    //
    public static final int BUTTON_INDEX_LEFT = 0;
    public static final int BUTTON_INDEX_RIGHT = 1;
    public static final int BUTTON_INDEX_DOWN = 2;
    public static final int BUTTON_INDEX_ROTATE_CW = 3;
    public static final int BUTTON_INDEX_ROTATE_CCW = 4;
    public static final int BUTTON_INDEX_COUNT = 5;

    // offet of the game area in screen coordinates
    public static final int GAME_AREA_X_ON_SCREEN = 3;
    public static final int GAME_AREA_Y_ON_SCREEN = 9;

    // offet of the (leftmost) preview box screen coordinates
    public static final int PREVIEW_X_ON_SCREEN = 1;
    public static final int PREVIEW_Y_ON_SCREEN = 1;

    // x offset of one preview box to the next
    public static final int PREVIEW_X_DELTA = 5;

    public static final int delayByLevel[] = {
            30, 27, 24, 21, 18, 15, 12, 8, 5, 2
    };

    public static final int delayLevels = 10;

    public static final int[] flashRowsEffectColors = new int[]{
            0, 3, 7, 3, 0, 3, 7
    };

    public static final int flashRowsEffectTotalLength = 35;

}
