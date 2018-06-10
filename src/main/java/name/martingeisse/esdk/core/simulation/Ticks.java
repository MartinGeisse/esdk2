package name.martingeisse.esdk.core.simulation;

/**
 *
 */
public final class Ticks {

	// prevent instantiation
	private Ticks() {
	}

	public static long seconds(long n) {
		return n * 1_000_000_000;
	}

	public static long milliseconds(long n) {
		return n * 1_000_000;
	}

	public static long microseconds(long n) {
		return n * 1_000;
	}

	public static long nanoseconds(long n) {
		return n;
	}

}
