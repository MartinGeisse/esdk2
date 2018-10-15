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
 * 1	1	continuation (depends on previous type). Expected contination marked as '.' below.
 * 2	2	completion (depends on previous type). Expected completion marked as '!' below.
 * 3	3	-
 * <p>
 * 4	4	unsigned 8-bit decimal integer
 * 5	5	signed 8-bit decimal integer
 * 6	6	unsigned 8-bit hexadecimal integer
 * 7	7	16 bit pattern
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

			default:
				out.print("(?)");
				break;
		}
	}

}
