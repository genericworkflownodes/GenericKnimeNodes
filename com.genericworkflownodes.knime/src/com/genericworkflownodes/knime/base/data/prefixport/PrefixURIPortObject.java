/**
 * Copyright (c) 2013, Stephan Aiche.
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

import java.util.List;

import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.PortObjectSpec;

/**
 * {@link URIPortObject} that carries additional meta data regarding the prefix.
 * This is necessary for tools which use prefixs as input.
 * 
 * @author aiche
 */
public class PrefixURIPortObject extends URIPortObject {

    /**
     * The key used to save the additional model settings or the
     * {@link PrefixURIPortObject}.
     */
    private static final String SETTINGS_KEY_PREFIX = "prefix";

    /**
     * The key used to store the subtree of settings reserved for the parent
     * object.
     * 
     * @see URIPortObject
     */
    private static final String SETTINGS_KEY_PARENT_OBJECT = "parent";

    /**
     * The prefix associated with the content of this {@link URIPortObject}.
     */
    private String m_prefix;

    /**
     * Framework constructor. <b>Do not use in client code.</b>
     */
    public PrefixURIPortObject() {
        // filled later in load
    }

    /**
     * Constructor for new URI port objects.
     * 
     * @param spec
     *            The non null spec. All file extensions in the spec must be
     *            present in the list argument.
     * @param uriContents
     *            The contend for this object. Must not be null or contain null.
     * @param prefix
     *            The prefix that generated the uriContents.
     */
    public PrefixURIPortObject(URIPortObjectSpec spec,
            final List<URIContent> uriContents, String prefix) {
        super(spec, uriContents);
        m_prefix = prefix;
    }

    /**
     * Constructor for new URI port objects.
     * 
     * @param uriContents
     *            The contend for this object. Must not be null or contain null.
     * @param prefix
     *            The prefix that generated the uriContents.
     */
    public PrefixURIPortObject(final List<URIContent> uriContents, String prefix) {
        super(uriContents);
        m_prefix = prefix;
    }

    /**
     * Returns the prefix that was used to generate the associated file list.
     * 
     * @return The prefix.
     */
    public String getPrefix() {
        return m_prefix;
    }

    @Override
    protected void load(ModelContentRO model, PortObjectSpec spec,
            ExecutionMonitor exec) throws InvalidSettingsException,
            CanceledExecutionException {
        // load parent settings from parent reserved subtree
        super.load(model.getModelContent(SETTINGS_KEY_PARENT_OBJECT), spec,
                exec);
        // get our own model settings
        m_prefix = model.getString(SETTINGS_KEY_PREFIX);
    }

    @Override
    protected void save(ModelContentWO model, ExecutionMonitor exec)
            throws CanceledExecutionException {
        super.save(model.addModelContent(SETTINGS_KEY_PARENT_OBJECT), exec);
        // save our own settings
        model.addString(SETTINGS_KEY_PREFIX, m_prefix);
    }
}
