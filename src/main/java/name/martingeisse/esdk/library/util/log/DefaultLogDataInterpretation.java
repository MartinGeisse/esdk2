package name.martingeisse.esdk.library.util.log;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * This class is final to prevent subclasses from assigning type numbers which later get double-assigned by this
 * base class when it evolves.
 * <p>
 * This class recognizes the following types:
 * <p>
 * 0	0	ISO-8859-1 characters
 * 1	1	start byte
 * 2	2	continuation byte
 * 3	3	-
 * <p>
 * 4	4	unsigned 8-bit decimal integer
 * 5	5	signed 8-bit decimal integer
 * 6	6	unsigned 8-bit hexadecimal integer
 * 7	7	8 bit pattern
 * <p>
 * 8	8	unsigned 16-bit decimal integer (c!)
 * 9	9	signed 16-bit decimal integer (c!)
 * 10	a	unsigned 16-bit hexadecimal integer (c!)
 * 11	b	16 bit pattern (c!)
 * <p>
 * 12	c	unsigned 32-bit decimal integer (c..!)
 * 13	d	signed 32-bit decimal integer (c..!)
 * 14	e	unsigned 32-bit hexadecimal integer (c..!)
 * 15	f	32 bit pattern (c..!)
 */
public final class DefaultLogDataInterpretation implements LogDataInterpretation {

	private final PrintWriter out;
	private final byte[] buffer = new byte[1];

	private int value;

	public DefaultLogDataInterpretation(PrintWriter out) {
		this.out = out;
	}

	public void consume(int data, int type) {
		// TODO
		switch (data) {

			case 0:
				buffer[0] = (byte)data;
				out.print(new String(buffer, 0, 1, StandardCharsets.ISO_8859_1));
				break;

			case 1:
				value = data;
				break;

			case 2:
				value = (value << 8) + data;
				break;
				
			case 4:
				System.out.print(data);
				break;

			case 5:
				System.out.print((byte)data);
				break;

			case 6:
				System.out.print(Integer.toHexString(data));
				break;

			case 7:
				printBits(data, 8);
				break;

			case 9:
			case 10:
			case 11:
			case 12:
				value = data;
				break;

			default:
				out.print("(?)");
				break;
		}
	}

	private void printBits(int value, int width) {
		while (width > 0) {
			width--;
			System.out.print(((value >> width) & 1) == 0 ? '0' : '1');
		}
	}

}
