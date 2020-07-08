package name.martingeisse.esdk.structural.midlevel;

import name.martingeisse.esdk.structural.midlevel.program.Draw;
import name.martingeisse.esdk.structural.midlevel.program.Engine;

public final class CpuProgramFragments extends AbstractCpuProgramFragments {

    public static final CpuProgramFragments INSTANCE = new CpuProgramFragments();

    private Label label;

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
                int row = (address >> 4);
                int column = (address & 15);
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
//region full-screen drawing

    public void clearScreen() {
        lyi(0);
        clc();
        loadFa8();
        while (true) {
            lxi(0);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
            if (isCarry()) {
                break;
            }
        }
        loadFa8();
        while (true) {
            lxi(0);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
            if (isCarry()) {
                break;
            }
        }
    }

    public void drawTitleScreen() {

        lxi(1);
        clc();
        loadFa8();
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
        loadFa8();
        //
        sxa(1, 0x03);
        sxa(1, 0x04);
        sxa(1, 0x13);
        sxa(1, 0x14);
        sxa(1, 0x23);
        sxa(1, 0x24);

        lxi(4);
        clc();
        loadFa8();
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
        loadFa8();
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

    public void drawBackground() {

        lyi(0);
        clc();
        loadFa8();
        while (true) {
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x04);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x4c);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x04);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x2e);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0xf0);
            loadFa8();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x60);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x23);
            if (isCarry()) {
                break;
            }
            loadFa8();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0c);
        }

        lyi(0x10);
        clc();
        loadFa8();
        while (true) {
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x05);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x05);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x05);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x73);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0b);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x35);
            loadFa8();
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0b);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x35);
            if (isCarry()) {
                loadFa8();
            }
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0b);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x35);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0b);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x35);
            lxi(7);
            sxn(1);
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x0b);
            lxi(7);
            sxn(1);

            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x33);
            if (isCarry()) {
                break;
            }
            loadFa8();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 0x50);

        }

        lxi(7);
        clc();
        loadFa8();
        sxa(1, 0x63);
        sxa(1, 0x64);
        sxa(1, 0x73);
        sxa(1, 0x74);

    }

//endregion
//region other drawing

    /**
     * Convenience wrapper.
     */
    public void fillGameRow(int y, int c) {
        Devices.memory[MemoryMap.TEMP_0] = (byte) y;
        Devices.memory[MemoryMap.TEMP_1] = (byte) c;
        fillGameRow();
    }

    /**
     * TEMP_0 must contain the row index, TEMP_1 the color to fill.
     */
    public void fillGameRow() {

        // x = 16 * TEMP_0, set fa8 to carry
        lxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        sxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        sxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        sxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        loadFa8();

        // x += 0x93, set fa8 on overflow
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x90);
        if (isCarry()) {
            loadFa8();
        }

        // draw line
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 3);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
        lxy();
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 1);
        lxa(MemoryMap.TEMP_1);
        sxn(1);
    }

    public void gameOver() {
        lxi(19);
        // invariant: X register contains the current row index
        while (true) {
            sxa(MemoryMap.UTIL_0);
            sxa(MemoryMap.TEMP_0);
            lxi(7);
            sxa(MemoryMap.TEMP_1);
            fillGameRow();
            Devices.delay(5);
            lxa(MemoryMap.UTIL_0);
            operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 1);
            if (!isCarry()) {
                break;
            }
        }
        label = Label.TITLE_SCREEN;
    }

    public void drawGameArea() {

        // set source pointer to 199
        lxi(199);
        sxa(MemoryMap.TEMP_0);

        // set row index to 19
        lxi(19);
        sxa(MemoryMap.TEMP_1);

        // row loop
        while (true) {

            // set column index to 9
            lxi(9);
            sxa(MemoryMap.TEMP_2);

            // column loop
            while (true) {

                // compute destination row base address, possibly set fa8 (first step)
                lxa(MemoryMap.TEMP_1);
                operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_1);
                sxa(MemoryMap.TEMP_3);
                operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_3);
                sxa(MemoryMap.TEMP_3);
                operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_3);
                sxa(MemoryMap.TEMP_3);
                operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_3);
                loadFa8();

                // x += 0x93, set fa8 on overflow
                operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x93);
                if (isCarry()) {
                    loadFa8();
                }

                // add column index, set fa8 on overflow
                operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_2);
                if (isCarry()) {
                    loadFa8();
                }

                // store computed address, then load block to transfer to X
                sxa(MemoryMap.TEMP_3);
                lya(MemoryMap.TEMP_0);
                lxn();

                // re-load destination address to Y
                lya(MemoryMap.TEMP_3);

                // save the block
                sxn(1);

                // move source pointer
                lxa(MemoryMap.TEMP_0);
                operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 1);
                sxa(MemoryMap.TEMP_0);

                // decrease the column index and loop
                lxa(MemoryMap.TEMP_2);
                operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 1);
                if (!isCarry()) {
                    break;
                }
                sxa(MemoryMap.TEMP_2);

            }


            // decrease the row index and loop
            lxa(MemoryMap.TEMP_1);
            operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 1);
            if (!isCarry()) {
                break;
            }
            sxa(MemoryMap.TEMP_1);

        }

    }

//endregion
//region game state

    public void clearGameArea() {
        lyi(199);
        while (true) {
            lxi(0);
            sxn();
            lxy();
            operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.Y, 255);
            if (!isCarry()) {
                break;
            }
        }
    }

//endregion
//region random number generator

    public void randomAutoSeederTick() {

        lxa(MemoryMap.RNG_SEEDER_0);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 1);
        sxa(MemoryMap.RNG_SEEDER_0);

        lxa(MemoryMap.RNG_SEEDER_1);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0);
        sxa(MemoryMap.RNG_SEEDER_1);

        lxa(MemoryMap.RNG_SEEDER_2);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0);
        sxa(MemoryMap.RNG_SEEDER_2);

        lxa(MemoryMap.RNG_SEEDER_3);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0);
        sxa(MemoryMap.RNG_SEEDER_3);

    }

    public void autoSeedRandom() {
        lxa(MemoryMap.RNG_SEEDER_0);
        sxa(MemoryMap.RNG_CURRENT_0);
        lxa(MemoryMap.RNG_SEEDER_1);
        sxa(MemoryMap.RNG_CURRENT_1);
        lxa(MemoryMap.RNG_SEEDER_2);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_SEEDER_3);
        sxa(MemoryMap.RNG_CURRENT_3);
    }

    /**
     * Computes a new random number mod 7 (all invocations happen to use 7), and stores the result in TEMP_0.
     */
    public void nextRandomMod7() {

        //
        // multiply by 1664525 (binary: 0000.0000 0001.1001 0110.0110 0000.1101)
        //

        // Copy to TEMP_0..3. That will keep the original number for now, while RNG_CURRENT_* will
        // accumulate the multiplication result, initialized to x1 to the LSB is already accounted for.
        lxa(MemoryMap.RNG_CURRENT_0);
        sxa(MemoryMap.TEMP_0);
        lxa(MemoryMap.RNG_CURRENT_1);
        sxa(MemoryMap.TEMP_1);
        lxa(MemoryMap.RNG_CURRENT_2);
        sxa(MemoryMap.TEMP_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        sxa(MemoryMap.TEMP_3);
        // remaining multiplier: 0000.0000 0001.1001 0110.0110 0000.1100

        // add *2^16 to the result
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_1);
        sxa(MemoryMap.RNG_CURRENT_3);
        // remaining multiplier: 0000.0000 0001.1000 0110.0110 0000.1100

        // next, set TEMP_4..7 to 3x the original number (0000.0011)
        lxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_1);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_1);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_2);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_2);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_3);
        sxa(MemoryMap.TEMP_7);
        //
        lxa(MemoryMap.TEMP_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_1);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_2);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // shift TEMP4..7 to (0000.0110)
        lxa(MemoryMap.TEMP_4);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_5);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_6);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_7);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // add *2^8
        lxa(MemoryMap.RNG_CURRENT_1);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.RNG_CURRENT_1);
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.RNG_CURRENT_3);
        // remaining multiplier: 0000.0000 0001.1000 0110.0000 0000.1100

        // shift TEMP4..7 to (0000.1100)
        lxa(MemoryMap.TEMP_4);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_5);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_6);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_7);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // add *1
        lxa(MemoryMap.RNG_CURRENT_0);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.RNG_CURRENT_0);
        lxa(MemoryMap.RNG_CURRENT_1);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.RNG_CURRENT_1);
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.RNG_CURRENT_3);
        // remaining multiplier: 0000.0000 0001.1000 0110.0000 0000.0000

        // shift TEMP4..7 to (0001.1000)
        lxa(MemoryMap.TEMP_4);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_5);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_6);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_7);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // add *2^16
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.RNG_CURRENT_3);
        // remaining multiplier: 0000.0000 0000.0000 0110.0000 0000.0000

        // shift TEMP4..7 to (0011.0000)
        lxa(MemoryMap.TEMP_4);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_5);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_6);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_7);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // shift TEMP4..7 to (0110.0000)
        lxa(MemoryMap.TEMP_4);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.TEMP_5);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.TEMP_6);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.TEMP_7);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
        sxa(MemoryMap.TEMP_7);

        // add *2^8
        lxa(MemoryMap.RNG_CURRENT_1);
        operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
        sxa(MemoryMap.RNG_CURRENT_1);
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
        sxa(MemoryMap.RNG_CURRENT_3);
        // remaining multiplier: 0000.0000 0000.0000 0000.0000 0000.0000 -> done

        // add 1013904223 (0x 3C 6E F3 5F)
        lxa(MemoryMap.RNG_CURRENT_0);
        operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 0x5f);
        sxa(MemoryMap.RNG_CURRENT_0);
        lxa(MemoryMap.RNG_CURRENT_1);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0xf3);
        sxa(MemoryMap.RNG_CURRENT_1);
        lxa(MemoryMap.RNG_CURRENT_2);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0x6e);
        sxa(MemoryMap.RNG_CURRENT_2);
        lxa(MemoryMap.RNG_CURRENT_3);
        operation(Operation.ADDC, AddressingMode.IMMEDIATE, Destination.X, 0x3c);
        sxa(MemoryMap.RNG_CURRENT_3);

        // copy the current number to TEMP_4..7
        lxa(MemoryMap.RNG_CURRENT_0);
        sxa(MemoryMap.TEMP_4);
        lxa(MemoryMap.RNG_CURRENT_1);
        sxa(MemoryMap.TEMP_5);
        lxa(MemoryMap.RNG_CURRENT_2);
        sxa(MemoryMap.TEMP_6);
        lxa(MemoryMap.RNG_CURRENT_3);
        sxa(MemoryMap.TEMP_7);

        // initialize the shift-in byte (TEMP_0) to zero
        lxi(0);
        sxa(MemoryMap.TEMP_0);

        // initialize the digit counter (TEMP_1) to 31
        lxi(31);
        sxa(MemoryMap.TEMP_1);

        // division loop
        while (true) {

            // shift left by 1, then subtract 7 from the shift-in byte if possible
            lxa(MemoryMap.TEMP_4);
            operation(Operation.ADD, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_4);
            sxa(MemoryMap.TEMP_4);
            lxa(MemoryMap.TEMP_5);
            operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_5);
            sxa(MemoryMap.TEMP_5);
            lxa(MemoryMap.TEMP_6);
            operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_6);
            sxa(MemoryMap.TEMP_6);
            lxa(MemoryMap.TEMP_7);
            operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_7);
            sxa(MemoryMap.TEMP_7);
            lxa(MemoryMap.TEMP_0);
            operation(Operation.ADDC, AddressingMode.ABSOLUTE, Destination.X, MemoryMap.TEMP_0);
            operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 7);
            if (!isCarry()) {
                operation(Operation.ADD, AddressingMode.IMMEDIATE, Destination.X, 7);
            }
            sxa(MemoryMap.TEMP_0);

            // loop 32 times
            lxa(MemoryMap.TEMP_1);
            operation(Operation.SUB, AddressingMode.IMMEDIATE, Destination.X, 1);
            if (!isCarry()) {
                break;
            }
            sxa(MemoryMap.TEMP_1);

        }

    }

//endregion
//region main

    public void main() {
        label = Label.TITLE_SCREEN;
        while (true) {
            // set the label to null while switching, so if we forget to set it again, we get an exception
            if (label == null) {
                throw new RuntimeException("no label set");
            }
            Label previousLabel = label;
            label = null;
            labelSwitch: switch (previousLabel) {

                case TITLE_SCREEN:
                    Draw.drawTitleScreen();
                    label = Label.TITLE_SCREEN_LOOP;
                    break;

                case TITLE_SCREEN_LOOP:
                    for (int i = 0; i < Devices.buttonStates.length; i++) {
                        if (Devices.buttonStates[i]) {
                            label = Label.START_GAME;
                            break labelSwitch;
                        }
                    }
                    CpuProgramFragments.INSTANCE.randomAutoSeederTick();
                    Devices.delay();
                    label = Label.TITLE_SCREEN_LOOP;
                    break;

                case START_GAME:
                    CpuProgramFragments.INSTANCE.autoSeedRandom();
                    Engine.engineNewGame();
                    label = Label.GAME_LOOP;
                    break;

                case GAME_LOOP:
                    label = Label.GAME_LOOP;
                    Engine.gameTick();
                    break;

                default:
                    throw new RuntimeException("unknown label: " + label);

            }
        }
    }

    private enum Label {
        TITLE_SCREEN,
        TITLE_SCREEN_LOOP,
        START_GAME,
        GAME_LOOP,
    }

//endregion

}
