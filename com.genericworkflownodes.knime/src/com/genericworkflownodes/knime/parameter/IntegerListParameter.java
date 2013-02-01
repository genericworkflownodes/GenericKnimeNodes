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
 * The IntegerListParameter class is used to store lists of int values.
 * 
 * @author roettig
 * 
 */
public class IntegerListParameter extends NumberListParameter<Integer> {

	/**
	 * The serial version uid.
	 */
	private static final long serialVersionUID = 3136376166293660419L;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public IntegerListParameter(final String key, final List<Integer> value) {
		super(key, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public String getMnemonic() {
		String lb = (getLowerBound() == Integer.MIN_VALUE ? "-inf" : String
				.format("%d", getLowerBound()));
		String ub = (getUpperBound() == Integer.MAX_VALUE ? "+inf" : String
				.format("%d", getUpperBound()));
		return String.format("integer list [%s:%s]", lb, ub);
	}

	@Override
	public void fillFromString(final String s)
			throws InvalidParameterValueException {
		try {
			if (s == null || s.equals("")) {
				setValue(new ArrayList<Integer>());
				return;
			}
			setValue(new ArrayList<Integer>());
			String[] toks = s.split(SEPARATOR_TOKEN);

			for (int i = 0; i < toks.length; i++) {
				this.getValue().add(Integer.parseInt(toks[i]));
			}
		} catch (NumberFormatException e) {
			throw new InvalidParameterValueException(
					"The given string cannot be transformed into a integer list.");
		}
	}

	@Override
	public boolean validate(final List<Integer> val) {
		if (isNull()) {
			return true;
		}
		boolean ok = true;

		for (Integer v : val) {
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
		for (Integer d : this.getValue()) {
			sb.append(String.format("%d", d) + SEPARATOR_TOKEN);
		}
		return sb.toString();
	}

	@Override
	public List<String> getStrings() {
		List<String> ret = new ArrayList<String>();
		for (Integer i : this.getValue()) {
			ret.add(i.toString());
		}
		return ret;
	}

	@Override
	public String toString() {
		if (getValue() == null) {
			return "";
		}
		String[] ret = new String[this.getValue().size()];
		int idx = 0;
		for (Integer i : getValue()) {
			ret[idx++] = i.toString();
		}
		return Arrays.toString(ret);
	}

	@Override
	public void fillFromStrings(final String[] values) {
		this.setValue(new ArrayList<Integer>());
		for (int i = 0; i < values.length; i++) {
			this.getValue().add(Integer.parseInt(values[i]));
		}
	}
}
