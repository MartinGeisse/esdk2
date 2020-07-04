package name.martingeisse.esdk.structural.midlevel;

public final class MemoryMap {

    private MemoryMap() {
    }

    // this is implicitly assumed by most code
    public static final int GAME_AREA = 0;

    public static final int TEMP_0 = 200;
    public static final int TEMP_1 = 201;
    public static final int TEMP_2 = 202;

    // preview pieces and their colors
    public static final int PREVIEW_PIECE_0 = 203;
    public static final int PREVIEW_COLOR_0 = 204;
    public static final int PREVIEW_PIECE_1 = 205;
    public static final int PREVIEW_COLOR_1 = 206;
    public static final int PREVIEW_PIECE_2 = 207;
    public static final int PREVIEW_COLOR_2 = 208;

    // current shape and color
    public static final int CURRENT_SHAPE = 209;
    public static final int CURRENT_COLOR = 210;


}
