/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j.model.optiontypes;

class EnumValueParser<T extends Enum<T>> implements OptionValueParser<T> {

	private final Class<? extends Enum> enumType;

	public EnumValueParser(final Class<? extends T> enumType) {
		this.enumType = enumType;
	}

	@Override
	public T parse(final String value) {
		try {
			return (T) Enum.valueOf(enumType, value.toUpperCase());
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}
	
	@Override
	public void validate(final String name, final String value) throws OptionException {
		try {
			Enum.valueOf(enumType, value.toUpperCase());
		} catch (final IllegalArgumentException e) {
			throw new OptionException("enum");
		}
	}

}
