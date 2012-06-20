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

/**
 * The DoubleParameter class is used to store double values.
 * 
 * @author roettig
 * 
 */
public class DoubleParameter extends NumberParameter<Double> {

	private static final long serialVersionUID = -8428868568959196082L;

	public DoubleParameter(String key, Double value) {
		super(key, value);
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}

	public DoubleParameter(String key, String value) {
		super(key, (value.equals("") ? null : Double.parseDouble(value)));
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}

	@Override
	public String toString() {
		if (value == null) {
			return null;
		}
		return String.format("%e", value);
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException {
		if (s == null || s.equals("")) {
			value = null;
			return;
		}
		try {
			value = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new InvalidParameterValueException("parameter "
					+ this.getKey() + " value is not a double", e);
		}
		if (value < this.getLowerBound() || value > this.getUpperBound()) {
			throw new InvalidParameterValueException("parameter "
					+ this.getKey() + " value is out of bounds");
		}
	}

	@Override
	public boolean validate(Double val) {
		if (isNull()) {
			return true;
		}
		if (val >= this.lowerBound && val <= this.upperBound) {
			return true;
		}
		return false;
	}

	@Override
	public String getMnemonic() {
		String lb = (this.lowerBound == Double.NEGATIVE_INFINITY ? "-inf"
				: String.format("%e", this.lowerBound));
		String ub = (this.upperBound == Double.POSITIVE_INFINITY ? "+inf"
				: String.format("%e", this.upperBound));
		return String.format("double [%s:%s]", lb, ub);
	}
}
