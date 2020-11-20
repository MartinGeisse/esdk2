package name.martingeisse.esdk.riscv.gamesys;

/**
 * RAM start is at address 0 to simplify things.
 */
public final class Constants {

    private Constants() {
    }

    private static final int WORD_ADDRESS_MASK = 0x3fff_ffff;

    public static final int RAM_SIZE_BYTES = 16 * 1024 * 1024;
    public static final int RAM_SIZE_WORDS = RAM_SIZE_BYTES / 4;
    public static final int FRAMEBUFFER_SIZE_BYTES = 512 * 1024;
    public static final int FRAMEBUFFER_SIZE_WORDS = FRAMEBUFFER_SIZE_BYTES / 4;
    public static final int FRAMEBUFFER_0_ADDRESS = RAM_SIZE_BYTES - 2 * FRAMEBUFFER_SIZE_BYTES;
    public static final int FRAMEBUFFER_0_WORD_ADDRESS = (FRAMEBUFFER_0_ADDRESS / 4) & WORD_ADDRESS_MASK;
    public static final int FRAMEBUFFER_1_ADDRESS = RAM_SIZE_BYTES - FRAMEBUFFER_SIZE_BYTES;
    public static final int FRAMEBUFFER_1_WORD_ADDRESS = (FRAMEBUFFER_1_ADDRESS / 4) & WORD_ADDRESS_MASK;
    public static final int FAST_RAM_ADDRESS = 0xffff_f000;
    public static final int FAST_RAM_WORD_ADDRESS = (FAST_RAM_ADDRESS / 4) & WORD_ADDRESS_MASK;
    public static final int FAST_RAM_SIZE_BYTES = 2048;
    public static final int FAST_RAM_SIZE_WORDS = FAST_RAM_SIZE_BYTES / 4;
    public static final int FAST_ROM_ADDRESS = 0xffff_f800;
    public static final int FAST_ROM_WORD_ADDRESS = (FAST_ROM_ADDRESS / 4) & WORD_ADDRESS_MASK;
    public static final int FAST_ROM_SIZE_BYTES = 2048;
    public static final int FAST_ROM_SIZE_WORDS = FAST_ROM_SIZE_BYTES / 4;

}
