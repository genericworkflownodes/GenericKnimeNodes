/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.verifier;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Component verifier to ensure that the values entered in the param dialogs are
 * valid.
 * 
 * @author aiche
 */
public class ParameterVerifier extends InputVerifier {

	/**
	 * The parameter that needs to be verified.
	 */
	private final Parameter<?> parameter;

	public ParameterVerifier(Parameter<?> parameter) {
		this.parameter = parameter;
	}

	private boolean verifyDouble(String toVerify, Double lb, Double ub) {
		Double d = new Double(toVerify);
		return d <= ub && d >= lb;
	}

	private boolean verifyInteger(String toVerify, Integer lb, Integer ub) {
		Integer i = new Integer(toVerify);
		return i <= ub && i >= lb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
	 */
	@Override
	public boolean verify(JComponent input) {
		if (input instanceof JTextField) {

			String inputValue = ((JTextField) input).getText();

			if (parameter instanceof DoubleParameter) {
				DoubleParameter dp = (DoubleParameter) parameter;
				return verifyDouble(inputValue, dp.getLowerBound(),
						dp.getUpperBound());
			} else if (parameter instanceof DoubleListParameter) {
				DoubleListParameter dlp = (DoubleListParameter) parameter;
				return verifyDouble(inputValue, dlp.getLowerBound(),
						dlp.getUpperBound());
			} else if (parameter instanceof IntegerParameter) {
				IntegerParameter ip = (IntegerParameter) parameter;
				return verifyInteger(inputValue, ip.getLowerBound(),
						ip.getUpperBound());
			} else if (parameter instanceof IntegerListParameter) {
				IntegerListParameter ilp = (IntegerListParameter) parameter;
				return verifyInteger(inputValue, ilp.getLowerBound(),
						ilp.getUpperBound());
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

}
