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
 * PortObjectSerializer for {@link FileStoreReferenceURIPortObject}.
 * 
 * @author aiche
 */
public class FileStoreReferenceURIPortObjectSerializer extends
        PortObjectSerializer<FileStoreReferenceURIPortObject> {

    /**
     * Model identifier.
     */
    private static final String MODEL_IDENTIFIER = "model";
    /**
     * content.xml file name.
     */
    private static final String CONTENT_XML = "content.xml";

    /**
     * Private c'tor to ensure singleton pattern.
     */
    private FileStoreReferenceURIPortObjectSerializer() {
    }

    /**
     * Initialization-on-demand holder idiom holder for the serializer instance.
     * 
     * @author aiche
     */
    private static class LazyHolder {
        private static final FileStoreReferenceURIPortObjectSerializer INSTANCE = new FileStoreReferenceURIPortObjectSerializer();
    }

    /**
     * Returns the singleton FileStoreReferenceURIPortObjectSerializer.
     * 
     * @return The FileStoreReferenceURIPortObjectSerializer.
     */
    public static FileStoreReferenceURIPortObjectSerializer getSerializer() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void savePortObject(FileStoreReferenceURIPortObject portObject,
            PortObjectZipOutputStream out, ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // save some general information
        ModelContent model = new ModelContent("model.xml");
        model.addInt("version", 1);
        model.addString("class_name", portObject.getClass().getName());
        // get model content from PortObject
        ModelContentWO subModel = model.addModelContent(MODEL_IDENTIFIER);
        portObject.save(subModel, exec);
        // save model content to stream
        out.putNextEntry(new ZipEntry(CONTENT_XML));
        model.saveToXML(out);
    }

    @Override
    public FileStoreReferenceURIPortObject loadPortObject(
            PortObjectZipInputStream in, PortObjectSpec spec,
            ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // retrieve model content from stream
        ZipEntry entry = in.getNextEntry();
        if (!CONTENT_XML.equals(entry.getName())) {
            throw new IOException("Expected zip entry content.xml, got "
                    + entry.getName());
        }
        ModelContentRO model = ModelContent
                .loadFromXML(new NonClosableInputStream.Zip(in));

        // default construct PortObject
        FileStoreReferenceURIPortObject result = new FileStoreReferenceURIPortObject();

        // ..and load from model content from stream
        try {
            ModelContentRO subModel = model.getModelContent(MODEL_IDENTIFIER);
            result.load(subModel, spec, exec);
            return result;
        } catch (InvalidSettingsException e) {
            throw new IOException(
                    "Unable to load model content into \"FileStoreURIPortObject\": "
                            + e.getMessage(), e);
        }
    }

}
