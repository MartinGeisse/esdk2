package name.martingeisse.esdk.structural.midlevel;

public final class CpuProgramFragments extends AbstractCpuProgramFragments {

    public static final CpuProgramFragments INSTANCE = new CpuProgramFragments();

//region memory access

    @Override
    protected int read(int bank, int address) {
        switch (bank) {

            case 0:
                return Devices.memory[address & 0xff] & 0xff;

            case 1:
                return Devices.getButtonStates();

            default:
                throw new RuntimeException();

        }
    }

    @Override
    protected void write(int bank, int address, int value) {
        switch (bank) {

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
/*
        lxi(0);
        lyi(0);
        sxa(MemoryMap.TEMP_0);

        while (true) {
            sxn();
        }
*/
    }

    public void drawTitleScreen() {

        lxi(1);
        sxa(1, 0x11);
        sxa(1, 0x12);
        sxa(1, 0x13);
        sxa(1, 0x14);
        sxa(1, 0x15);
        sxa(1, 0x16);
        sxa(1, 0x21);
        sxa(1, 0x22);
        sxa(1, 0x23);
        sxa(1, 0x24);
        sxa(1, 0x25);
        sxa(1, 0x26);
        sxa(1, 0x33);
        sxa(1, 0x34);
        sxa(1, 0x43);
        sxa(1, 0x44);
        sxa(1, 0x53);
        sxa(1, 0x54);
        sxa(1, 0x63);
        sxa(1, 0x64);
        sxa(1, 0x73);
        sxa(1, 0x74);
        sxa(1, 0x83);
        sxa(1, 0x84);

        lxi(2);
        sxa(1, 0x19);
        sxa(1, 0x1a);
        sxa(1, 0x1b);
        sxa(1, 0x1c);
        sxa(1, 0x1d);
        sxa(1, 0x1e);
        sxa(1, 0x29);
        sxa(1, 0x2a);
        sxa(1, 0x2b);
        sxa(1, 0x2c);
        sxa(1, 0x2d);
        sxa(1, 0x39);
        sxa(1, 0x3a);
        sxa(1, 0x49);
        sxa(1, 0x4a);
        sxa(1, 0x4b);
        sxa(1, 0x4c);
        sxa(1, 0x59);
        sxa(1, 0x5a);
        sxa(1, 0x5b);
        sxa(1, 0x5c);
        sxa(1, 0x69);
        sxa(1, 0x6a);
        sxa(1, 0x79);
        sxa(1, 0x7a);
        sxa(1, 0x7b);
        sxa(1, 0x7c);
        sxa(1, 0x7d);
        sxa(1, 0x89);
        sxa(1, 0x8a);
        sxa(1, 0x8b);
        sxa(1, 0x8c);
        sxa(1, 0x8d);
        sxa(1, 0x8e);

        lxi(3);
        sxa(1, 0xb1);
        sxa(1, 0xb2);
        sxa(1, 0xb3);
        sxa(1, 0xb4);
        sxa(1, 0xb5);
        sxa(1, 0xb6);
        sxa(1, 0xc1);
        sxa(1, 0xc2);
        sxa(1, 0xc3);
        sxa(1, 0xc4);
        sxa(1, 0xc5);
        sxa(1, 0xc6);
        sxa(1, 0xd3);
        sxa(1, 0xd4);
        sxa(1, 0xe3);
        sxa(1, 0xe4);
        sxa(1, 0xf3);
        sxa(1, 0xf4);
        //
        lxi(0x83);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        //
        sxa(1, 0x03);
        sxa(1, 0x04);
        sxa(1, 0x13);
        sxa(1, 0x14);
        sxa(1, 0x23);
        sxa(1, 0x24);

        lxi(4);
        sxa(1, 0xb9);
        sxa(1, 0xba);
        sxa(1, 0xbb);
        sxa(1, 0xbc);
        sxa(1, 0xc9);
        sxa(1, 0xca);
        sxa(1, 0xcb);
        sxa(1, 0xcc);
        sxa(1, 0xcd);
        sxa(1, 0xce);
        sxa(1, 0xd9);
        sxa(1, 0xda);
        sxa(1, 0xdd);
        sxa(1, 0xde);
        sxa(1, 0xe9);
        sxa(1, 0xea);
        sxa(1, 0xeb);
        sxa(1, 0xec);
        sxa(1, 0xed);
        sxa(1, 0xee);
        sxa(1, 0xf9);
        sxa(1, 0xfa);
        sxa(1, 0xfb);
        sxa(1, 0xfc);
        //
        lxi(0x84);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        //
        sxa(1, 0x09);
        sxa(1, 0x0a);
        sxa(1, 0x0c);
        sxa(1, 0x0d);
        sxa(1, 0x19);
        sxa(1, 0x1a);
        sxa(1, 0x1c);
        sxa(1, 0x1d);
        sxa(1, 0x1e);
        sxa(1, 0x29);
        sxa(1, 0x2a);
        sxa(1, 0x2d);
        sxa(1, 0x2e);

        lxi(0x85);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        sxa(1, 0x52);
        sxa(1, 0x53);
        sxa(1, 0x54);
        sxa(1, 0x55);
        sxa(1, 0x63);
        sxa(1, 0x64);
        sxa(1, 0x73);
        sxa(1, 0x74);
        sxa(1, 0x83);
        sxa(1, 0x84);
        sxa(1, 0x93);
        sxa(1, 0x94);
        sxa(1, 0xa3);
        sxa(1, 0xa4);
        sxa(1, 0xb3);
        sxa(1, 0xb4);
        sxa(1, 0xc2);
        sxa(1, 0xc3);
        sxa(1, 0xc4);
        sxa(1, 0xc5);

        lxi(0x86);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x80);
        sxa(1, 0x5a);
        sxa(1, 0x5b);
        sxa(1, 0x5c);
        sxa(1, 0x5d);
        sxa(1, 0x69);
        sxa(1, 0x6a);
        sxa(1, 0x6b);
        sxa(1, 0x6c);
        sxa(1, 0x6d);
        sxa(1, 0x79);
        sxa(1, 0x7a);
        sxa(1, 0x89);
        sxa(1, 0x8a);
        sxa(1, 0x8b);
        sxa(1, 0x8c);
        sxa(1, 0x8d);
        sxa(1, 0x9a);
        sxa(1, 0x9b);
        sxa(1, 0x9c);
        sxa(1, 0x9d);
        sxa(1, 0x9e);
        sxa(1, 0xad);
        sxa(1, 0xae);
        sxa(1, 0xba);
        sxa(1, 0xbb);
        sxa(1, 0xbc);
        sxa(1, 0xbd);
        sxa(1, 0xbe);
        sxa(1, 0xca);
        sxa(1, 0xcb);
        sxa(1, 0xcc);
        sxa(1, 0xcd);

    }

//endregion
}
