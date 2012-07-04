/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Collection of utility methods and classes for Strings.
 * 
 * @author aiche et al.
 * 
 */
public final class StringUtils {

	/**
	 * C'tor. Prohibit initialization since it is a utility class.
	 */
	private StringUtils() {
	}

	/**
	 * Joins all elements of the {@link Collection} into a single string,
	 * separating them by the passed delimiter.
	 * 
	 * @param collection
	 *            The collection to join.
	 * @param delimiter
	 *            The used delimiter.
	 * @return A string containing all elements of the collection separated by
	 *         the delimiter.
	 */
	public static String join(final Collection<?> collection,
			final String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = collection.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	/**
	 * Abstract class to extract ranges from strings of the form "0:10".
	 * 
	 * @author aiche
	 * 
	 * @param <T>
	 *            The type of Number to extract.
	 */
	private abstract static class RangeExtractor<T extends Number> {

		/**
		 * This method returns the default upper bound for the given number type
		 * T.
		 * 
		 * @return The default upper bound.
		 */
		protected abstract T getUpperBoundDefault();

		/**
		 * This method returns the default lower bound for the given number type
		 * T.
		 * 
		 * @return The default lower bound.
		 */
		protected abstract T getLowerBoundDefault();

		/**
		 * Tries to convert the given string into the specified number type T.
		 * 
		 * @param str
		 *            The string to convert.
		 * @return A number corresponding to the passed string.
		 */
		protected abstract T parseString(String str);

		/**
		 * Extracts the given lower bound from the string.
		 * 
		 * @param str
		 *            The string from which the lower bound should be extracted.
		 * @return The lower bound contained in the string or the default lower
		 *         bound.
		 */
		public T getLowerBound(final String str) {

			String[] toks = str.split(":");

			if (toks.length > 0) {
				if (!"".equals(toks[0])) {
					return parseString(toks[0]);
				}
			}

			return getLowerBoundDefault();
		}

		/**
		 * Extracts the given upper bound from the string.
		 * 
		 * @param str
		 *            The string from which the upper bound should be extracted.
		 * @return The upper bound contained in the string or the default upper
		 *         bound.
		 */
		public T getUpperBound(final String str) {
			String[] toks = str.split(":");

			if (toks.length > 1) {
				if (!"".equals(toks[1])) {
					return parseString(toks[1]);
				}
			}

			return getUpperBoundDefault();
		}
	}

	/**
	 * {@link RangeExtractor} for doubles.
	 * 
	 * @author aiche
	 */
	public static class DoubleRangeExtracted extends RangeExtractor<Double> {

		@Override
		protected Double getUpperBoundDefault() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		protected Double getLowerBoundDefault() {
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		protected Double parseString(final String str) {
			return Double.parseDouble(str);
		}

	}

	/**
	 * {@link RangeExtractor} for integers.
	 * 
	 * @author aiche
	 */
	public static class IntegerRangeExtractor extends RangeExtractor<Integer> {

		@Override
		protected Integer getUpperBoundDefault() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected Integer getLowerBoundDefault() {
			return Integer.MIN_VALUE;
		}

		@Override
		protected Integer parseString(final String str) {
			return Integer.parseInt(str);
		}

	}
}
