package name.martingeisse.esdk.experiment;

public class IntegerSquareRootExperimentMain {

    public static void main(String[] args) {
        for (int k = 1; k < 32768; k++) {
            int start = k << 16;
            int end = start + 65536;
            for (int i = start; i < end; i++) {
                test(i);
            }
            System.out.println("DONE: " + k);
        }
    }

    static void test(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("negative: " + x);
        }
        int root = newton(x);
        if ((root - 1) * (root - 1) >= x) {
            throw new RuntimeException(x + " --> " + root);
        }
        if ((root + 1) * (root + 1) <= x) {
            throw new RuntimeException(x + " --> " + root);
        }
        // System.out.println("ok: " + x + " --> " + root);
    }

    static int newton(int x) {
        if (x <= 0) {
            return 0;
        }
        int r  = x;
        while (true) {
            int next = (r + x / r) >> 1;
            if (next >= r - 1 && next <= r + 1) {
                return next;
            }
            r = next;
        }
    }

}
