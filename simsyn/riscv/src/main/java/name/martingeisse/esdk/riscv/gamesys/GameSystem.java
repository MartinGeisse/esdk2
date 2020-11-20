package name.martingeisse.esdk.riscv.gamesys;

import name.martingeisse.esdk.riscv.instruction.io.IoUnit;
import name.martingeisse.esdk.riscv.instruction.muldiv.HardwareMultiplyDivideUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class GameSystem implements IoUnit {

    public final Cpu cpu;
    public final int[] ram;
    public final int[] fastRam;
    public final int[] fastRom;

    public GameSystem() {
        cpu = new Cpu();
        cpu.setMultiplyDivideUnit(new HardwareMultiplyDivideUnit(cpu));
        cpu.setIoUnit(this);
        ram = new int[Constants.RAM_SIZE_WORDS];
        fastRam = new int[Constants.FAST_RAM_SIZE_WORDS];
        fastRom = new int[Constants.FAST_ROM_SIZE_WORDS];
    }

    public void run() {
        while (true) {
            cpu.step();
        }
    }

    public void loadImage(File file, int address) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            while (true) {
                int dataByte = in.read();
                if (dataByte < 0) {
                    break;
                }
                int wordAddress = address >>> 2;
                int byteOffset = address & 3;
                int bitOffset = byteOffset * 8;
                write(wordAddress, dataByte << bitOffset, 1 << byteOffset);
                address++;
            }
        }

    }

    @Override
    public int fetchInstruction(int wordAddress) {
        return read(wordAddress);
    }

    @Override
    public int read(int wordAddress) {

        // normal RAM, including framebuffer
        if (wordAddress >= 0 && wordAddress < ram.length) {
            return ram[wordAddress];
        }

        // fast RAM and ROM
        if (wordAddress >= Constants.FAST_ROM_WORD_ADDRESS) {
            return fastRom[wordAddress - Constants.FAST_ROM_WORD_ADDRESS];
        }
        if (wordAddress >= Constants.FAST_RAM_WORD_ADDRESS) {
            return fastRam[wordAddress - Constants.FAST_RAM_WORD_ADDRESS];
        }

        // detect errors early
        throw new RuntimeException("unexpected read access to word address " + Integer.toHexString(wordAddress) +
                ", byte address " + Integer.toHexString(4 * wordAddress));

    }

    @Override
    public void write(int wordAddress, int data, int byteMask) {

        // normal RAM, including framebuffer
        if (wordAddress >= 0 && wordAddress < ram.length) {
            write(ram, wordAddress, data, byteMask);
            return;
        }

        // fast RAM
        if (wordAddress >= Constants.FAST_RAM_WORD_ADDRESS && wordAddress < Constants.FAST_ROM_WORD_ADDRESS) {
            write(fastRam, wordAddress - Constants.FAST_RAM_WORD_ADDRESS, data, byteMask);
            return;
        }

        // detect errors early
        throw new RuntimeException("unexpected write access to word address " + Integer.toHexString(wordAddress) +
                ", byte address " + Integer.toHexString(4 * wordAddress));

    }

    private static void write(int[] ram, int wordAddress, int data, int byteMask) {
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
