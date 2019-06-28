/**
 * Copyright (c) 2012, Marc Röttig
 * Copyright (c) 2019, Julianus Pfeuffer.
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
 * The BoolParameter class is used to store boolean values.
 * 
 * @author roettig, jpfeuffer
 * 
 */
public class BoolParameter extends Parameter<Boolean> {
	
	/**
	 * A boolean parameter can be a flag (bool type in ParamXML) or
	 * a String parameter with restrictions true/false (in case a
	 * default value of true is desired on the command line
	 * Nonetheless in views and in the code and in the KNIME settings.xml
	 * we treat both as booleans to be backwards-compatible.
	 * Only when writing we want to correctly
	 * preserve the original type.
	 */
	public boolean isFlag = true;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 7880934193463457962L;

    /**
     * Constructor.
     * 
     * @param key
     *            The unique key of the parameter.
     * @param value
     *            The value of the parameter.
     */
    public BoolParameter(final String key, final Boolean value) {
        super(key, value);
    }

    /**
     * Constructor.
     * 
     * @param key
     *            The unique key of the parameter.
     * @param value
     *            The value of the parameter as {@link String}.
     */
    public BoolParameter(final String key, final String value) {
        super(key, Boolean.valueOf(value.toLowerCase()));
    }
    
    /**
     * Constructor.
     * 
     * @param key
     *            The unique key of the parameter.
     * @param value
     *            The value of the parameter as {@link String}.
     */
    public BoolParameter(final String key, final String value, final boolean isFlag) {
        super(key, Boolean.valueOf(value.toLowerCase()));
        this.isFlag = isFlag;
    }

    @Override
    public String toString() {
        if (getValue() == null) {
            return null;
        }
        return (getValue() ? "true" : "false");
    }

    @Override
    public void fillFromString(final String s)
            throws InvalidParameterValueException {
        if (s == null || s.equals("")) {
            setValue(null);
            return;
        }
        if (!(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))) {
            throw new InvalidParameterValueException("");
        }
        setValue(Boolean.parseBoolean(s));
    }

    @Override
    public boolean validate(final Boolean val) {
        return true;
    }

    @Override
    public String getMnemonic() {
        return "bool";
    }

    @Override
    public String getStringRep() {
        return toString();
    }
}
