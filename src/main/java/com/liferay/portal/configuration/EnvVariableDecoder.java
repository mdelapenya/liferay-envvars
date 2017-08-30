/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
 
package com.liferay.portal.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manuel de la Peña
 */
public class EnvVariableDecoder {

    public static String decode(String s) {
		int index = -1;
		int openUnderLine = -1;
		int position = 0;
		StringBuilder sb = new StringBuilder();

		while ((index = s.indexOf(CharPool.UNDERLINE, index + 1)) != -1) {
			if (openUnderLine == -1) {
				sb.append(s.substring(position, index));

				openUnderLine = index;
				position = index;

				continue;
			}

			String encoded = s.substring(openUnderLine + 1, index);

			Character character = _charPoolChars.get(encoded.toUpperCase());

			if (character == null) {
				int value = _get(encoded, -1);

				if (Character.isDefined(value)) {
					sb.append(new String(Character.toChars(value)));
				}
				else {
                    System.out.println(
                        "Unable to decode part \"" + encoded +
                            "\" from \"" + s + "\", preserve it literally");

					sb.append(s.substring(openUnderLine, index + 1));
				}
			}
			else {
				sb.append(character);
			}

			openUnderLine = -1;
			position = index + 1;
		}

		sb.append(s.substring(position, s.length()));

		return sb.toString();
	}

	/**
	 * Returns the String value as an integer. If the value is <code>null</code>
	 * or not convertible to an integer, the default value is returned.
	 *
	 * @param  value the value to convert
	 * @param  defaultValue a default value
	 * @return the value as an integer
	 */
	private static int _get(String value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		return _parseInt(value.trim(), defaultValue);
	}

	private static int _parseInt(String value, int defaultValue) {
		int length = value.length();

		if (length <= 0) {
			return defaultValue;
		}

		int pos = 0;
		int limit = -Integer.MAX_VALUE;
		boolean negative = false;

		char c = value.charAt(0);

		if (c < CharPool.NUMBER_0) {
			if (c == CharPool.MINUS) {
				limit = Integer.MIN_VALUE;
				negative = true;
			}
			else if (c != CharPool.PLUS) {
				return defaultValue;
			}

			if (length == 1) {
				return defaultValue;
			}

			pos++;
		}

		int smallLimit = limit / 10;

		int result = 0;

		while (pos < length) {
			if (result < smallLimit) {
				return defaultValue;
			}

			c = value.charAt(pos++);

			if ((c < CharPool.NUMBER_0) || (c > CharPool.NUMBER_9)) {
				return defaultValue;
			}

			int number = c - CharPool.NUMBER_0;

			result *= 10;

			if (result < (limit + number)) {
				return defaultValue;
			}

			result -= number;
		}

		if (negative) {
			return result;
		}
		else {
			return -result;
		}
	}

	private static String _removeChar(String s, char oldSub) {
		if (s == null) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {
			StringBuilder sb = new StringBuilder();

			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));

				x = y + 1;

				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}
		else {
			return s;
		}
	}

	private static final Map<String, Character> _charPoolChars =
        new HashMap<>();

	static {
		try {
			for (Field field : CharPool.class.getFields()) {
				if (Modifier.isStatic(field.getModifiers()) &&
					(field.getType() == char.class)) {

					_charPoolChars.put(
						_removeChar(
							field.getName(), CharPool.UNDERLINE),
							field.getChar(null));
				}
			}
		}
		catch (ReflectiveOperationException roe) {
			throw new ExceptionInInitializerError(roe);
		}
	}

}