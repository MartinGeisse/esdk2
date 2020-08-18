package name.martingeisse.esdk.riscv.rtl.pixel;

import org.lwjgl.opengl.GL11;

/**
 *
 */
public final class GlUtil {

	// prevent instantiation
	private GlUtil() {
	}

	public static void checkError() {
		int error = GL11.glGetError();
		if (error != 0) {
			throw new RuntimeException("GL error: " + error + " (0x" + Integer.toHexString(error) + ")");
		}
	}

}
