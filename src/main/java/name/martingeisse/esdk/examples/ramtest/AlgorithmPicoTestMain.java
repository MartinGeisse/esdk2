/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

/**
 * (4 x 8M) x 16 Bit = 32M * 16 Bit = 64 MB = 16MW
 * 1 W = 32 Bits
 */
public class AlgorithmPicoTestMain {

	private static final int ADDRESS_MASK = 0x00ffffff;
	private static int[] memory = new int[16 * 1024 * 1024];
	private static MemoryFaultModel memoryFaultModel = MemoryFaultModel.INTACT;

	private int addressRegister = 0;
	private int writeDataRegister = 0;
	private int readDataRegister = 0;

	public static void main(String[] args) {

		int x;

		x = 9;
		for (int i = 0; i < memory.length; i++) {
			memoryFaultModel.write(i, x);
			x = 5 * x + 1;
		}

		x = 9;
		for (int i = 0; i < memory.length; i++) {
			int value = memoryFaultModel.read(i);
			if (value != x) {
				System.out.println("error at " + i + ": " + value + " should be " + x);
			}
			x = 5 * x + 1;
		}

	}

	private void write(int port, int data) {
		int shift = (port & 3) * 8;
		int mask = ~(0xff << shift);
		int shiftedData = (data & 0xff) << shift;
		if ((port & 0x10) != 0) {
			addressRegister = (addressRegister & mask) | shiftedData;
		}
		if ((port & 0x20) != 0) {
			writeDataRegister = (writeDataRegister & mask) | shiftedData;
		}
		// port & 0x40 -- read data, cannot be written by CPU
		if ((port & 0x80) != 0) {
			if ((data & 1) != 0) {
				memoryFaultModel.write(addressRegister & ADDRESS_MASK, writeDataRegister);
			} else {
				readDataRegister = memoryFaultModel.read(addressRegister & ADDRESS_MASK);
			}
		}
	}

	private byte read(int port) {
		int value;
		if ((port & 0x10) != 0) {
			value = addressRegister;
		} else if ((port & 0x20) != 0) {
			value = writeDataRegister;
		} else if ((port & 0x40) != 0) {
			value = readDataRegister;
		} else {
			value = 0;
		}
		int shift = (port & 3) * 8;
		return (byte) (value >> shift);
	}

	enum MemoryFaultModel {

		INTACT(address -> memory[address], (address, data) -> memory[address] = data),
		READ_ZERO(address -> 0, (address, data) -> memory[address] = data),
		NO_WRITE(address -> memory[address], (address, data) -> {});

		private final Reader reader;
		private final Writer writer;

		MemoryFaultModel(Reader reader, Writer writer) {
			this.reader = reader;
			this.writer = writer;
		}

		int read(int address) {
			return reader.read(address);
		}

		void write(int address, int data) {
			writer.write(address, data);
		}

		interface Reader {
			int read(int address);
		}

		interface Writer {
			void write(int address, int data);
		}
	}
}
