package name.martingeisse.esdk.riscv.experiment;

import java.util.Random;

/**
 *
 */
public class MultiplyAlgorithm {

	public static void main(String[] args) {

		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			long x = random.nextLong();
			long y = random.nextLong();
			System.out.println(x + " * " + y + " = " + (x * y) + " // " + multiply(x, y));
		}
	}

//	private static long multiply(long x, long y) {
//		long a = x, b = 0;
//		for (int i = 0; i < 64; i++) {
//			if ((a & 1) != 0) {
//				b += (y << i);
//			}
//			a = a >> 1;
//		}
//		return b;
//	}

	private static long multiply(long a, long y) {
		long b = 0;
		for (int i = 0; i < 64; i++) {
			if ((a & 1) != 0) {
				b += (y << i);
			}
			a = a >> 1;
		}
		return b;
	}

}
