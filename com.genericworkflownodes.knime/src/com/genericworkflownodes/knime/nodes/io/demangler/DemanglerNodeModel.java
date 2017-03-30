/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.demangler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

import com.genericworkflownodes.knime.mime.demangler.DemanglerRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of DemanglerNodeModel.
 * 
 * 
 * @author roettig
 */
public class DemanglerNodeModel extends NodeModel {

    /**
     * Settings field where the currently selected demangler is stored.
     */
    static final String SELECTED_DEMANGLER_SETTINGNAME = "selected_demangler";

    /**
     * Settings field where the currently configured {@link MIMEType} is stored.
     */
    static final String CONFIGURED_FILE_EXTENSION_SETTINGNAME = "configured_mime_type";

    /**
     * The selected {@link IDemangler}.
     */
    private IDemangler demangler;

    /**
     * The currently configured {@link MIMEType}.
     */
    private String fileExtension;

    /**
     * Constructor for the node model.
     */
    protected DemanglerNodeModel() {
        super(new PortType[] { PortTypeRegistry.getInstance().getPortType(IURIPortObject.class) },
                new PortType[] { PortTypeRegistry.getInstance().getPortType(BufferedDataTable.class) });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        demangler = null;
        fileExtension = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        settings.addString(CONFIGURED_FILE_EXTENSION_SETTINGNAME, fileExtension);
        settings.addStringArray(SELECTED_DEMANGLER_SETTINGNAME, demangler
                .getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        String demanglerClassName = settings.getString(
                SELECTED_DEMANGLER_SETTINGNAME, "");
        fileExtension = settings
                .getString(CONFIGURED_FILE_EXTENSION_SETTINGNAME);
        String mimeType = MIMETypeHelper.getMIMEtypeByExtension(fileExtension);
        List<IDemangler> availableDemangler = DemanglerRegistry
                .getDemanglerRegistry().getDemangler(mimeType);

        demangler = null;
        if (!"".equals(demanglerClassName)) {
            for (IDemangler de : availableDemangler) {
                if (demanglerClassName.equals(de.getClass().getName())) {
                    demangler = de;
                    break;
                }
            }
        } else if (availableDemangler.size() > 0) {
            demangler = availableDemangler.get(0);
        }

        if (demangler == null) {
            throw new InvalidSettingsException(
                    "Could not find an implementation for the previously selected demangler: "
                            + demanglerClassName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }

    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        if (!(inSpecs[0] instanceof URIPortObjectSpec)) {
            throw new InvalidSettingsException(
                    "No URIPortObjectSpec compatible port object at port 0");
        }

        URIPortObjectSpec spec = (URIPortObjectSpec) inSpecs[0];
        fileExtension = spec.getFileExtensions().get(0);
        String mimeType = MIMETypeHelper.getMIMEtypeByExtension(fileExtension);
        // try to find a demangler for the data type ...

        List<IDemangler> availableDemanglers = DemanglerRegistry
                .getDemanglerRegistry().getDemangler(mimeType);

        if (availableDemanglers == null || availableDemanglers.size() == 0) {
            throw new InvalidSettingsException(
                    "No IDemangler found for "
                            + fileExtension
                            + ". Please register before transforming the a file with this MIMEType to a KNIME table.");
        }

        if (demangler == null) {
            demangler = availableDemanglers.get(0);
        }

        return new DataTableSpec[] { getDataTableSpec() };
    }

    /**
     * Retrieves the {@link DataTableSpec} from the selected {@link IDemangler}.
     * 
     * @return A configured {@link DataTableSpec}.
     * @throws InvalidSettingsException
     *             If the requested configuration can not be created.
     */
    private DataTableSpec getDataTableSpec() throws InvalidSettingsException {
        return demangler.getTableSpec();
    }

    @Override
    protected BufferedDataTable[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        BufferedDataContainer container = exec.createDataContainer(demangler
                .getTableSpec());

        IURIPortObject obj = (IURIPortObject) inObjects[0];
        List<URIContent> uris = obj.getURIContents();
        if (uris.size() == 0) {
            throw new Exception(
                    "No URI was supplied in IURIPortObject at input port 0");
        } else if (uris.size() != 1) {
            throw new Exception(String.format(
                    "We can only demangle a single file but got %d.",
                    uris.size()));
        }

        URI relURI = uris.get(0).getURI();

        Iterator<DataRow> iter = demangler.demangle(relURI);
        while (iter.hasNext()) {
            container.addRowToTable(iter.next());
        }
        container.close();
        BufferedDataTable out = container.getTable();

        return new BufferedDataTable[] { out };
    }

    @Override
    protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // TODO Auto-generated method stub

    }
}
