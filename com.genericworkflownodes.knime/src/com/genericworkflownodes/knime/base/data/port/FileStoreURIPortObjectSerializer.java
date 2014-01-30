/**
 * Copyright (c) 2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;
import java.util.zip.ZipEntry;

import org.knime.core.data.util.NonClosableInputStream;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.PortObject.PortObjectSerializer;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;

/**
 * {@link PortObjectSerializer} for {@link FileStoreURIPortObject}.
 * 
 * @author aiche
 */
public class FileStoreURIPortObjectSerializer extends
        PortObjectSerializer<FileStoreURIPortObject> {

    /**
     * Private c'tor to ensure singleton pattern.
     */
    private FileStoreURIPortObjectSerializer() {
    }

    /**
     * Initialization-on-demand holder idiom holder for the DemanglerRegistry
     * instance.
     * 
     * @author aiche
     */
    private static class LazyHolder {
        private static final FileStoreURIPortObjectSerializer INSTANCE = new FileStoreURIPortObjectSerializer();
    }

    /**
     * Returns the singleton FileStoreURIPortObjectSerializer.
     * 
     * @return The FileStoreURIPortObjectSerializer.
     */
    public static FileStoreURIPortObjectSerializer getSerializer() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void savePortObject(FileStoreURIPortObject portObject,
            PortObjectZipOutputStream out, ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // save some general information
        ModelContent model = new ModelContent("model.xml");
        model.addInt("version", 1);
        model.addString("class_name", portObject.getClass().getName());
        // get model content from PortObject
        ModelContentWO subModel = model.addModelContent("model");
        portObject.save(subModel, exec);
        // save model content to stream
        out.putNextEntry(new ZipEntry("content.xml"));
        model.saveToXML(out);

    }

    @Override
    public FileStoreURIPortObject loadPortObject(PortObjectZipInputStream in,
            PortObjectSpec spec, ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {

        // retrieve model content from stream
        ZipEntry entry = in.getNextEntry();
        if (!"content.xml".equals(entry.getName())) {
            throw new IOException("Expected zip entry content.xml, got "
                    + entry.getName());
        }
        ModelContentRO model = ModelContent
                .loadFromXML(new NonClosableInputStream.Zip(in));

        // default construct PortObject
        FileStoreURIPortObject result = new FileStoreURIPortObject();

        // ..and load from model content from stream
        try {
            ModelContentRO subModel = model.getModelContent("model");
            result.load(subModel, spec, exec);
            return result;
        } catch (InvalidSettingsException e) {
            throw new IOException(
                    "Unable to load model content into \"FileStoreURIPortObject\": "
                            + e.getMessage(), e);
        }
    }

}
