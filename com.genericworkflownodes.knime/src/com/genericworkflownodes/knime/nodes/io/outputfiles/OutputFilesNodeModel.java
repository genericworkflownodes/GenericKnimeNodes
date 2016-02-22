/**
 * Copyright (c) 2011-2013, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.outputfiles;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of OutputFiles Node.
 * 
 * @author roettig, aiche
 */
public class OutputFilesNodeModel extends NodeModel {

    static final String CFG_FILENAME = "FILENAME";

    SettingsModelString m_filename = new SettingsModelString(
            OutputFilesNodeModel.CFG_FILENAME, "");

    /**
     * Constructor for the node model.
     */
    protected OutputFilesNodeModel() {
        super(new PortType[] { PortTypeRegistry.getInstance().getPortType(IURIPortObject.class) },
                new PortType[] {});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // set string value to ""
        m_filename.setStringValue("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_filename.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_filename.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_filename.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        if (!(inSpecs[0] instanceof URIPortObjectSpec)) {
            throw new InvalidSettingsException(
                    "No URIPortObjectSpec compatible port object");
        }

        // check the selected file
        if ("".equals(m_filename.getStringValue())) {
            throw new InvalidSettingsException(
                    "Please select a target file for the Output Files node.");
        }

        if (!mimeTypeCompatible(inSpecs)) {
            throw new InvalidSettingsException(
                    "The selected output files and the incoming files have incompatible mime types.");
        }

        return new PortObjectSpec[] {};
    }

    /**
     * Checks if incoming and outgoing mime types are compatible.
     * 
     * @param inSpecs
     *            The incoming port spec.
     * @return True if the mime types are compatible, false otherwise.
     */
    private boolean mimeTypeCompatible(PortObjectSpec[] inSpecs) {
        String selectedMimeType = MIMETypeHelper.getMIMEtype(m_filename
                .getStringValue());
        String incomingMimeType = MIMETypeHelper
                .getMIMEtypeByExtension(((URIPortObjectSpec) inSpecs[0])
                        .getFileExtensions().get(0));
        return incomingMimeType.equals(selectedMimeType);
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        IURIPortObject obj = (IURIPortObject) inObjects[0];
        List<URIContent> uris = obj.getURIContents();

        if (uris.size() == 0) {
            throw new Exception(
                    "There were no URIs in the supplied IURIPortObject");
        }

        int idx = 1;
        for (URIContent uri : uris) {
            File in = new File(uri.getURI());
            if (!in.canRead()) {
                throw new Exception("Cannot read file to export: "
                        + in.getAbsolutePath());
            }

            String outfilename = insertIndex(m_filename.getStringValue(), obj
                    .getSpec().getFileExtensions().get(0), idx++);
            File out = new File(outfilename);

            if (out.exists() && !out.canWrite()) {
                throw new Exception("Cannot write to file: "
                        + out.getAbsolutePath());
            } else if (!out.getParentFile().canWrite()) {
                throw new Exception("Cannot write to containing directoy: "
                        + out.getParentFile().getAbsolutePath());
            }

            FileUtils.copyFile(in, out);
        }
        return null;
    }

    private static String insertIndex(String filename, String extension, int idx) {
        if (filename.equals("") || filename.length() == 0) {
            return filename;
        }

        String filename_ = filename.toLowerCase();
        String ext = extension.toLowerCase();

        int idx1 = filename_.lastIndexOf(ext);

        if (idx == -1) {
            return filename;
        }

        String s1 = filename.substring(0, idx1);
        return s1 + idx + "." + extension;
    }
}
