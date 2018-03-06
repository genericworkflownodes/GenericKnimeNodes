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

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ParamCellEditor;
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
     * Logger instance.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ParameterVerifier.class);

    /**
     * The m_parameter that needs to be verified.
     */
    private final Parameter<?> m_parameter;

    public ParameterVerifier(Parameter<?> parameter) {
        m_parameter = parameter;
    }

    private boolean verifyDouble(String toVerify, Double lb, Double ub) {
        try {
            Double d = Double.valueOf(toVerify);
            return d != null && d <= ub && d >= lb;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean verifyInteger(String toVerify, Integer lb, Integer ub) {
        try {
            Integer i = Integer.valueOf(toVerify);
            return i != null && i <= ub && i >= lb;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
     */
    @Override
    public boolean verify(JComponent input) {
        boolean returnVal = false;
        if (input instanceof JTextField) {

            String inputValue = ((JTextField) input).getText();

            if (m_parameter instanceof DoubleParameter) {
                DoubleParameter dp = (DoubleParameter) m_parameter;
                returnVal = verifyDouble(inputValue, dp.getLowerBound(),
                        dp.getUpperBound());
            } else if (m_parameter instanceof DoubleListParameter) {
                DoubleListParameter dlp = (DoubleListParameter) m_parameter;
                returnVal = verifyDouble(inputValue, dlp.getLowerBound(),
                        dlp.getUpperBound());
            } else if (m_parameter instanceof IntegerParameter) {
                IntegerParameter ip = (IntegerParameter) m_parameter;
                returnVal = verifyInteger(inputValue, ip.getLowerBound(),
                        ip.getUpperBound());
            } else if (m_parameter instanceof IntegerListParameter) {
                IntegerListParameter ilp = (IntegerListParameter) m_parameter;
                returnVal = verifyInteger(inputValue, ilp.getLowerBound(),
                        ilp.getUpperBound());
            } else {
                returnVal = true;
            }
            
            if (!returnVal) 
            {  
              //TODO we currently just show a message box in the ParamCellEditor (that uses the verify function).
              //input.setBackground(Color.PINK);
              LOGGER.debug("Tried to set Parameter to an invalid value. Please see the restrictrions of the parameter.");
            }
        }
        return returnVal;
    }
};
