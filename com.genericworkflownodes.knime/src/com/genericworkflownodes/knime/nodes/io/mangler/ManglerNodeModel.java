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
package com.genericworkflownodes.knime.nodes.io.mangler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.mime.demangler.DemanglerRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.util.FileStashFactory;
import com.genericworkflownodes.util.FileStashProperties;
import com.genericworkflownodes.util.IFileStash;

/**
 * This is the model implementation of ManglerNodeModel.
 * 
 * @author roettig, aiche
 */
public class ManglerNodeModel extends NodeModel {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ManglerNodeModel.class);

    /**
     * Settings field where the currently selected demangler is stored.
     */
    static final String SELECTED_DEMANGLER_SETTINGNAME = "selected_demangler";

    /**
     * Settings field where the currently configured {@link MIMEType} is stored.
     */
    static final String AVAILABLE_MIMETYPE_SETTINGNAME = "available_demangler";

    /**
     * The selected {@link IDemangler}.
     */
    private IDemangler demangler;

    /**
     * Available {@link IDemangler}.
     */
    private List<IDemangler> availableMangler;

    /**
     * The currently active inputTalbeSpecification.
     */
    private DataTableSpec inputTalbeSpecification;

    private IFileStash fileStash;

    /**
     * Constructor for the node model.
     */
    protected ManglerNodeModel() {
        super(new PortType[] { new PortType(BufferedDataTable.class) },
                new PortType[] { new PortType(URIPortObject.class) });
        fileStash = FileStashFactory.createTemporary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {

        // translate portobject to table

        BufferedDataTable table = (BufferedDataTable) inData[0];

        // create a file where we can write to
        File file = fileStash.getFile(demangler.getMIMEType(), "mime");

        // translate the filename to a URIContent
        URIContent outputURI = new URIContent(file.toURI(),
                demangler.getMIMEType());

        // write file
        demangler.mangle(table, outputURI.getURI());

        // create list
        List<URIContent> uriList = new ArrayList<URIContent>();
        uriList.add(outputURI);

        return new URIPortObject[] { new URIPortObject(uriList) };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        demangler = null;
        availableMangler = null;
        try {
            fileStash.deleteAllFiles();
        } catch (IOException e) {
            LOGGER.error("Error cleaning " + IFileStash.class.getSimpleName(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        if (inSpecs[0] instanceof DataTableSpec) {

            inputTalbeSpecification = (DataTableSpec) inSpecs[0];

            availableMangler = DemanglerRegistry.getDemanglerRegistry()
                    .getMangler(inputTalbeSpecification);

            if (availableMangler == null || availableMangler.size() == 0) {
                throw new InvalidSettingsException(
                        "no IDemangler found for the given table configuration. "
                                + "Please register one before transforming the a file with "
                                + "this MIMEType to a KNIME table.");
            }

            if (demangler == null) {
                demangler = availableMangler.get(0);
            }

            return new URIPortObjectSpec[] { new URIPortObjectSpec(
                    demangler.getMIMEType()) };
        } else {
            throw new InvalidSettingsException("Cannot handle non-table input");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        settings.addString(SELECTED_DEMANGLER_SETTINGNAME, demangler.getClass()
                .getName());
        String[] manglers = new String[availableMangler.size()];
        int i = 0;
        for (IDemangler mangler : availableMangler) {
            manglers[i++] = mangler.getClass().getName();
        }

        settings.addStringArray(AVAILABLE_MIMETYPE_SETTINGNAME, manglers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        String manglerClassName = settings.getString(
                SELECTED_DEMANGLER_SETTINGNAME, "");

        List<IDemangler> matchingManglers = DemanglerRegistry
                .getDemanglerRegistry().getMangler(inputTalbeSpecification);

        demangler = null;
        for (IDemangler mangler : matchingManglers) {
            if (manglerClassName.equals(mangler.getClass().getName())) {
                demangler = mangler;
                break;
            }
        }

        if (demangler == null) {
            throw new InvalidSettingsException(
                    "Could not find an implementation for the previously selected mangler: "
                            + manglerClassName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        File file = FileStashProperties.readLocation(internDir);
        if (file != null) {
            fileStash = FileStashFactory.createPersistent(file);
        } // else leave temporary file stash
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        FileStashProperties.saveLocation(fileStash, internDir);
    }

}
