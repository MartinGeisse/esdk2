package name.martingeisse.esdk.library.riscv;

import org.junit.Assert;

/**
 *
 */
public final class Trace {

	private static final int DEFAULT_INITIAL_CAPACITY = 256;
	private static final int GROW_CAPACITY = 256;

	private int[] elements;
	private int length;

	public Trace() {
		this(256);
	}

	public Trace(int initialCapacity) {
		elements = new int[initialCapacity];
		length = 0;
	}

	public static Trace from(int... elements) {
		Trace trace = new Trace(elements.length);
		for (int element : elements) {
			trace.append(element);
		}
		return trace;
	}

	public void allocate(int capacity) {
		if (capacity < length) {
			throw new IllegalStateException("trace is already longer than " + capacity);
		}
		int[] oldElements = elements;
		elements = new int[capacity];
		System.arraycopy(oldElements, 0, elements, 0, length);
	}

	public void append(int element) {
		if (length == elements.length) {
			allocate(length + GROW_CAPACITY);
		}
		elements[length] = element;
		length++;
	}

	public void toString(StringBuilder builder) {
		boolean first = true;
		for (int i = 0; i < length; i++) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append(elements[i]);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	public void assertEquals(int... expectedElements) {
		int i = 0;
		while (true) {
			if (i == length && i == expectedElements.length) {
				// traces are equal
				break;
			}
			assertHelper("expected more trace elements", i != length, expectedElements);
			assertHelper("unexpected elements at end of trace", i != expectedElements.length, expectedElements);
			assertHelper("trace elements differ", elements[i] == expectedElements[i], expectedElements);
			i++;
		}
	}

	private void assertHelper(String message, boolean expectedCondition, int[] expectedElements) {
		if (!expectedCondition) {
			Assert.fail(message + "\nexpected: " + from(expectedElements) + "\nactual: " + this);
		}
	}

}
