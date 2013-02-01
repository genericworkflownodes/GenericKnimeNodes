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
 * An abstract numerical parameter providing boundary checking for the derived
 * parameters.
 * 
 * @author roettig
 * 
 * @param <T>
 *            The numerical parameter type.
 */
public abstract class NumberParameter<T extends Number> extends Parameter<T> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 3529659248042850739L;

	/**
	 * The lower bound of the given numerical parameter.
	 */
	private T lowerBound;

	/**
	 * The upper bound of the given numerical parameter.
	 */
	private T upperBound;

	/**
	 * Constructor.
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
	public NumberParameter(final String key, final T value, final T lowerBound,
			final T upperBound) {
		super(key, value);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * returns the lower bound for constrained numeric parameters.
	 * 
	 * @return lower bound
	 */
	public T getLowerBound() {
		return lowerBound;
	}

	/**
	 * sets the lower bound for constrained numeric parameters.
	 * 
	 * @param lowerBound
	 *            The new lower bound for the given numerical parameter.
	 */
	public void setLowerBound(final T lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * returns the upper bound for constrained numeric parameters.
	 * 
	 * @return upper bound
	 */
	public T getUpperBound() {
		return upperBound;
	}

	/**
	 * sets the upper bound for constrained numeric parameters.
	 * 
	 * @param upperBound
	 *            The new upper bound for the given numerical parameter.
	 */
	public void setUpperBound(final T upperBound) {
		this.upperBound = upperBound;
	}
}
