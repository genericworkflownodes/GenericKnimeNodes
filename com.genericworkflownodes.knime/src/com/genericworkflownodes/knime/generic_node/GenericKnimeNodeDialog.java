/**
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

package com.genericworkflownodes.knime.generic_node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.generic_node.dialogs.mimetype_dialog.MimeTypeChooserDialog;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ParameterDialog;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Generic dialog for the tools.
 * 
 * @author aiche
 */
public class GenericKnimeNodeDialog extends NodeDialogPane {

    /**
     * The logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(GenericKnimeNodeDialog.class);

    /**
     * The node configuration.
     */
    private final INodeConfiguration config;

    /**
     * The dialog for the parameters.
     */
    private ParameterDialog dialog;

    /**
     * The dialog for choosing the MIMEType.
     */
    private MimeTypeChooserDialog mtc;

    /**
     * Creates a new GenericKnimeNodeDialog for the given node configuration.
     * 
     * @param configuration
     *            The node configuration that should be visualized.
     */
    public GenericKnimeNodeDialog(INodeConfiguration configuration) {
        config = configuration;
        dialog = new ParameterDialog(config);
        addTab("Parameters", dialog);
        mtc = new MimeTypeChooserDialog(config);
        addTab("OutputTypes", mtc);
    }

    @Override
    protected void saveSettingsTo(NodeSettingsWO settings) {
        // ensure all edit operations are finished
        dialog.stopEditing();
        // transfer values
        for (String key : config.getParameterKeys()) {
            Parameter<?> param = config.getParameter(key);

            // skip file parameters
            if (param instanceof IFileParameter) {
                continue;
            }

            settings.addString(key, param.getStringRep());
        }

        int[] selectedPorts = mtc.getSelectedTypes();

        for (int i = 0; i < config.getNumberOfOutputPorts(); i++) {
            settings.addInt(
                    GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUTTYPE_PREFIX
                            + i, selectedPorts[i]);
        }
    }

    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {
        for (String key : config.getParameterKeys()) {
            Parameter<?> param = config.getParameter(key);
            // skip file parameters
            if (param instanceof IFileParameter) {
                continue;
            }

            String value = null;
            try {
                value = settings.getString(key);
            } catch (InvalidSettingsException e) {
                LOGGER.warn("Invalid value detected.", e);
            }
            try {
                param.fillFromString(value);
            } catch (InvalidParameterValueException e) {
                throw new NotConfigurableException(e.getMessage(), e);
            }
        }

        int nP = config.getNumberOfOutputPorts();
        int[] selectedPorts = new int[nP];

        for (int i = 0; i < nP; i++) {
            try {
                int idx = settings
                        .getInt(GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUTTYPE_PREFIX
                                + i);
                selectedPorts[i] = idx;
            } catch (InvalidSettingsException e) {
                throw new NotConfigurableException(e.getMessage(), e);
            }
        }
        mtc.setSelectedTypes(selectedPorts);
    }
}
