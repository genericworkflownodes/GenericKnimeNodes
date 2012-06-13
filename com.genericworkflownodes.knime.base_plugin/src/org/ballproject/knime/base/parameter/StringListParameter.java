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
	private static final long serialVersionUID = -3843594608327851669L;

	public StringListParameter(String key, List<String> value) {
		super(key, value);
	}

	@Override
	public String getMnemonic() {
		return "string list";
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException {
		if (s == null || s.equals("")) {
			value = new ArrayList<String>();
			return;
		}
		this.value = new ArrayList<String>();
		String[] toks = s.split(SEPERATORTOKEN);
		for (int i = 0; i < toks.length; i++) {
			this.value.add(toks[i]);
		}
	}

	@Override
	public String getStringRep() {
		if (value == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (String s : this.value) {
			sb.append(s + SEPERATORTOKEN);
		}
		return sb.toString();
	}

	@Override
	public boolean validate(List<String> val) {
		return true;
	}

	@Override
	public String toString() {
		if (value == null) {
			return "[]";
		}
		String[] values = value.toArray(new String[0]);
		return Arrays.toString(values);
	}

	@Override
	public List<String> getStrings() {
		List<String> ret = new ArrayList<String>();
		for (String s : this.value) {
			ret.add(s);
		}
		return ret;
	}

	@Override
	public void fillFromStrings(String[] values) {
		this.value = new ArrayList<String>();
		for (int i = 0; i < values.length; i++) {
			this.value.add(values[i]);
		}
	}
}
