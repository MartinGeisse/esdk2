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
        ram = new int[Constants.RAM_SIZE >> 2];
        fastRam = new int[512]; // single BlockRAM: 2kB == 512k words
        fastRom = new int[512]; // single BlockRAM: 2kB == 512k words
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
        return (wordAddress < ram.length ? ram[wordAddress] : 0);
    }

    @Override
    public void write(int wordAddress, int data, int byteMask) {
        if (wordAddress >= ram.length) {
            return;
        }
        if (byteMask == 15) {
            // optimization
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
