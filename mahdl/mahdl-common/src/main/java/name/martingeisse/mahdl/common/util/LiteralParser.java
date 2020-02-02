/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.util;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class LiteralParser {

	private static final Pattern VECTOR_PATTERN = Pattern.compile("([0-9]+)([bodh])([0-9a-fA-F]+)");

	// prevent instantiation
	private LiteralParser() {
	}

	public static ConstantValue parseLiteral(@NotNull Expression_Literal literalExpression) throws ParseException {
		return parseLiteral(literalExpression.getLiteral());
	}

	public static ConstantValue parseLiteral(@NotNull Literal literal) throws ParseException {
		try {
			if (literal instanceof Literal_Vector) {
				return parseVector(((Literal_Vector) literal).getValue().getText());
			} else if (literal instanceof Literal_Integer) {
				String text = ((Literal_Integer) literal).getValue().getText();
				return new ConstantValue.Integer(new BigInteger(text));
			} else if (literal instanceof Literal_Text) {
				return parseText(((Literal_Text) literal).getValue());
			} else {
				throw new ParseException("unknown literal type");
			}
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
	}

	@NotNull
	public static ConstantValue.Vector parseVector(@NotNull String literalText) throws ParseException {
		Matcher matcher = VECTOR_PATTERN.matcher(literalText);
		if (!matcher.matches()) {
			throw new ParseException("malformed vector");
		}
		int size = Integer.parseInt(matcher.group(1));
		char radixCode = matcher.group(2).charAt(0);
		String digits = matcher.group(3);
		int radix = radixCode == 'b' ? 2 : radixCode == 'o' ? 8 : radixCode == 'd' ? 10 : radixCode == 'h' ? 16 : 0;
		if (radix == 0) {
			throw new ParseException("unknown radix '" + radixCode);
		}
		final BigInteger integerValue = new BigInteger(digits, radix);
		if (integerValue.bitLength() > size) {
			throw new ParseException("vector literal contains a value larger than its sepcified size");
		}
		try {
			return new ConstantValue.Vector(size, integerValue, false);
		} catch (ConstantValue.TruncateRequiredException e) {
			throw new ParseException("internal error while parsing vector constant: " + e);
		}
	}

	@NotNull
	private static ConstantValue parseText(@NotNull CmToken textElement) throws ParseException {
		String rawText = textElement.getText();
		if (rawText.charAt(0) != '"' || rawText.charAt(rawText.length() - 1) != '"') {
			throw new ParseException("missing quotation marks");
		}
		StringBuilder builder = new StringBuilder();
		boolean escape = false;
		for (int i = 1; i < rawText.length() - 1; i++) {
			if (escape) {
				i = i + parseEscapeSequence(rawText, i, builder) - 1;
			} else {
				char c = rawText.charAt(i);
				if (c == '\\') {
					escape = true;
				} else {
					builder.append(c);
				}
			}
		}
		if (escape) {
			throw new ParseException("unterminated escape sequence");
		}
		return new ConstantValue.Text(builder.toString());
	}

	private static int parseEscapeSequence(String source, int index, StringBuilder builder) throws ParseException {
		char c = source.charAt(index);
		switch (c) {

			case 'b':
				builder.append('\b');
				return 1;

			case 'n':
				builder.append('\n');
				return 1;

			case 't':
				builder.append('\t');
				return 1;

			case 'f':
				builder.append('\f');
				return 1;

			case 'r':
				builder.append('\r');
				return 1;

			case 'u':
				if (source.length() < index + 5) {
					throw new ParseException("interrupted unicode escape sequence");
				}
				try {
					builder.append((char) (Integer.parseInt(source.substring(index + 1, index + 5), 16)));
				} catch (NumberFormatException e) {
					throw new ParseException("malformed unicode escape sequence");
				}
				return 5;

			default:
				if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					throw new ParseException("invalid escape sequence: \\" + c);
				} else {
					builder.append(c);
					return 1;
				}
		}
	}

	public static final class ParseException extends Exception {
		public ParseException(String message) {
			super(message);
		}
	}

}
