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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The StringListParameter class is used to store lists of string values.
 * 
 * @author roettig
 * 
 */
public class StringListParameter extends Parameter<List<String>> implements
		ListParameter {

	/**
	 * Set of allowed entries for this StringListParameter
	 */
	private List<String> validValues;

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -3843594608327851669L;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public StringListParameter(final String key, final List<String> value) {
		super(key, value);
		validValues = new ArrayList<String>();
	}

	public void setRestrictions(List<String> newRestrictions) {
		validValues = newRestrictions;
	}

	public void addRestrictions(String allowedValue) {
		if (!validValues.contains(allowedValue))
			validValues.add(allowedValue);
	}

	public String[] getRestrictions() {
		return (String[]) validValues.toArray();
	}

	@Override
	public String getMnemonic() {
		return "string list";
	}

	@Override
	public void fillFromString(final String s)
			throws InvalidParameterValueException {
		if (s == null || s.equals("")) {
			setValue(new ArrayList<String>());
			return;
		}
		setValue(new ArrayList<String>());
		String[] toks = s.split(SEPARATOR_TOKEN);
		for (int i = 0; i < toks.length; i++) {
			getValue().add(toks[i]);
		}
	}

	@Override
	public String getStringRep() {
		if (getValue() == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (String s : getValue()) {
			sb.append(s + SEPARATOR_TOKEN);
		}
		return sb.toString();
	}

	@Override
	public boolean validate(final List<String> values) {
		if (validValues.isEmpty())
			return true;
		else {
			return validValues.containsAll(values);
		}
	}

	@Override
	public String toString() {
		if (getValue() == null) {
			return "[]";
		}
		String[] values = getValue().toArray(new String[0]);
		return Arrays.toString(values);
	}

	@Override
	public List<String> getStrings() {
		List<String> ret = new ArrayList<String>();
		for (String s : getValue()) {
			ret.add(s);
		}
		return ret;
	}

	@Override
	public void fillFromStrings(final String[] values)
			throws InvalidParameterValueException {
		setValue(new ArrayList<String>());
		if (validate(Arrays.asList(values))) {
			for (int i = 0; i < values.length; i++) {
				getValue().add(values[i]);
			}
		} else {
			throw new InvalidParameterValueException(
					"Some or all of the given values are not contained in the set of valid values.");
		}
	}
}
