/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.textmode;

/**
 * This class contains the static character pixels.
 */
public class CharacterGenerator {

	/**
	 * The character data. This data should be regarded constant.
	 * The array is indexed by character code, and contains an
	 * array of row specifications in ascending texel row order.
	 * Each row specification contains 8 texels, where ascending
	 * bit positions indicate ascending texel columns.
	 * 
	 * For example, the element at index 65 of the major array
	 * contains the specification of the letter 'A'. The value at
	 * index 3 of the corresponding sub-array contains the texels
	 * for texel row 3 (counted from 0/top to 15/bottom). Bit
	 * 6 of this value -- extracted with the bit mask (1 << 6) --
	 * contains the texel value for texel column 6 (counted from
	 * 0/left to 7/right).
	 * 
	 * Set bits indicate foreground color, cleared bits indicate
	 * background color.
	 */
	public static final byte[][] CHARACTER_DATA = {
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 126, -127, -91, -127, -127, -67, -103, -127, -127, 126, 0, 0, 0, 0, },
		{0, 0, 126, -1, -37, -1, -1, -61, -25, -1, -1, 126, 0, 0, 0, 0, },
		{0, 0, 0, 0, 54, 127, 127, 127, 127, 62, 28, 8, 0, 0, 0, 0, },
		{0, 0, 0, 0, 8, 28, 62, 127, 62, 28, 8, 0, 0, 0, 0, 0, },
		{0, 0, 0, 24, 60, 60, -25, -25, -25, -103, 24, 60, 0, 0, 0, 0, },
		{0, 0, 0, 24, 60, 126, -1, -1, 126, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 24, 60, 60, 24, 0, 0, 0, 0, 0, 0, },
		{-1, -1, -1, -1, -1, -1, -25, -61, -61, -25, -1, -1, -1, -1, -1, -1, },
		{0, 0, 0, 0, 0, 60, 102, 66, 66, 102, 60, 0, 0, 0, 0, 0, },
		{-1, -1, -1, -1, -1, -61, -103, -67, -67, -103, -61, -1, -1, -1, -1, -1, },
		{0, 0, 120, 112, 88, 76, 30, 51, 51, 51, 51, 30, 0, 0, 0, 0, },
		{0, 0, 60, 102, 102, 102, 102, 60, 24, 126, 24, 24, 0, 0, 0, 0, },
		{0, 0, -4, -52, -4, 12, 12, 12, 12, 14, 15, 7, 0, 0, 0, 0, },
		{0, 0, -2, -58, -2, -58, -58, -58, -58, -26, -25, 103, 3, 0, 0, 0, },
		{0, 0, 0, 24, 24, -37, 60, -25, 60, -37, 24, 24, 0, 0, 0, 0, },
		{0, 1, 3, 7, 15, 31, 127, 31, 15, 7, 3, 1, 0, 0, 0, 0, },
		{0, 64, 96, 112, 120, 124, 127, 124, 120, 112, 96, 64, 0, 0, 0, 0, },
		{0, 0, 24, 60, 126, 24, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0, },
		{0, 0, 102, 102, 102, 102, 102, 102, 102, 0, 102, 102, 0, 0, 0, 0, },
		{0, 0, -2, -37, -37, -37, -34, -40, -40, -40, -40, -40, 0, 0, 0, 0, },
		{0, 62, 99, 6, 28, 54, 99, 99, 54, 28, 48, 99, 62, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 127, 127, 127, 127, 0, 0, 0, 0, },
		{0, 0, 24, 60, 126, 24, 24, 24, 24, 126, 60, 24, 126, 0, 0, 0, },
		{0, 0, 24, 60, 126, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, },
		{0, 0, 24, 24, 24, 24, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 24, 48, 127, 48, 24, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 12, 6, 127, 6, 12, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 3, 3, 3, 3, 127, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 20, 54, 127, 54, 20, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 8, 28, 28, 62, 62, 127, 127, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 127, 127, 62, 62, 28, 28, 8, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 24, 60, 60, 60, 24, 24, 24, 0, 24, 24, 0, 0, 0, 0, },
		{0, 102, 102, 102, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 54, 54, 127, 54, 54, 54, 127, 54, 54, 0, 0, 0, 0, },
		{24, 24, 62, 99, 67, 3, 62, 96, 97, 99, 62, 24, 24, 0, 0, 0, },
		{0, 0, 0, 0, 67, 99, 48, 24, 12, 6, 99, 97, 0, 0, 0, 0, },
		{0, 0, 28, 54, 54, 28, 110, 59, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 12, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 48, 24, 12, 12, 12, 12, 12, 12, 24, 48, 0, 0, 0, 0, },
		{0, 0, 12, 24, 48, 48, 48, 48, 48, 48, 24, 12, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 102, 60, -1, 60, 102, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 24, 24, 126, 24, 24, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 12, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0, 0, },
		{0, 0, 0, 0, 64, 96, 48, 24, 12, 6, 3, 1, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 115, 107, 107, 103, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 24, 28, 30, 24, 24, 24, 24, 24, 24, 126, 0, 0, 0, 0, },
		{0, 0, 62, 99, 96, 48, 24, 12, 6, 3, 99, 127, 0, 0, 0, 0, },
		{0, 0, 62, 99, 96, 96, 60, 96, 96, 96, 99, 62, 0, 0, 0, 0, },
		{0, 0, 48, 56, 60, 54, 51, 127, 48, 48, 48, 120, 0, 0, 0, 0, },
		{0, 0, 127, 3, 3, 3, 63, 112, 96, 96, 99, 62, 0, 0, 0, 0, },
		{0, 0, 28, 6, 3, 3, 63, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 127, 99, 96, 96, 48, 24, 12, 12, 12, 12, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 99, 62, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 99, 126, 96, 96, 96, 48, 30, 0, 0, 0, 0, },
		{0, 0, 0, 0, 24, 24, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 24, 24, 0, 0, 0, 24, 24, 12, 0, 0, 0, 0, },
		{0, 0, 0, 96, 48, 24, 12, 6, 12, 24, 48, 96, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 127, 0, 0, 127, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 6, 12, 24, 48, 96, 48, 24, 12, 6, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 48, 24, 24, 24, 0, 24, 24, 0, 0, 0, 0, },
		{0, 0, 0, 62, 99, 99, 123, 123, 123, 59, 3, 62, 0, 0, 0, 0, },
		{0, 0, 8, 28, 54, 99, 99, 127, 99, 99, 99, 99, 0, 0, 0, 0, },
		{0, 0, 63, 102, 102, 102, 62, 102, 102, 102, 102, 63, 0, 0, 0, 0, },
		{0, 0, 60, 102, 67, 3, 3, 3, 3, 67, 102, 60, 0, 0, 0, 0, },
		{0, 0, 31, 54, 102, 102, 102, 102, 102, 102, 54, 31, 0, 0, 0, 0, },
		{0, 0, 127, 102, 70, 22, 30, 22, 6, 70, 102, 127, 0, 0, 0, 0, },
		{0, 0, 127, 102, 70, 22, 30, 22, 6, 6, 6, 15, 0, 0, 0, 0, },
		{0, 0, 60, 102, 67, 3, 3, 123, 99, 99, 102, 92, 0, 0, 0, 0, },
		{0, 0, 99, 99, 99, 99, 127, 99, 99, 99, 99, 99, 0, 0, 0, 0, },
		{0, 0, 60, 24, 24, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 120, 48, 48, 48, 48, 48, 51, 51, 51, 30, 0, 0, 0, 0, },
		{0, 0, 103, 102, 54, 54, 30, 30, 54, 102, 102, 103, 0, 0, 0, 0, },
		{0, 0, 15, 6, 6, 6, 6, 6, 6, 70, 102, 127, 0, 0, 0, 0, },
		{0, 0, 99, 119, 127, 127, 107, 99, 99, 99, 99, 99, 0, 0, 0, 0, },
		{0, 0, 99, 103, 111, 127, 123, 115, 99, 99, 99, 99, 0, 0, 0, 0, },
		{0, 0, 28, 54, 99, 99, 99, 99, 99, 99, 54, 28, 0, 0, 0, 0, },
		{0, 0, 63, 102, 102, 102, 62, 6, 6, 6, 6, 15, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 99, 99, 99, 99, 107, 123, 62, 48, 112, 0, 0, },
		{0, 0, 63, 102, 102, 102, 62, 54, 102, 102, 102, 103, 0, 0, 0, 0, },
		{0, 0, 62, 99, 99, 6, 28, 48, 96, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 126, 126, 90, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 99, 99, 99, 99, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 99, 99, 99, 99, 99, 99, 99, 54, 28, 8, 0, 0, 0, 0, },
		{0, 0, 99, 99, 99, 99, 99, 107, 107, 127, 54, 54, 0, 0, 0, 0, },
		{0, 0, 99, 99, 54, 54, 28, 28, 54, 54, 99, 99, 0, 0, 0, 0, },
		{0, 0, 102, 102, 102, 102, 60, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 127, 99, 97, 48, 24, 12, 6, 67, 99, 127, 0, 0, 0, 0, },
		{0, 0, 60, 12, 12, 12, 12, 12, 12, 12, 12, 60, 0, 0, 0, 0, },
		{0, 0, 0, 1, 3, 7, 14, 28, 56, 112, 96, 64, 0, 0, 0, 0, },
		{0, 0, 60, 48, 48, 48, 48, 48, 48, 48, 48, 60, 0, 0, 0, 0, },
		{8, 28, 54, 99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, },
		{12, 12, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 7, 6, 6, 30, 54, 102, 102, 102, 102, 59, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 62, 99, 3, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 0, 56, 48, 48, 60, 54, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 62, 99, 127, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 0, 28, 54, 38, 6, 15, 6, 6, 6, 6, 15, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 110, 51, 51, 51, 51, 51, 62, 48, 51, 30, 0, },
		{0, 0, 7, 6, 6, 54, 110, 102, 102, 102, 102, 103, 0, 0, 0, 0, },
		{0, 0, 24, 24, 0, 28, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 96, 96, 0, 112, 96, 96, 96, 96, 96, 96, 102, 102, 60, 0, },
		{0, 0, 7, 6, 6, 102, 54, 30, 30, 54, 102, 103, 0, 0, 0, 0, },
		{0, 0, 28, 24, 24, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 55, 127, 107, 107, 107, 107, 107, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 59, 102, 102, 102, 102, 102, 102, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 62, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 59, 102, 102, 102, 102, 102, 62, 6, 6, 15, 0, },
		{0, 0, 0, 0, 0, 110, 51, 51, 51, 51, 51, 62, 48, 48, 120, 0, },
		{0, 0, 0, 0, 0, 59, 110, 70, 6, 6, 6, 15, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 62, 99, 6, 28, 48, 99, 62, 0, 0, 0, 0, },
		{0, 0, 8, 12, 12, 63, 12, 12, 12, 12, 108, 56, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 51, 51, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 102, 102, 102, 102, 102, 60, 24, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 99, 99, 99, 107, 107, 127, 54, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 99, 54, 28, 28, 28, 54, 99, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 99, 126, 96, 48, 31, 0, },
		{0, 0, 0, 0, 0, 127, 51, 24, 12, 6, 99, 127, 0, 0, 0, 0, },
		{0, 0, 112, 24, 24, 24, 14, 24, 24, 24, 24, 112, 0, 0, 0, 0, },
		{0, 0, 24, 24, 24, 24, 0, 24, 24, 24, 24, 24, 0, 0, 0, 0, },
		{0, 0, 14, 24, 24, 24, 112, 24, 24, 24, 24, 14, 0, 0, 0, 0, },
		{0, 0, 110, 59, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 8, 28, 54, 99, 99, 99, 127, 0, 0, 0, 0, 0, },
		{0, 0, 60, 102, 67, 3, 3, 3, 67, 102, 60, 48, 96, 62, 0, 0, },
		{0, 0, 51, 51, 0, 51, 51, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 48, 24, 12, 0, 62, 99, 127, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 8, 28, 54, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 51, 51, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 6, 12, 24, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 28, 54, 28, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 0, 0, 60, 102, 6, 6, 102, 60, 48, 96, 60, 0, 0, 0, },
		{0, 8, 28, 54, 0, 62, 99, 127, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 0, 99, 99, 0, 62, 99, 127, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 6, 12, 24, 0, 62, 99, 127, 3, 3, 99, 62, 0, 0, 0, 0, },
		{0, 0, 102, 102, 0, 28, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 24, 60, 102, 0, 28, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 6, 12, 24, 0, 28, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 99, 99, 8, 28, 54, 99, 99, 127, 99, 99, 99, 0, 0, 0, 0, },
		{28, 54, 28, 0, 28, 54, 99, 99, 127, 99, 99, 99, 0, 0, 0, 0, },
		{24, 12, 6, 0, 127, 102, 6, 62, 6, 6, 102, 127, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 51, 110, 108, 126, 27, 27, 118, 0, 0, 0, 0, },
		{0, 0, 124, 54, 51, 51, 127, 51, 51, 51, 51, 115, 0, 0, 0, 0, },
		{0, 8, 28, 54, 0, 62, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 99, 99, 0, 62, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 6, 12, 24, 0, 62, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 12, 30, 51, 0, 51, 51, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 6, 12, 24, 0, 51, 51, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 99, 99, 0, 99, 99, 99, 99, 99, 99, 126, 96, 48, 30, 0, },
		{0, 99, 99, 0, 28, 54, 99, 99, 99, 99, 54, 28, 0, 0, 0, 0, },
		{0, 99, 99, 0, 99, 99, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 24, 24, 60, 102, 6, 6, 6, 102, 60, 24, 24, 0, 0, 0, 0, },
		{0, 28, 54, 38, 6, 15, 6, 6, 6, 6, 103, 63, 0, 0, 0, 0, },
		{0, 0, 102, 102, 60, 24, 126, 24, 126, 24, 24, 24, 0, 0, 0, 0, },
		{0, 31, 51, 51, 31, 35, 51, 123, 51, 51, 51, 99, 0, 0, 0, 0, },
		{0, 112, -40, 24, 24, 24, 126, 24, 24, 24, 24, 24, 27, 14, 0, 0, },
		{0, 24, 12, 6, 0, 30, 48, 62, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 48, 24, 12, 0, 28, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, },
		{0, 24, 12, 6, 0, 62, 99, 99, 99, 99, 99, 62, 0, 0, 0, 0, },
		{0, 24, 12, 6, 0, 51, 51, 51, 51, 51, 51, 110, 0, 0, 0, 0, },
		{0, 0, 110, 59, 0, 59, 102, 102, 102, 102, 102, 102, 0, 0, 0, 0, },
		{110, 59, 0, 99, 103, 111, 127, 123, 115, 99, 99, 99, 0, 0, 0, 0, },
		{0, 60, 54, 54, 124, 0, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 28, 54, 54, 28, 0, 62, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 12, 12, 0, 12, 12, 6, 3, 99, 99, 62, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 127, 3, 3, 3, 3, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 127, 96, 96, 96, 96, 0, 0, 0, 0, 0, },
		{0, 3, 3, 67, 99, 51, 24, 12, 6, 115, -55, 96, 48, -8, 0, 0, },
		{0, 3, 3, 67, 99, 51, 24, 12, 102, 115, 89, -4, 96, -16, 0, 0, },
		{0, 0, 24, 24, 0, 24, 24, 24, 60, 60, 60, 24, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, -52, 102, 51, 102, -52, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 51, 102, -52, 102, 51, 0, 0, 0, 0, 0, 0, },
		{-120, 34, -120, 34, -120, 34, -120, 34, -120, 34, -120, 34, -120, 34, -120, 34, },
		{-86, 85, -86, 85, -86, 85, -86, 85, -86, 85, -86, 85, -86, 85, -86, 85, },
		{-69, -18, -69, -18, -69, -18, -69, -18, -69, -18, -69, -18, -69, -18, -69, -18, },
		{24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 24, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 31, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24, },
		{108, 108, 108, 108, 108, 108, 108, 111, 108, 108, 108, 108, 108, 108, 108, 108, },
		{0, 0, 0, 0, 0, 0, 0, 127, 108, 108, 108, 108, 108, 108, 108, 108, },
		{0, 0, 0, 0, 0, 31, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24, },
		{108, 108, 108, 108, 108, 111, 96, 111, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, },
		{0, 0, 0, 0, 0, 127, 96, 111, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, 111, 96, 127, 0, 0, 0, 0, 0, 0, 0, 0, },
		{108, 108, 108, 108, 108, 108, 108, 127, 0, 0, 0, 0, 0, 0, 0, 0, },
		{24, 24, 24, 24, 24, 31, 24, 31, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 31, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 24, 24, -8, 0, 0, 0, 0, 0, 0, 0, 0, },
		{24, 24, 24, 24, 24, 24, 24, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, -1, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 24, 24, -8, 24, 24, 24, 24, 24, 24, 24, 24, },
		{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{24, 24, 24, 24, 24, 24, 24, -1, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, -8, 24, -8, 24, 24, 24, 24, 24, 24, 24, 24, },
		{108, 108, 108, 108, 108, 108, 108, -20, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, -20, 12, -4, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, -4, 12, -20, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, -17, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, -1, 0, -17, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, -20, 12, -20, 108, 108, 108, 108, 108, 108, 108, 108, },
		{0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{108, 108, 108, 108, 108, -17, 0, -17, 108, 108, 108, 108, 108, 108, 108, 108, },
		{24, 24, 24, 24, 24, -1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{108, 108, 108, 108, 108, 108, 108, -1, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, -1, 0, -1, 24, 24, 24, 24, 24, 24, 24, 24, },
		{0, 0, 0, 0, 0, 0, 0, -1, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, 108, 108, -4, 0, 0, 0, 0, 0, 0, 0, 0, },
		{24, 24, 24, 24, 24, -8, 24, -8, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, -8, 24, -8, 24, 24, 24, 24, 24, 24, 24, 24, },
		{0, 0, 0, 0, 0, 0, 0, -4, 108, 108, 108, 108, 108, 108, 108, 108, },
		{108, 108, 108, 108, 108, 108, 108, -1, 108, 108, 108, 108, 108, 108, 108, 108, },
		{24, 24, 24, 24, 24, -1, 24, -1, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 24, 24, 31, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, -8, 24, 24, 24, 24, 24, 24, 24, 24, },
		{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
		{0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
		{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, },
		{-16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, },
		{-1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 110, 59, 27, 27, 27, 59, 110, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 63, 99, 63, 99, 99, 63, 3, 3, 3, 0, 0, },
		{0, 0, 127, 99, 99, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, },
		{0, 0, 0, 0, 1, 127, 54, 54, 54, 54, 54, 54, 0, 0, 0, 0, },
		{0, 0, 0, 127, 99, 6, 12, 24, 12, 6, 99, 127, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 126, 27, 27, 27, 27, 27, 14, 0, 0, 0, 0, },
		{0, 0, 0, 0, 102, 102, 102, 102, 102, 62, 6, 6, 3, 0, 0, 0, },
		{0, 0, 0, 0, 110, 59, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, },
		{0, 0, 0, 126, 24, 60, 102, 102, 102, 60, 24, 126, 0, 0, 0, 0, },
		{0, 0, 0, 28, 54, 99, 99, 127, 99, 99, 54, 28, 0, 0, 0, 0, },
		{0, 0, 28, 54, 99, 99, 99, 54, 54, 54, 54, 119, 0, 0, 0, 0, },
		{0, 0, 120, 12, 24, 48, 124, 102, 102, 102, 102, 60, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 126, -37, -37, -37, 126, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, -64, 96, 126, -13, -37, -49, 126, 6, 3, 0, 0, 0, 0, },
		{0, 0, 56, 12, 6, 6, 62, 6, 6, 6, 12, 56, 0, 0, 0, 0, },
		{0, 0, 0, 62, 99, 99, 99, 99, 99, 99, 99, 99, 0, 0, 0, 0, },
		{0, 0, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 24, 24, 126, 24, 24, 0, 0, -1, 0, 0, 0, 0, },
		{0, 0, 0, 12, 24, 48, 96, 48, 24, 12, 0, 126, 0, 0, 0, 0, },
		{0, 0, 0, 48, 24, 12, 6, 12, 24, 48, 0, 126, 0, 0, 0, 0, },
		{0, 0, 112, -40, -40, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, },
		{24, 24, 24, 24, 24, 24, 24, 24, 27, 27, 27, 14, 0, 0, 0, 0, },
		{0, 0, 0, 0, 24, 24, 0, 126, 0, 24, 24, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 110, 59, 0, 110, 59, 0, 0, 0, 0, 0, 0, },
		{0, 28, 54, 54, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, },
		{0, -16, 48, 48, 48, 48, 48, 55, 54, 54, 60, 56, 0, 0, 0, 0, },
		{0, 27, 54, 54, 54, 54, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 14, 25, 12, 6, 19, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 62, 62, 62, 62, 62, 62, 62, 0, 0, 0, 0, 0, },
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
	};
	
}
