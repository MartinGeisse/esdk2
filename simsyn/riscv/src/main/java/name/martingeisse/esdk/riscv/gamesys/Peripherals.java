package name.martingeisse.esdk.riscv.gamesys;

public final class Peripherals {

    public final GameSystem gameSystem;
    public final int[] ram;
    public final int[] fastRam;
    public final int[] fastRom;
    public final SimdevHelper simdevHelper;

    public Peripherals(GameSystem gameSystem) {
        this.gameSystem = gameSystem;
        this.ram = new int[Constants.RAM_SIZE_WORDS];
        this.fastRam = new int[Constants.FAST_RAM_SIZE_WORDS];
        this.fastRom = new int[Constants.FAST_ROM_SIZE_WORDS];
        this.simdevHelper = new SimdevHelper(this);
    }

    public int read(int wordAddress) {

        // normal RAM, including framebuffer
        if (wordAddress < ram.length) {
            return ram[wordAddress];
        } else if (wordAddress < 0x1fff_ffff) {
            throw new BadAddressException();
        }

        // switch by device
        int deviceId = (wordAddress >> 22) & 127;
        int localWordAddress = wordAddress & 0x3f_ffff;
        switch (deviceId) {

            case 126:
                return simdevHelper.read(localWordAddress);

            case 127:
                if (wordAddress >= Constants.FAST_ROM_WORD_ADDRESS) {
                    return fastRom[wordAddress - Constants.FAST_ROM_WORD_ADDRESS];
                }
                if (wordAddress >= Constants.FAST_RAM_WORD_ADDRESS) {
                    return fastRam[wordAddress - Constants.FAST_RAM_WORD_ADDRESS];
                }
                throw new BadAddressException();

            default:
                throw new BadAddressException();

        }
    }

    public void write(int wordAddress, int data, int byteMask) {

        // normal RAM, including framebuffer
        if (wordAddress < ram.length) {
            write(ram, wordAddress, data, byteMask);
            return;
        } else if (wordAddress < 0x1fff_ffff) {
            throw new BadAddressException();
        }

        // switch by device
        int deviceId = (wordAddress >> 22) & 127;
        int localWordAddress = wordAddress & 0x3f_ffff;
        switch (deviceId) {

            case 126:
                simdevHelper.write(localWordAddress, data, byteMask);
                return;

            case 127:
                // fast RAM
                if (wordAddress >= Constants.FAST_RAM_WORD_ADDRESS && wordAddress < Constants.FAST_ROM_WORD_ADDRESS) {
                    write(fastRam, wordAddress - Constants.FAST_RAM_WORD_ADDRESS, data, byteMask);
                    return;
                }
                break;

            default:
                throw new BadAddressException();

        }
    }

    private void write(int[] ram, int wordAddress, int data, int byteMask) {
        if (byteMask == 15) { // optimization
            ram[wordAddress] = data;
            return;
        }
        if ((byteMask & 1) != 0) {
            ram[wordAddress] = (ram[wordAddress] & 0xffffff00) | (data & 0x000000ff);
        }
        if ((byteMask & 2) != 0) {
            ram[wordAddress] = (ram[wordAddress] & 0xffff00ff) | (data & 0x0000ff00);
        }
        if ((byteMask & 4) != 0) {
            ram[wordAddress] = (ram[wordAddress] & 0xff00ffff) | (data & 0x00ff0000);
        }
        if ((byteMask & 8) != 0) {
            ram[wordAddress] = (ram[wordAddress] & 0x00ffffff) | (data & 0xff000000);
        }
    }

}
