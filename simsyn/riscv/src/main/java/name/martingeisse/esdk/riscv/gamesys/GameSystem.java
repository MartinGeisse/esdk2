package name.martingeisse.esdk.riscv.gamesys;

import name.martingeisse.esdk.riscv.instruction.io.IoUnit;
import name.martingeisse.esdk.riscv.instruction.muldiv.HardwareMultiplyDivideUnit;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class GameSystem implements IoUnit {

    public final Cpu cpu;
    public final int[] ram;
    public final int[] fastRam;
    public final int[] fastRom;
    public File memoryMapFile;

    public GameSystem() {
        cpu = new Cpu();
        cpu.setMultiplyDivideUnit(new HardwareMultiplyDivideUnit(cpu));
        cpu.setIoUnit(this);
        ram = new int[Constants.RAM_SIZE_WORDS];
        fastRam = new int[Constants.FAST_RAM_SIZE_WORDS];
        fastRom = new int[Constants.FAST_ROM_SIZE_WORDS];
    }

    public void run() {
        try {
            while (true) {
                cpu.step();
            }
        } catch (Exception e) {
            System.err.println("--- exception occurred (Java stack trace below) ---");
            System.err.println();
            System.err.println("CPU state:");
            System.err.println("pc = " + cpu.getPc() + " (0x" + Integer.toHexString(cpu.getPc()) + ")");
            System.err.println();
            try {
                String functionName = mapAddressToFunctionName(cpu.getPc());
                if (functionName != null) {
                    System.err.println("C function: " + functionName);
                    System.err.println();
                }
            } catch (Exception e2) {
                System.err.println("failed to map address to function name: " + e2.getMessage());
            }
            e.printStackTrace(System.err);
            System.exit(1);
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

    public String mapAddressToFunctionName(int address32) throws Exception {
        if (memoryMapFile == null) {
            return null;
        }
        long address64 = 0xffff_ffffL & (long)address32;
        boolean foundStarterLine = false;
        String lastLineWithLowerAddress = null;
        try (FileInputStream fileInputStream = new FileInputStream(memoryMapFile)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1)) {
                try (LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader)) {
                    while (true) {
                        String line = lineNumberReader.readLine();
                        if (line == null) {
                            break;
                        }
                        if (foundStarterLine) {
                            if (line.equals("/DISCARD/")) {
                                foundStarterLine = false;
                            } else {
                                int index1 = line.indexOf("0x0000");
                                if (index1 >= 0) {
                                    int index2 = line.indexOf(' ', index1);
                                    if (index2 >= 0) {
                                        long lineAddress64 = Long.parseLong(line.substring(index1 + 2, index2), 16);
                                        if (address64 >= lineAddress64) {
                                            lastLineWithLowerAddress = line;
                                        }
                                    }
                                }
                            }
                        } else if (line.equals("Linker script and memory map")) {
                            foundStarterLine = true;
                        }
                    }
                }
            }
        }
        if (lastLineWithLowerAddress == null) {
            return null;
        }
        int index = lastLineWithLowerAddress.lastIndexOf(' ');
        return index < 0 ? lastLineWithLowerAddress : lastLineWithLowerAddress.substring(index + 1);
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
