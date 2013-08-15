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
package org.knime.core.data.uri.candidate;

import java.util.List;

import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
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
		m_prefix = model.getString("prefix");
		ModelContent mc = new ModelContent("");

		int i = 0;

		for (String key : model.keySet()) {
			if (key.startsWith("child-")) {
				// load child into temp object and store this in temp
				// ModelContent
				ModelContentRO load_child = model.getModelContent(key);
				ModelContentWO save_child = mc.addModelContent("child-" + i);
				URIContent.load(load_child).save(save_child);
				i++;
			}
		}

		super.load(mc, spec, exec);
	}

	@Override
	protected void save(ModelContentWO model, ExecutionMonitor exec)
			throws CanceledExecutionException {
		super.save(model, exec);
		// also save the prefix
		model.addString("prefix", m_prefix);
	}
}
