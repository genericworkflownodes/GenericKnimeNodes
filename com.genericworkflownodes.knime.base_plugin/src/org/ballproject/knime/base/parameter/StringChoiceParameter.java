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

import java.util.Arrays;
import java.util.List;

/**
 * The StringChoiceParameter class reprents string values taken from a
 * restricted set.
 * 
 * @author roettig
 * 
 */
public class StringChoiceParameter extends Parameter<String> {
	private static final long serialVersionUID = -2717347970783695908L;
	private List<String> values;
	private List<String> labels;

	public StringChoiceParameter(String key, String value) {
		super(key, value);
	}

	public StringChoiceParameter(String key, List<String> values) {
		super(key, values.get(0));
		this.values = values;
		this.labels = values;
	}

	public StringChoiceParameter(String key, String[] values) {
		super(key, values[0]);
		this.values = Arrays.asList(values);
		this.labels = Arrays.asList(values);
	}

	public StringChoiceParameter(String key, List<String> values,
			List<String> labels) {
		super(key, values.get(0));
		this.values = values;
		this.labels = labels;
	}

	public StringChoiceParameter(String key, String[] values, String[] labels) {
		super(key, values[0]);
		this.values = Arrays.asList(values);
		this.labels = Arrays.asList(labels);
	}

	@Override
	public void setValue(String value) {
		if (values.contains(value)) {
			super.setValue(value);
		}
	}

	/**
	 * returns the list of allowed string values
	 * 
	 * @return allowed values
	 */
	public List<String> getAllowedValues() {
		return values;
	}

	/**
	 * returns the list of associated labels for each value.
	 * 
	 * This is mainly for display purposes within GUIs.
	 * 
	 * @return list of labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException {
		if (s == null) {
			value = null;
			return;
		}
		if (!this.getAllowedValues().contains(s)) {
			throw new InvalidParameterValueException("parameter "
					+ this.getKey() + " value is invalid");
		}
		value = s;

	}

	@Override
	public boolean validate(String val) {
		return true;
	}

	@Override
	public String getMnemonic() {
		return "string choice";
	}
}
