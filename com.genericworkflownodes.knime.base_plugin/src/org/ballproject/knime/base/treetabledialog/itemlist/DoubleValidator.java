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

package org.ballproject.knime.base.treetabledialog.itemlist;

/**
 * The DoubleValidator class checks whether supplied string values are valid
 * doubles.
 * 
 * @author roettig
 * 
 */
public class DoubleValidator implements Validator {
	private Double UB = Double.POSITIVE_INFINITY;
	private Double LB = Double.NEGATIVE_INFINITY;
	private String reason = "N/A";

	@Override
	public boolean validate(final String s) {
		Double d = null;
		try {
			d = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			reason = "invalid number format";
			return false;
		}

		if (d > UB) {
			reason = "higher than upper bound";
			return false;
		}
		if (d < LB) {
			reason = "lower than lower bound";
			return false;
		}

		return true;
	}

	@Override
	public String getName() {
		return "double";
	}

	/**
	 * set the lower bound for any range validation.
	 * 
	 * @param d
	 *            lower bound
	 */
	public void setLowerBound(final Double d) {
		LB = d;
	}

	/**
	 * sets the upper bound for any range validation.
	 * 
	 * @param d
	 *            upper bound
	 */
	public void setUpperBound(final Double d) {
		UB = d;
	}

	@Override
	public String getReason() {
		return reason;
	}
}
