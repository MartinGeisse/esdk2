package name.martingeisse.esdk.structural.midlevel;

/**
 * The TEMP_* cells can be overwritten by any function at any time, so they can only be used between function calls
 * (including the whole of a leaf function).
 *
 * The UTIL_* cells are similar, multi-purpose cells but are used for "outer" functions. Any utility cell gets shared
 * only between a few places, which are clearly specified for each cell.
 */
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

    // random number generator
    public static final int RNG_SEEDER_0 = 211;
    public static final int RNG_SEEDER_1 = 212;
    public static final int RNG_SEEDER_2 = 213;
    public static final int RNG_SEEDER_3 = 214;
    public static final int RNG_CURRENT_0 = 215;
    public static final int RNG_CURRENT_1 = 216;
    public static final int RNG_CURRENT_2 = 217;
    public static final int RNG_CURRENT_3 = 218;

    public static final int TEMP_3 = 219;
    public static final int TEMP_4 = 220;
    public static final int TEMP_5 = 221;
    public static final int TEMP_6 = 222;
    public static final int TEMP_7 = 223;

    /**
     * This cell contains a site-specific value that indicates which place to return to from a function. Since my CPU
     * does not have indirect jumps, each function that gets called from multiple sites must return to a site selected
     * by an if-elseif-else chain based on the value of this cell. Possible values and their meanings are defined
     * individually for each function that gets called from multiple sites.
     *
     * NOTE: This might stay unused in the "midlevel" implementation since control flow happens in Java code, but it's
     * a good move anyway to reserve a memory cell for it.
     */
    public static final int RETURN_SELECTOR = 224;

    /**
     * Uses:
     * - new game: preview shift-in counter
     * - "game over fill": row counter
     *
     */
    public static final int UTIL_0 = 225;

    // main loop state
    public static final int GAME_DELAY_COUNTER = 228;
    public static final int MOVEMENT_DELAY_COUNTER = 229;
    public static final int FLASH_ROWS_EFFECT = 230;

    // accumulated rows for current level, and level counter
    public static final int ROW_COUNTER = 231;
    public static final int LEVEL = 232;

    // TODO shape and backup, move together
    public static final int CURRENT_X = 233;
    public static final int CURRENT_Y = 234;
    public static final int OLD_SHAPE = 235;
    public static final int OLD_X = 236;
    public static final int OLD_Y = 237;

    // number and indices of completed rows. Indices must be in ascending order, and the last index is repeated to fill
    // a total of 5 index cells. This simplifies the remaining logic.
    public static final int COMPLETED_ROW_COUNT = 238;
    public static final int COMPLETED_ROW_INDEX_0 = 239;
    public static final int COMPLETED_ROW_INDEX_1 = 240;
    public static final int COMPLETED_ROW_INDEX_2 = 241;
    public static final int COMPLETED_ROW_INDEX_3 = 242;
    public static final int COMPLETED_ROW_INDEX_4 = 243;

}
