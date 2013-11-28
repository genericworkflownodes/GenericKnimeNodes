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
 * The IntegerParameter class is used to store int values.
 * 
 * @author roettig
 * 
 */
public class IntegerParameter extends NumberParameter<Integer> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -2665635647061983296L;

    /**
     * Constructor.
     * 
     * @param key
     *            The unique key of the parameter.
     * @param value
     *            The value of the parameter.
     */
    public IntegerParameter(final String key, final Integer value) {
        super(key, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param key
     *            The unique key of the parameter.
     * @param value
     *            The value of the parameter.
     */
    public IntegerParameter(final String key, final String value) {
        this(key, (value.equals("") ? null : Integer.parseInt(value)));
    }

    @Override
    public String toString() {
        if (getValue() == null) {
            return "";
        }
        return String.format("%d", getValue());
    }

    @Override
    public void fillFromString(final String s)
            throws InvalidParameterValueException {
        if (s == null || s.equals("")) {
            setValue(null);
            return;
        }
        try {
            setValue(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new InvalidParameterValueException("parameter "
                    + this.getKey() + " value is not an integer", e);
        }
        if (getValue() < this.getLowerBound()
                || getValue() > this.getUpperBound()) {
            throw new InvalidParameterValueException("parameter "
                    + this.getKey() + " value is out of bounds");
        }
    }

    @Override
    public boolean validate(final Integer val) {
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
        String lb = (getLowerBound() == Integer.MIN_VALUE ? "-inf" : String
                .format("%d", getLowerBound()));
        String ub = (getUpperBound() == Integer.MAX_VALUE ? "+inf" : String
                .format("%d", getUpperBound()));
        return String.format("integer [%s:%s]", lb, ub);
    }
}
