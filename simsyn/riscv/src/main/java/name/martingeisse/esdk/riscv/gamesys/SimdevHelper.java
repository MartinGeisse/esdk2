package name.martingeisse.esdk.riscv.gamesys;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class SimdevHelper {

    private final Peripherals peripherals;
    private final GameSystem gameSystem;

    public SimdevHelper(Peripherals peripherals) {
        this.peripherals = peripherals;
        this.gameSystem = peripherals.gameSystem;
    }

    public int read(int localWordAddress) {
        if (localWordAddress == 0) {
            return 1;
        } else {
            throw new BadAddressException();
        }
    }

    public void write(int localWordAddress, int data, int byteMask) {
        switch (localWordAddress) {

            case 0:
                gameSystem.simulationStopped = true;
                return;

            case 1:
                debugPrint(data);
                return;

            case 2:
                memoryHelper(data);
                return;

            case 3:
                System.out.println("displayPanel.setDisplayPlane(data & 1);");
                return;

            default:
                throw new BadAddressException();

        }
    }

    private void debugPrint(int subcode) {
        System.out.print("OUT:        ");
        int a0 = gameSystem.cpu.getRegister(10);
        int a1 = gameSystem.cpu.getRegister(11);
        switch (subcode) {

            case 0:
                System.out.println(readZeroTerminatedMemoryString(a0));
                break;

            case 1: {
                System.out.println(readZeroTerminatedMemoryString(a0) + ": " + a1 + " (0x" + Integer.toHexString(a1) + ")");
                break;
            }

            default:
                throw new RuntimeException("invalid debugPrint subcode");
        }
    }

    private void memoryHelper(int subcode) {
        int a0 = gameSystem.cpu.getRegister(10);
        int a1 = gameSystem.cpu.getRegister(11);
        int a2 = gameSystem.cpu.getRegister(12);
        switch (subcode) {

            // fill words
            case 0: {
                int wordAddress = (a0 >> 2);
                if (wordAddress < 0 || a2 < 0 || wordAddress + a2 >= peripherals.ram.length) {
                    throw new RuntimeException("invalid parameters for memory helper (fill words): address = " +
                            Integer.toHexString(a0) + ", word value = " + Integer.toHexString(a1) + ", count = " + a2);
                }
                Arrays.fill(peripherals.ram, wordAddress, wordAddress + a2, a1);
                break;
            }

            default:
                throw new RuntimeException("invalid memoryHelper subcode: " + subcode);
        }
    }

    public byte[] readMemoryBytes(int startAddress, int count) {
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            result[i] = readByte(startAddress + i);
        }
        return result;
    }

    public String readMemoryString(int startAddress, int count) {
        return new String(readMemoryBytes(startAddress, count), StandardCharsets.ISO_8859_1);
    }

    public String readZeroTerminatedMemoryString(int startAddress) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while (true) {
            byte b = readByte(startAddress);
            if (b == 0) {
                break;
            }
            stream.write(b);
            startAddress++;
        }
        return new String(stream.toByteArray(), StandardCharsets.ISO_8859_1);
    }

    private byte readByte(int address) {
        return (byte) (peripherals.read(address >> 2) >> ((address & 3) * 8));
    }

}
