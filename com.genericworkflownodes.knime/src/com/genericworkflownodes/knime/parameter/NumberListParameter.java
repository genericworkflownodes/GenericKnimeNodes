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

import java.util.List;

/**
 * The abstract NumberListParameter class is used to store lists of numeric
 * values.
 * 
 * @param <T>
 *            The numerical type stored in the list.
 * 
 * @author roettig
 * 
 */
public abstract class NumberListParameter<T extends Number> extends
		Parameter<List<T>> implements ListParameter {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4722657913698964700L;

	/**
	 * The lower bound of the given numerical parameter.
	 */
	private T lowerBound;

	/**
	 * The upper bound of the given numerical parameter.
	 */
	private T upperBound;

	/**
	 * 
	 * @param key
	 *            The unique key of the parameter.
	 * @param value
	 *            The value of the parameter.
	 * @param lowerBound
	 *            The lower bound of the contained values.
	 * @param upperBound
	 *            The upper bound of the contained values.
	 */
	public NumberListParameter(final String key, final List<T> value,
			final T lowerBound, final T upperBound) {
		super(key, value);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * Returns the lower bound for constrained numeric parameters.
	 * 
	 * @return lower bound
	 */
	public T getLowerBound() {
		return lowerBound;
	}

	/**
	 * Sets the lower bound for constrained numeric parameters.
	 * 
	 * @param lowerBound
	 *            The new lower bound for the given numerical parameter.
	 */
	public void setLowerBound(final T lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Returns the upper bound for constrained numeric parameters.
	 * 
	 * @return upper bound
	 */
	public T getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets the upper bound for constrained numeric parameters.
	 * 
	 * @param upperBound
	 *            The new upper bound for the given numerical parameter.
	 */
	public void setUpperBound(final T upperBound) {
		this.upperBound = upperBound;
	}
}
