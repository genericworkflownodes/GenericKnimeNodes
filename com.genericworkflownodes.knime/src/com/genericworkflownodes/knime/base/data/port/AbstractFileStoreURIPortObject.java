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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStorePortObject;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
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
public abstract class AbstractFileStoreURIPortObject extends
        FileStorePortObject implements IURIPortObject {

    /**
     * The number of extensions to show if string representation is generated.
     */
    private static final int NUMBER_OF_EXTENSIONS_TO_SHOW = 3;

    /**
     * The key of the rel-path setting stored while loading/saving.
     */
    private static final String SETTINGS_KEY_REL_PATH = "rel-path";

    /**
     * List of files stored in the port object.
     */
    private List<URIContent> m_uriContents;

    /**
     * List of paths stored inside the underlying filestore, relative to it's
     * location.
     */
    private List<String> m_relPaths;

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
    public AbstractFileStoreURIPortObject(FileStore fs) {
        super(Collections.singletonList(fs));
        m_uriContents = new ArrayList<URIContent>();
        m_relPaths = new ArrayList<String>();
    }

    /**
     * Default constructor, should only be used by the
     * FileStoreURIPortObjectSerializer.
     */
    AbstractFileStoreURIPortObject() {
        m_uriContents = new ArrayList<URIContent>();
        m_relPaths = new ArrayList<String>();
    }

    /**
     * Returns the folder where all content related to this port object should
     * be stored.
     * 
     * @return A folder name.
     */
    protected File getFileStoreRootDirectory() {
        File fsf = getFileStore(0).getFile();
        // make sure that it is a directory as we want to store all content in
        // this directory
        if (!fsf.exists()) {
            // this should not fail
            boolean success = fsf.mkdirs();
            assert success;
        }
        assert fsf.isDirectory();

        return fsf;
    }

    /**
     * Adds the given file to the {@link FileStoreURIPortObject}.
     * 
     * @param filename
     *            The relative path that should be stored inside the file-store,
     *            e.g., outfile.txt or subfolder/outfile.txt.
     * @return A {@link File} object pointing to the registered file.
     */
    public File registerFile(String filename) {
        // register the URIContent
        File child = new File(getFileStoreRootDirectory(), filename);
        URIContent uric = new URIContent(child.toURI(),
                MIMETypeHelper.getMIMEtypeExtension(filename));

        // update content and spec accordingly
        m_uriContents.add(uric);
        m_uriPortObjectSpec = URIPortObjectSpec.create(m_uriContents);
        m_relPaths.add(filename);

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
                m_uriPortObjectSpec.getFileExtensions(),
                NUMBER_OF_EXTENSIONS_TO_SHOW));
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
     * @param exec
     *            The associated execution context.
     */
    void save(final ModelContentWO model, final ExecutionMonitor exec)
            throws CanceledExecutionException {
        // store manged URIs
        for (int i = 0; i < m_uriContents.size() && i < m_relPaths.size(); ++i) {
            ModelContentWO child = model.addModelContent("file-" + i);
            m_uriContents.get(i).save(child);
            child.addString(SETTINGS_KEY_REL_PATH, m_relPaths.get(i));
        }
    }

    /**
     * Reconstruct the {@link AbstractFileStoreURIPortObject} from the given
     * {@link ModelContentRO}.
     * 
     * @param model
     *            The {@link ModelContentRO} from where the object should be
     *            reconstructed.
     * @param spec
     *            The expected {@link PortObjectSpec}.
     * @param exec
     *            The current {@link ExecutionContext}.
     * @throws InvalidSettingsException
     *             Thrown if the content is invalid.
     */
    void load(final ModelContentRO model, PortObjectSpec spec,
            ExecutionMonitor exec) throws InvalidSettingsException {
        List<URIContent> list = new ArrayList<URIContent>();
        List<String> relPathList = new ArrayList<String>();
        for (String key : model.keySet()) {
            if (key.startsWith("file-")) {
                ModelContentRO child = model.getModelContent(key);
                list.add(URIContent.load(child));
                relPathList.add(child.getString(SETTINGS_KEY_REL_PATH));
            }
        }

        m_uriContents = list;
        m_relPaths = relPathList;
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

        List<URIContent> relocatedURIContents = new ArrayList<URIContent>();

        //
        for (int i = 0; i < m_uriContents.size() && i < m_relPaths.size(); ++i) {
            File fileInNewFileStore = new File(getFileStoreRootDirectory(),
                    m_relPaths.get(i));
            if (!fileInNewFileStore.exists()) {
                throw new IOException(String.format(
                        "Could not locate file %s in FileStoreURIPortObject.",
                        m_relPaths.get(i)));
            }

            // create new URIContent using the rel path and the old extension
            // infos
            relocatedURIContents.add(new URIContent(fileInNewFileStore.toURI(),
                    m_uriContents.get(i).getExtension()));
        }

        m_uriContents = relocatedURIContents;
    }

    /**
     * Gives access to the underlying file store.
     * 
     * @return The underlying file store.
     */
    FileStore getInternalFileStore() {
        return getFileStore(0);
    }

    /**
     * Gives access to the list of relative paths inside the file store.
     * 
     * @return The the list of relative paths.
     */
    List<String> getRelativePaths() {
        return m_relPaths;
    }

    /**
     * Gives access to the list of relative paths inside the file store.
     * 
     * @return The the list of relative paths.
     */
    List<String> getRelativePaths() {
        return m_relPaths;
    }

}
