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
import org.knime.core.node.workflow.ModelContentOutPortView;

/**
 * PortObject that wrappes multiple managed and unmanaged URIs and exposes them
 * as a {@link IURIPortObject} referencing the underlying {@link FileStore}s
 * without recreating or copying them.
 * 
 * @author aiche
 */
public class FileStoreReferenceURIPortObject extends FileStorePortObject
        implements IURIPortObject {

    /**
     * The key of the rel-path setting stored while loading/saving.
     */
    private static final String SETTINGS_KEY_REL_PATH = "rel-path";

    /**
     * The key of the rel-path setting stored while loading/saving.
     */
    private static final String SETTINGS_KEY_FS_INDEX = "fs-index";

    /**
     * The prefix used to identify model contents belonging to this object.
     */
    private static final String MODEL_PREFIX = "fs-wrapped-";

    /**
     * List of URIContent objects managed by this port object.
     */
    private List<URIContent> m_uriContents;

    /**
     * List of paths stored inside the underlying filestore, relative to it's
     * location.
     */
    private List<String> m_relPaths;

    /**
     * List of indices of the associated {@link FileStore}.
     */
    private List<Integer> m_fsIndices;

    /**
     * The PortObjectSpec of the underlying content.
     */
    private URIPortObjectSpec m_uriPortObjectSpec;

    /**
     * Framework c'tor.
     */
    FileStoreReferenceURIPortObject() {
    }

    private FileStoreReferenceURIPortObject(List<URIContent> uriContents,
            List<String> relPaths, List<Integer> fsIndices,
            List<FileStore> fileStores) {
        // file stores are managed by FileStorePortObject
        super(fileStores);

        m_uriContents = uriContents;
        m_relPaths = relPaths;
        m_fsIndices = fsIndices;

        // create spec from contents
        m_uriPortObjectSpec = URIPortObjectSpec.create(m_uriContents);
    }

    /**
     * 
     * @param uriPortObjects
     */
    public static FileStoreReferenceURIPortObject create(
            List<IURIPortObject> uriPortObjects) {

        List<URIContent> uriContents = new ArrayList<URIContent>();
        List<String> relPaths = new ArrayList<String>();
        List<Integer> fsIndices = new ArrayList<Integer>();
        List<FileStore> fileStores = new ArrayList<FileStore>();

        for (IURIPortObject po : uriPortObjects) {
            int count = 0;
            for (URIContent uriContent : po.getURIContents()) {
                uriContents.add(uriContent);
                if (po instanceof AbstractFileStoreURIPortObject) {
                    AbstractFileStoreURIPortObject afspo = (AbstractFileStoreURIPortObject) po;
                    relPaths.add(afspo.getRelativePaths().get(count));
                    fileStores.add(afspo.getInternalFileStore());
                    fsIndices.add(fileStores.size() - 1);
                } else if (po instanceof FileStoreReferenceURIPortObject) {
                    FileStoreReferenceURIPortObject frpo = (FileStoreReferenceURIPortObject) po;
                    relPaths.add(frpo.getRelativePath(count));
                    //get the old fileStore for the current URIContent
                    fileStores.add(frpo.getFileStore(frpo.getFileStoreIndex(count)));
                    fsIndices.add(fileStores.size() - 1);
                } else {
                    // we add a dummy relative path for (non-FileStore-based) URIPortObjects etc.
                    relPaths.add("");
                    fsIndices.add(-1);
                }
                ++count;
            }
        }
        return new FileStoreReferenceURIPortObject(uriContents, relPaths,
                fsIndices, fileStores);
    }

    @Override
    public String getSummary() {
        return CustomPortObjectUtils.getSummary(this);
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
     * Reconstruct the {@link FileStoreReferenceURIPortObject} from the given
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
        List<URIContent> uriContents = new ArrayList<URIContent>();
        List<String> relPaths = new ArrayList<String>();
        List<Integer> fsIndices = new ArrayList<Integer>();
        for (String key : model.keySet()) {
            if (key.startsWith(MODEL_PREFIX)) {
                ModelContentRO child = model.getModelContent(key);
                uriContents.add(URIContent.load(child));
                relPaths.add(child.getString(SETTINGS_KEY_REL_PATH));
                fsIndices.add(child.getInt(SETTINGS_KEY_FS_INDEX));
            }
        }

        m_uriContents = uriContents;
        m_relPaths = relPaths;
        m_fsIndices = fsIndices;
        m_uriPortObjectSpec = (URIPortObjectSpec) spec;
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
        for (int i = 0; i < m_uriContents.size() && i < m_relPaths.size()
                && i < m_fsIndices.size(); ++i) {
            ModelContentWO child = model.addModelContent(MODEL_PREFIX + i);
            m_uriContents.get(i).save(child);
            child.addString(SETTINGS_KEY_REL_PATH, m_relPaths.get(i));
            child.addInt(SETTINGS_KEY_FS_INDEX, m_fsIndices.get(i));
        }

    }

    @Override
    protected void postConstruct() throws IOException {
        // call super if they have something todo
        super.postConstruct();

        List<URIContent> relocatedURIContents = new ArrayList<URIContent>();

        //
        for (int i = 0; i < m_uriContents.size() && i < m_relPaths.size(); ++i) {
            if (m_fsIndices.get(i) != -1) {
                File fileInNewFileStore = new File(getFileStore(
                        m_fsIndices.get(i)).getFile(), m_relPaths.get(i));
                if (!fileInNewFileStore.exists()) {
                    throw new IOException(
                            String.format(
                                    "Could not locate file %s in FileStoreURIPortObject.",
                                    m_relPaths.get(i)));
                }
                // create new URIContent using the rel path and the old
                // extension
                // infos
                relocatedURIContents.add(new URIContent(fileInNewFileStore
                        .toURI(), m_uriContents.get(i).getExtension()));

            } else {
                relocatedURIContents.add(m_uriContents.get(i));
            }
        }

        m_uriContents = relocatedURIContents;
    }

    @Override
    public List<URIContent> getURIContents() {
        return m_uriContents;
    }

    @Override
    public URIPortObjectSpec getSpec() {
        return m_uriPortObjectSpec;
    }
    
    public String getRelativePath(int index) {
        return m_relPaths.get(index);
    }
    
    public Integer getFileStoreIndex(int index) {
        return m_fsIndices.get(index);
    }

}
