package name.martingeisse.esdk.structural.midlevel;

public final class CpuProgramFragments extends AbstractCpuProgramFragments {

    public static final CpuProgramFragments INSTANCE = new CpuProgramFragments();

    @Override
    protected int read(int address) {
        switch (getB()) {

            case 0:
                return Devices.memory[address & 0xff] & 0xff;

            case 1:
                return Devices.getButtonStates();

            default:
                throw new RuntimeException();

        }
    }

    @Override
    protected void write(int address, int value) {
        switch (getB()) {

            case 0:
                Devices.memory[address & 0xff] = (byte)value;
                break;

            case 1: {
                // now this is a hack, using the carry as an extra address bit
                int localAddress = (address & 0xff) | (isCarry() ? 0x100 : 0);
                int row = (localAddress >> 4);
                int column = (localAddress & 15);
                if (row < 30) {
                    Devices.frameBuffer[row * 40 + column] = (byte)value;
                }
                break;
            }

            default:
                throw new RuntimeException();

        }
    }

}
