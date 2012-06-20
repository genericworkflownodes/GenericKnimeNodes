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
public class IntegerListParameter extends NumberListParameter<Integer>
		implements ListParameter {
	private static final long serialVersionUID = 3136376166293660419L;

	public IntegerListParameter(String key, List<Integer> value) {
		super(key, value);
	}

	@Override
	public String getMnemonic() {
		String lb = (this.lowerBound == Integer.MIN_VALUE ? "-inf" : String
				.format("%d", this.lowerBound));
		String ub = (this.upperBound == Integer.MAX_VALUE ? "+inf" : String
				.format("%d", this.upperBound));
		return String.format("integer list [%s:%s]", lb, ub);
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException {
		if (s == null || s.equals("")) {
			value = new ArrayList<Integer>();
			return;
		}
		this.value = new ArrayList<Integer>();
		String[] toks = s.split(SEPERATORTOKEN);

		for (int i = 0; i < toks.length; i++) {
			this.value.add(Integer.parseInt(toks[i]));
		}
	}

	@Override
	public boolean validate(List<Integer> val) {
		if (isNull()) {
			return true;
		}
		boolean ok = true;

		for (Integer v : val) {
			if (v < this.lowerBound || v > this.upperBound) {
				ok = false;
			}
		}
		return ok;
	}

	@Override
	public String getStringRep() {
		if (value == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Integer d : this.value) {
			sb.append(String.format("%d", d) + SEPERATORTOKEN);
		}
		return sb.toString();
	}

	@Override
	public List<String> getStrings() {
		List<String> ret = new ArrayList<String>();
		for (Integer i : this.value) {
			ret.add(i.toString());
		}
		return ret;
	}

	public String toString() {
		if (value == null) {
			return "";
		}
		String[] ret = new String[this.value.size()];
		int idx = 0;
		for (Integer i : value) {
			ret[idx++] = i.toString();
		}
		return Arrays.toString(ret);
	}

	@Override
	public void fillFromStrings(String[] values) {
		this.value = new ArrayList<Integer>();
		for (int i = 0; i < values.length; i++) {
			this.value.add(Integer.parseInt(values[i]));
		}
	}
}
