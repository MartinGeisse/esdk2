package name.martingeisse.esdk.structural.midlevel;

public final class CpuProgramFragments extends AbstractCpuProgramFragments {

    public static final CpuProgramFragments INSTANCE = new CpuProgramFragments();

//region memory access

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
                Devices.memory[address & 0xff] = (byte) value;
                break;

            case 1: {
                // now this is a hack, using the carry as an extra address bit
                int localAddress = (address & 0xff) | (isCarry() ? 0x100 : 0);
                int row = (localAddress >> 4);
                int column = (localAddress & 15);
                if (row < 30) {
                    Devices.frameBuffer[row * 40 + column] = (byte) value;
                }
                break;
            }

            default:
                throw new RuntimeException();

        }
    }

//endregion
//region drawing

    public void clearScreen() {

        lxi(0);
        lyi(0);
        sxa(MemoryMap.TEMP_0);
        sb(1);

        while (true) {
            sxn();
        }

    }

    public void drawTitleScreen() {
        sb(1);

        lxi(1);
        sxa(0x11);
        sxa(0x12);
        sxa(0x13);
        sxa(0x14);
        sxa(0x15);
        sxa(0x16);
        sxa(0x21);
        sxa(0x22);
        sxa(0x23);
        sxa(0x24);
        sxa(0x25);
        sxa(0x26);
        sxa(0x33);
        sxa(0x34);
        sxa(0x43);
        sxa(0x44);
        sxa(0x53);
        sxa(0x54);
        sxa(0x63);
        sxa(0x64);
        sxa(0x73);
        sxa(0x74);
        sxa(0x83);
        sxa(0x84);

        lxi(2);
        sxa(0x19);
        sxa(0x1a);
        sxa(0x1b);
        sxa(0x1c);
        sxa(0x1d);
        sxa(0x1e);
        sxa(0x29);
        sxa(0x2a);
        sxa(0x2b);
        sxa(0x2c);
        sxa(0x2d);
        sxa(0x39);
        sxa(0x3a);
        sxa(0x49);
        sxa(0x4a);
        sxa(0x4b);
        sxa(0x4c);
        sxa(0x59);
        sxa(0x5a);
        sxa(0x5b);
        sxa(0x5c);
        sxa(0x69);
        sxa(0x6a);
        sxa(0x79);
        sxa(0x7a);
        sxa(0x7b);
        sxa(0x7c);
        sxa(0x7d);
        sxa(0x89);
        sxa(0x8a);
        sxa(0x8b);
        sxa(0x8c);
        sxa(0x8d);
        sxa(0x8e);

        lxi(3);
        sxa(0xb1);
        sxa(0xb2);
        sxa(0xb3);
        sxa(0xb4);
        sxa(0xb5);
        sxa(0xb6);
        sxa(0xc1);
        sxa(0xc2);
        sxa(0xc3);
        sxa(0xc4);
        sxa(0xc5);
        sxa(0xc6);
        sxa(0xd3);
        sxa(0xd4);
        sxa(0xe3);
        sxa(0xe4);
        sxa(0xf3);
        sxa(0xf4);
        //
        lxi(0x83);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        //
        sxa(0x03);
        sxa(0x04);
        sxa(0x13);
        sxa(0x14);
        sxa(0x23);
        sxa(0x24);

        lxi(4);
        sxa(0xb9);
        sxa(0xba);
        sxa(0xbb);
        sxa(0xbc);
        sxa(0xc9);
        sxa(0xca);
        sxa(0xcb);
        sxa(0xcc);
        sxa(0xcd);
        sxa(0xce);
        sxa(0xd9);
        sxa(0xda);
        sxa(0xdd);
        sxa(0xde);
        sxa(0xe9);
        sxa(0xea);
        sxa(0xeb);
        sxa(0xec);
        sxa(0xed);
        sxa(0xee);
        sxa(0xf9);
        sxa(0xfa);
        sxa(0xfb);
        sxa(0xfc);
        //
        lxi(0x84);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        //
        sxa(0x09);
        sxa(0x0a);
        sxa(0x0c);
        sxa(0x0d);
        sxa(0x19);
        sxa(0x1a);
        sxa(0x1c);
        sxa(0x1d);
        sxa(0x1e);
        sxa(0x29);
        sxa(0x2a);
        sxa(0x2d);
        sxa(0x2e);

        lxi(0x85);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        sxa(0x52);
        sxa(0x53);
        sxa(0x54);
        sxa(0x55);
        sxa(0x63);
        sxa(0x64);
        sxa(0x73);
        sxa(0x74);
        sxa(0x83);
        sxa(0x84);
        sxa(0x93);
        sxa(0x94);
        sxa(0xa3);
        sxa(0xa4);
        sxa(0xb3);
        sxa(0xb4);
        sxa(0xc2);
        sxa(0xc3);
        sxa(0xc4);
        sxa(0xc5);

        lxi(0x86);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        sxa(0x5a);
        sxa(0x5b);
        sxa(0x5c);
        sxa(0x5d);
        sxa(0x69);
        sxa(0x6a);
        sxa(0x6b);
        sxa(0x6c);
        sxa(0x6d);
        sxa(0x79);
        sxa(0x7a);
        sxa(0x89);
        sxa(0x8a);
        sxa(0x8b);
        sxa(0x8c);
        sxa(0x8d);
        sxa(0x9a);
        sxa(0x9b);
        sxa(0x9c);
        sxa(0x9d);
        sxa(0x9e);
        sxa(0xad);
        sxa(0xae);
        sxa(0xba);
        sxa(0xbb);
        sxa(0xbc);
        sxa(0xbd);
        sxa(0xbe);
        sxa(0xca);
        sxa(0xcb);
        sxa(0xcc);
        sxa(0xcd);

    }

//endregion
}
