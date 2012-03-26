/*
 * Copyright (c) 2011, Marc Röttig.
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

package org.ballproject.knime.base.parameter;

/**
 * The StringParameter class is used to store string values.
 * 
 * @author roettig
 * 
 */
public class StringParameter extends Parameter<String> {

	private static final long serialVersionUID = 2757963248340525354L;

	public StringParameter(String key, String value) {
		super(key, value);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void fillFromString(String s) {
		if (s == null) {
			value = null;
			return;
		}
		value = s;
	}

	@Override
	public boolean validate(String val) {
		return true;
	}

	@Override
	public String getMnemonic() {
		return "string";
	}
}
