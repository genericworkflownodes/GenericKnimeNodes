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
 * The DoubleListParameter class is used to store lists of double values.
 * 
 * @author roettig
 * 
 */
public class DoubleListParameter extends NumberListParameter<Double> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -5162432579726548479L;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public DoubleListParameter(final String key, final List<Double> value) {
		super(key, value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	@Override
	public String getMnemonic() {
		String lb = (getLowerBound() == Double.NEGATIVE_INFINITY ? "-inf"
				: String.format("%s", getLowerBound()));
		String ub = (getUpperBound() == Double.POSITIVE_INFINITY ? "+inf"
				: String.format("%s", getUpperBound()));
		return String.format("double list [%s:%s]", lb, ub);
	}

	@Override
	public void fillFromString(final String s)
			throws InvalidParameterValueException {
		try {
			if (s == null || s.equals("")) {
				setValue(new ArrayList<Double>());
				return;
			}
			setValue(new ArrayList<Double>());
			String[] toks = s.split(SEPARATOR_TOKEN);

			for (int i = 0; i < toks.length; i++) {
				getValue().add(Double.parseDouble(toks[i]));
			}
		} catch (NumberFormatException e) {
			throw new InvalidParameterValueException(
					"The given string cannot be transformed into a double list.");
		}
	}

	@Override
	public boolean validate(final List<Double> val) {
		if (isNull()) {
			return true;
		}

		boolean ok = true;

		for (Double v : val) {
			if (v < getLowerBound() || v > getUpperBound()) {
				ok = false;
			}
		}
		return ok;
	}

	@Override
	public String getStringRep() {
		if (getValue() == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Double d : getValue()) {
			sb.append(String.format("%f", d) + SEPARATOR_TOKEN);
		}
		return sb.toString();
	}

	@Override
	public List<String> getStrings() {
		List<String> ret = new ArrayList<String>();
		for (Double d : getValue()) {
			ret.add(String.format("%f", d));
		}
		return ret;
	}

	@Override
	public void fillFromStrings(final String[] values) {
		setValue(new ArrayList<Double>());
		for (int i = 0; i < values.length; i++) {
			getValue().add(Double.parseDouble(values[i]));
		}
	}

	@Override
	public String toString() {
		if (getValue() == null) {
			return "[]";
		}
		String[] ret = new String[getValue().size()];
		int idx = 0;
		for (Double i : getValue()) {
			ret[idx++] = i.toString();
		}
		return Arrays.toString(ret);
	}
}
