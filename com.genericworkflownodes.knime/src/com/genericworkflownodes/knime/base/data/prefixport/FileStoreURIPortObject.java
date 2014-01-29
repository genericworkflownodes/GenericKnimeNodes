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
package com.genericworkflownodes.knime.base.data.prefixport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStorePortObject;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ConvenienceMethods;
import org.knime.core.node.workflow.ModelContentOutPortView;

import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * PortObject behaving like a URIPortObject but with managed file storage.
 * 
 * @author aiche
 */
public class FileStoreURIPortObject extends FileStorePortObject implements
        IURIPortObject {

    public static final PortObjectSerializer<FileStoreURIPortObject> getPortObjectSerializer() {
        return FileStoreURIPortObjectSerializer.getSerializer();
    }

    /**
     * List of files stored in the port object.
     */
    private List<URIContent> m_uriContents;

    /**
     * The PortObjectSpec of the underlying content.
     */
    private URIPortObjectSpec m_uriPortObjectSpec;

    /**
     * Creates {@link FileStoreURIPortObject} with the given {@link FileStore}.
     * 
     * @param fs
     *            The {@link FileStore} associated to this port object.
     */
    public FileStoreURIPortObject(FileStore fs) {
        super(fs);
        m_uriContents = new ArrayList<URIContent>();
    }

    /**
     * Default constructor, should only be used by the
     * FileStoreURIPortObjectSerializer.
     */
    FileStoreURIPortObject() {
        m_uriContents = new ArrayList<URIContent>();
    }

    /**
     * Returns the folder where all content related to this port object should
     * be stored.
     * 
     * @return A folder name.
     */
    private File getFile() {
        File fsf = getFileStore().getFile();
        // make sure that it is a directory as we want to store all content in
        // this directory
        fsf.mkdirs();
        assert fsf.isDirectory();

        return fsf;
    }

    public File registerFile(String filename) {
        File child = new File(getFile(), filename);
        URIContent uric = new URIContent(child.toURI(),
                MIMETypeHelper.getMIMEtypeExtension(filename));

        // update content and spec accordingly
        m_uriContents.add(uric);
        m_uriPortObjectSpec = URIPortObjectSpec.create(m_uriContents);

        // give the file object to the client so he can work with it
        return child;
    }

    @Override
    public String getSummary() {
        StringBuilder b = new StringBuilder();
        int size = m_uriContents.size();
        b.append(size);
        b.append(size == 1 ? " file (extension: " : " files (extensions: ");
        b.append(ConvenienceMethods.getShortStringFrom(
                m_uriPortObjectSpec.getFileExtensions(), 3));
        b.append(")");
        return b.toString();
    }

    @Override
    public JComponent[] getViews() {
        try {
            ModelContent model = new ModelContent("Model Content");
            save(model, new ExecutionMonitor());
            return new JComponent[] { new ModelContentOutPortView(model) };
        } catch (CanceledExecutionException ex) {
            // shouldn't happen
        }
        return null;
    }

    /**
     * Save the currently managed files as model content.
     * 
     * @param model
     *            The {@link ModelContentWO} object to fill with the list of
     *            files.
     */
    void save(final ModelContentWO model, final ExecutionMonitor exec)
            throws CanceledExecutionException {
        int i = 0;
        for (URIContent uri : m_uriContents) {
            ModelContentWO child = model.addModelContent("file-" + i);
            uri.save(child);
            i++;
        }
    }

    void load(final ModelContentRO model, PortObjectSpec spec,
            ExecutionMonitor exec) throws InvalidSettingsException {
        List<URIContent> list = new ArrayList<URIContent>();
        for (String key : model.keySet()) {
            if (key.startsWith("file-")) {
                ModelContentRO child = model.getModelContent(key);
                list.add(URIContent.load(child));
            }
        }

        m_uriContents = list;
        m_uriPortObjectSpec = (URIPortObjectSpec) spec;
    }

    @Override
    public List<URIContent> getURIContents() {
        return m_uriContents;
    }

    @Override
    public URIPortObjectSpec getSpec() {
        return m_uriPortObjectSpec;
    }

    @Override
    protected void postConstruct() throws IOException {
        // call super if they have something todo
        super.postConstruct();

        // validate m_uriContents against content of the file store

        for (URIContent content : m_uriContents) {
            System.out.println("Validating: " + content.getURI().toString());
        }

        // clear the current state
        // m_uriContents.clear();

        // construct uri_content/spec from directory content (recursively)
        // fillFromFile(getFileStore().getFile());
        // m_uriPortObjectSpec = URIPortObjectSpec.create(m_uriContents);
    }

    private void fillFromFile(File fs_dir) {
        Iterator<File> fIt = FileUtils.iterateFiles(fs_dir,
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        while (fIt.hasNext()) {
            File currentFile = fIt.next();
            if (!currentFile.isDirectory()) {
                final String ext = MIMETypeHelper
                        .getMIMEtypeExtension(currentFile.getAbsolutePath());
                m_uriContents.add(new URIContent(currentFile.toURI(), ext));
            } else {
                fillFromFile(currentFile);
            }

        }
    }

    @Override
    protected void flushToFileStore() throws IOException {
        super.flushToFileStore();
        // nothing todo .. for now
    }
}
