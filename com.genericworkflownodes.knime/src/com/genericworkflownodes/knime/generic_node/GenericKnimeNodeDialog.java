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
import com.genericworkflownodes.knime.port.Port;

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
        boolean[] activePorts = mtc.getActiveness();
        int[] linkedInputPorts = mtc.getLinkedInputPorts();
        String[] customBasenames = mtc.getCustomBasenames();

        for (int i = 0; i < config.getNumberOfOutputPorts(); i++) {
            settings.addBoolean(
                    GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_ACTIVE
                            + i, activePorts[i]);
            
            //TODO Remove that dirty hack for backwards compatibility ASAP
            // in new version. Resets the selected mimetype index to an
            // invalid one when it gets deactivated. -> User has to rechoose
            // when he reactivates! But this ensures that inactive ports
            // are correctly read by older GKN versions
            if (!activePorts[i])
            {
                settings.addInt(
                        GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_TYPE
                                + i, config.getOutputPorts().get(i).getMimeTypes().size());
            } else {
                settings.addInt(
                        GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_TYPE
                                + i, selectedPorts[i]);
            }


            settings.addInt(
                    GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_LINKEDINPUT
                            + i, linkedInputPorts[i]);
            settings.addString(
                    GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_CUSTOMBASENAME
                            + i, customBasenames[i]);
        }
    }

    // This is called before the dialog opens but after all the constructors of the dialogues and tabs in there
    // were called
    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {
        
        String errorsFound = "";
        for (String key : config.getParameterKeys()) {
            Parameter<?> param = config.getParameter(key);
            // skip file parameters
            if (param instanceof IFileParameter) {
                continue;
            }
            
            String value = null;
            try {
                value = settings.getString(key);
                param.fillFromString(value);
            } catch (InvalidSettingsException e) {
                errorsFound += "- Entry for " + key + " not found in settings.xml.\n";
            } catch (InvalidParameterValueException e) {
                errorsFound += "- Entry for " + key + " in settings.xml has a value that does not match its restrictions.\n";
                //Do not hard fail. Users should be able to edit the value in the dialog.
                /*throw new NotConfigurableException(e.getMessage(), e);*/
            }
        }
        
        if (!errorsFound.isEmpty())
        {
           LOGGER.error("Errors found loading Settings from disk. Maybe you are loading a node created with another version."
                   + "\nUsing default values as defined in the Tool description."
                   + "\nWe recommend to check the parameters highlighted in red carefully before clicking OK: \n"
                   + errorsFound);
        }

        int nP = config.getNumberOfOutputPorts();
        int[] selectedPorts = new int[nP];
        boolean[] activePorts = new boolean[nP];
        int[] linkedInputPorts = new int[nP];
        String[] customBasenames = new String[nP];

        for (int i = 0; i < nP; i++) {
            Port p = config.getOutputPorts().get(i);
            int idx = 0; // default mimetype is the first
            try{
                idx = settings.getInt(GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_TYPE + i);
                selectedPorts[i] = idx;
            } catch (InvalidSettingsException e) {
                //TODO Warning that we fell back to defaults?
            }

            boolean idxOOR = (idx < 0 || idx >= p.getMimeTypes().size());
            if (idxOOR && !p.isOptional()) // invalid required port setting read
            {
                idx = 0;
                //TODO Best would be to also deactivate this port to show the user that they have to reconfigure.
                // But currently I disabled the editing of the activeness checkbox for required ports, so it would get
                // initialized with an unchecked checkbox and you could not reactivate.
                // You could do a function that, when the OutputTypes tab is clicked, that it resets to active
                // for all required ports, because we assume the user has seen/thought about the new settings.
                // But then, anyways a loaded unchanged workflow would fail due to inactiveness of the port, so no general best solution
                // for it.
                // Maybe provide better defaults (e.g. by looking at a mapping from old to new versions, if available). But
                // that is a lot of work and does not work if the invalidness didnt come from a version change.
                LOGGER.warn("Invalid mime-type index in settings.xml for required port #" + i + ". Using default (first).");
            }
            
            try{
                // A found activeness setting always takes precedence
                boolean active = settings
                        .getBoolean(GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_ACTIVE
                                + i);
                activePorts[i] = active;
            } catch (InvalidSettingsException e) {
                // else check if index is invalid otherwise default to active. This is also to cope
                // with old versions that encoded invalidness in an additional mimetype that is either present as inactive
                // in old generated NodeFactories or out of range in newer ones/dynamic factories.
                activePorts[i] = !(idxOOR || p.getMimeTypes().get(selectedPorts[i]).toLowerCase() == "inactive");
            }
            
            try{ //get linked inport
                int linked = settings
                        .getInt(GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_LINKEDINPUT
                                + i);
                linkedInputPorts[i] = linked;
            } catch (InvalidSettingsException e) {
                // probably an older version then. Index 0 is auto.
                linkedInputPorts[i] = 0;
            }
            
            try{ //get custom basename
                String bn = settings
                        .getString(GenericKnimeNodeModel.GENERIC_KNIME_NODES_OUT_CUSTOMBASENAME
                                + i);
                customBasenames[i] = bn;
            } catch (InvalidSettingsException e) {
                // probably an older version then.
                customBasenames[i] = "";
            }
        }
        // let the outputtype tab know about the settings so we can show when opened
        mtc.setSelectedTypes(selectedPorts);
        mtc.setActivePorts(activePorts);
        mtc.setBasenameTextboxes(customBasenames);
        mtc.setSelectedLinkedInports(linkedInputPorts);
    }
}
