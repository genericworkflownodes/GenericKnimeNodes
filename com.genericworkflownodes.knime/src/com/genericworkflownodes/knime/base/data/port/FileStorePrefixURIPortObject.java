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

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.PortObjectSpec;

/**
 * PortObject collecting URIs stored with a common prefix. Mostly used when the
 * number of generated files is not known in advance.
 * 
 * @author aiche
 */
public class FileStorePrefixURIPortObject extends
        AbstractFileStoreURIPortObject implements IPrefixURIPortObject {

    /**
     * Key to access/store the prefix value from/to model content.
     */
    private static final String SETTINGS_KEY_PREFIX = "prefix";
    /**
     * Key to access/store the base class content from/to model content.
     */
    private static final String SETTINGS_KEY_PARENT_SETTINGS = "parent";

    /**
     * The actual prefix, relative to the filestore.
     */
    private String m_prefix;

    /**
     * The default c'tor.
     */
    FileStorePrefixURIPortObject() {
        m_prefix = "";
    }
    
    /**
     * Access to the PortObjectSerializer.
     * 
     * @return The PortObjectSerializer.
     */
    public static final PortObjectSerializer<FileStorePrefixURIPortObject> getPortObjectSerializer() {
        return new FileStorePrefixURIPortObjectSerializer();
    }
    
    /**
     * Create a new {@link FileStorePrefixURIPortObject} given an associated
     * file store and the prefix for the files that should be represented.
     * 
     * @param fs
     *            The {@link FileStore} associated to this port object.
     * @param prefix
     *            The common prefix of all the contained files.
     * 
     */
    public FileStorePrefixURIPortObject(FileStore fs, String prefix) {
        super(fs);
        m_prefix = prefix;
    }

    @Override
    void load(ModelContentRO model, PortObjectSpec spec, ExecutionMonitor exec)
            throws InvalidSettingsException {
        m_prefix = model.getString(SETTINGS_KEY_PREFIX);
        super.load(model.getModelContent(SETTINGS_KEY_PARENT_SETTINGS), spec,
                exec);
    }

    @Override
    void save(ModelContentWO model, ExecutionMonitor exec)
            throws CanceledExecutionException {
        model.addString(SETTINGS_KEY_PREFIX, m_prefix);
        super.save(model.addModelContent(SETTINGS_KEY_PARENT_SETTINGS), exec);
    }

    @Override
    public String getPrefix() {
        return (new File(getFileStoreRootDirectory(), m_prefix))
                .getAbsolutePath();
    }

    /**
     * Triggers the re-indexing of the current content of the port, by
     * collecting all files that share the given prefix. This method should be
     * called after all content was generated inside the file store.
     */
    public void collectFiles() {
        // we have generated a list of files based on a prefix
        final File f = new File(getPrefix());

        Iterator<File> fIt = FileUtils.iterateFiles(f.getParentFile(),
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        while (fIt.hasNext()) {
            File tf = fIt.next();
            if (!tf.isDirectory()) {
                final String abs_path = tf.getAbsolutePath();
                if (abs_path.startsWith(getPrefix())) {
                    // get relative path to filestore as string
                    File relPath = new File(tf.getName());
                    File parent = tf.getParentFile();
                    while (!parent.equals(getFileStoreRootDirectory())) {
                        relPath = new File(parent.getName(), relPath.toString());
                        parent = parent.getParentFile();
                    }

                    // register the found file at the underlying
                    // FileStoreURIPortObject
                    registerFile(relPath.toString());
                }
            }
        }

    }
}
