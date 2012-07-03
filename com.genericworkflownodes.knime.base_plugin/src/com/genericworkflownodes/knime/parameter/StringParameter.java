/**
 * Copyright (c) 2012, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.parameter;

/**
 * The StringParameter class is used to store string values.
 * 
 * @author roettig
 * 
 */
public class StringParameter extends Parameter<String> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 2757963248340525354L;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public StringParameter(final String key, final String value) {
		super(key, value);
	}

	@Override
	public String toString() {
		return getValue();
	}

	@Override
	public void fillFromString(final String s) {
		if (s == null) {
			setValue(null);
			return;
		}
		setValue(s);
	}

	@Override
	public boolean validate(final String val) {
		return true;
	}

	@Override
	public String getMnemonic() {
		return "string";
	}
}
