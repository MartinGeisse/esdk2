package name.martingeisse.esdk.riscv.gamesys;

import name.martingeisse.esdk.riscv.instruction.io.IoUnit;
import name.martingeisse.esdk.riscv.instruction.muldiv.HardwareMultiplyDivideUnit;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class GameSystem implements IoUnit {

    public final Cpu cpu;
    public final Peripherals peripherals;
    public File memoryMapFile;
    public boolean simulationStopped;

    public GameSystem() {
        cpu = new Cpu();
        cpu.setMultiplyDivideUnit(new HardwareMultiplyDivideUnit(cpu));
        cpu.setIoUnit(this);
        peripherals = new Peripherals(this);
    }

    public void run() {
        try {
            while (!simulationStopped) {
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
        long address64 = 0xffff_ffffL & (long) address32;
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
        try {
            return peripherals.read(wordAddress);
        } catch (BadAddressException e) {
            throw BadAddressException.forInstructionFetch(wordAddress);
        }
    }

    @Override
    public int read(int wordAddress) {
        try {
            return peripherals.read(wordAddress);
        } catch (BadAddressException e) {
            throw BadAddressException.forRead(wordAddress);
        }
    }

    @Override
    public void write(int wordAddress, int data, int byteMask) {
        try {
            peripherals.write(wordAddress, data, byteMask);
        } catch (BadAddressException e) {
            throw BadAddressException.forWrite(wordAddress, data, byteMask);
        }
    }

}
