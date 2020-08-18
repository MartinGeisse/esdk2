package name.martingeisse.esdk.structural.highlevel.program;

public final class Shapes {

    /* Number of pieces: 7  (O,  I,  T,  L,  J,  S,  Z)
     *
     * Piece index:
     * 0 - O
     * 1 - I
     * 2 - T
     * 3 - L
     * 4 - J
     * 5 - S
     * 6 - Z
     */
    public static final int numPieces = 7;

    /**
     * Number of shapes: 19 (1 + 2 + 4 + 4 + 4 + 2 + 2)
     *
     * Shape index:
     *  0 - O
     *  1 - lying I
     *  2 - standing I
     *  3 - regular (down-pointing) T
     *  4 - left-pointing T
     *  5 - up-pointing T
     *  6 - right-pointing T
     *  7 - regular L
     *  8 - "feet-up" L
     *  9 - upside-down L
     * 10 - "feet-down" L
     * 11 - regular J
     * 12 - "feet-up" J
     * 13 - upside-down J
     * 14 - "feet-down" J
     * 15 - regular S
     * 16 - rotated S
     * 17 - regular Z
     * 18 - rotated Z
     */
    public static final int numShapes = 19;

    /* This array maps piece indices to their "normal" shape index. This is,
     * for each piece, the shape to be displayed in the "next piece" display,
     * and also the initial shape to appear when the piece drops into the
     * game area.
     */
    public static final int[] normalShapeByPiece = new int[]{
            0, 2, 6, 7, 11, 16, 18
    };

    /* This array maps shape indices to their piece index.
     */
    public static final int[] pieceByShape = new int[]{
            0,
            1, 1,
            2, 2, 2, 2,
            3, 3, 3, 3,
            4, 4, 4, 4,
            5, 5,
            6, 6
    };

    // This array maps shapes to their corresponding clockwise rotated shape
    public static final int[] shapeRotatedClockwise = new int[]{
            0,
            2, 1,
            4, 5, 6, 3,
            10, 7, 8, 9,
            12, 13, 14, 11,
            16, 15,
            18, 17
    };

    // This array maps shapes to their corresponding counter-clockwise rotated shape
    public static final int[] shapeRotatedCounterClockwise = new int[]{
            0,
            2, 1,
            6, 3, 4, 5,
            8, 9, 10, 7,
            14, 11, 12, 13,
            16, 15,
            18, 17
    };

    /* This array defines which tiles are occupied by which shape. The size
     * of an occupation matrix is 4x4. The element type is boolean, with false
     * meaning 'free' and true meaning 'occupied'.
     */
    public static final boolean[][] shapeOccupationMatrices = new boolean[][]{
            { // 0
                    false, false, false, false,
                    false, true, true, false,
                    false, true, true, false,
                    false, false, false, false
            },
            { // 1
                    false, false, false, false,
                    false, false, false, false,
                    true, true, true, true,
                    false, false, false, false
            },
            { // 2
                    false, true, false, false,
                    false, true, false, false,
                    false, true, false, false,
                    false, true, false, false
            },
            { // 3
                    false, false, false, false,
                    true, true, true, false,
                    false, true, false, false,
                    false, false, false, false
            },
            { // 4
                    false, true, false, false,
                    true, true, false, false,
                    false, true, false, false,
                    false, false, false, false
            },
            { // 5
                    false, true, false, false,
                    true, true, true, false,
                    false, false, false, false,
                    false, false, false, false
            },
            { // 6
                    false, true, false, false,
                    false, true, true, false,
                    false, true, false, false,
                    false, false, false, false
            },
            { // 7
                    false, true, false, false,
                    false, true, false, false,
                    false, true, true, false,
                    false, false, false, false
            },
            { // 8
                    false, false, true, false,
                    true, true, true, false,
                    false, false, false, false,
                    false, false, false, false
            },
            { // 9
                    true, true, false, false,
                    false, true, false, false,
                    false, true, false, false,
                    false, false, false, false
            },
            { // 10
                    false, false, false, false,
                    true, true, true, false,
                    true, false, false, false,
                    false, false, false, false
            },
            { // 11
                    false, false, true, false,
                    false, false, true, false,
                    false, true, true, false,
                    false, false, false, false
            },
            { // 12
                    false, true, false, false,
                    false, true, true, true,
                    false, false, false, false,
                    false, false, false, false
            },
            { // 13
                    false, false, true, true,
                    false, false, true, false,
                    false, false, true, false,
                    false, false, false, false
            },
            { // 14
                    false, false, false, false,
                    false, true, true, true,
                    false, false, false, true,
                    false, false, false, false
            },
            { // 15
                    false, false, false, false,
                    false, false, true, true,
                    false, true, true, false,
                    false, false, false, false
            },
            { // 16
                    false, true, false, false,
                    false, true, true, false,
                    false, false, true, false,
                    false, false, false, false
            },
            { // 17
                    false, false, false, false,
                    false, true, true, false,
                    false, false, true, true,
                    false, false, false, false
            },
            { // 18
                    false, false, true, false,
                    false, true, true, false,
                    false, true, false, false,
                    false, false, false, false
            }
    };

    private Shapes() {
    }


}
