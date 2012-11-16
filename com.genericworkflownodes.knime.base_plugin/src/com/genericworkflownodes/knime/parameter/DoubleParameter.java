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
 * The DoubleParameter class is used to store double values.
 * 
 * @author roettig
 * 
 */
public class DoubleParameter extends NumberParameter<Double> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -8428868568959196082L;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public DoubleParameter(final String key, final Double value) {
		super(key, value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	public DoubleParameter(final String key, final String value) {
		this(key, (value.equals("") ? null : Double.parseDouble(value)));
	}

	@Override
	public String toString() {
		if (getValue() == null) {
			return null;
		}
		return String.format("%s", getValue());
	}

	@Override
	public void fillFromString(final String s)
			throws InvalidParameterValueException {
		if (s == null || s.equals("")) {
			setValue(null);
			return;
		}
		try {
			setValue(Double.parseDouble(s));
		} catch (NumberFormatException e) {
			throw new InvalidParameterValueException("parameter " + getKey()
					+ " value is not a double", e);
		}
		if (getValue() < getLowerBound() || getValue() > getUpperBound()) {
			throw new InvalidParameterValueException("parameter " + getKey()
					+ " value is out of bounds");
		}
	}

	@Override
	public boolean validate(final Double val) {
		if (isNull()) {
			return true;
		}
		if (val >= getLowerBound() && val <= getUpperBound()) {
			return true;
		}
		return false;
	}

	@Override
	public String getMnemonic() {
		String lb = (getLowerBound() == Double.NEGATIVE_INFINITY ? "-inf"
				: String.format("%s", getLowerBound()));
		String ub = (getUpperBound() == Double.POSITIVE_INFINITY ? "+inf"
				: String.format("%s", getUpperBound()));
		return String.format("double [%s:%s]", lb, ub);
	}
}
