package name.martingeisse.esdk.riscv.gamesys;

/**
 * RAM start is at address 0 to simplify things.
 */
public final class Constants {

    private Constants() {
    }

    public static final int RAM_SIZE = 16 * 1024 * 1024;
    public static final int FRAMEBUFFER_SIZE = 512 * 1024;
    public static final int FRAMEBUFFER_0_ADDRESS = RAM_SIZE - 2 * FRAMEBUFFER_SIZE;
    public static final int FRAMEBUFFER_1_ADDRESS = RAM_SIZE - FRAMEBUFFER_SIZE;
    public static final int FAST_RAM_ADDRESS = 0xffff_f000;
    public static final int FAST_RAM_SIZE = 2048;
    public static final int FAST_ROM_ADDRESS = 0xffff_f800;
    public static final int FAST_ROM_SIZE = 2048;

}
