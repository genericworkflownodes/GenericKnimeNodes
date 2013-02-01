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
package com.genericworkflownodes.knime.mime.demangler;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Defines a {@link IDemangler} which converts a specified {@link MIMEType} into
 * a KNIME table.
 * 
 * @author roettig,aiche
 * 
 */
public interface IDemangler extends Serializable {

	/**
	 * Returns the {@link MIMEType} that this {@link IDemangler} can translate.
	 * 
	 * @return The convertible {@link MIMEType}.
	 */
	String getMIMEType();

	/**
	 * Returns the {@link DataTableSpec} which will be created by the
	 * {@link IDemangler}.
	 * 
	 * @return The {@link DataTableSpec} of the table that will be generated.
	 */
	DataTableSpec getTableSpec();

	/**
	 * Returns the {@link PortObjectSpec} for the {@link MIMEType} that will be
	 * created while mangling.
	 * 
	 * @return The {@link PortObjectSpec} that will be created by this
	 *         {@link IDemangler}.
	 */
	PortObjectSpec getPortOjectSpec();

	/**
	 * Demangles the given file by returning an {@link Iterator} to the rows
	 * that can be added directly to the generated table.
	 * 
	 * @param file
	 *            The file to demangle.
	 * @return An {@link Iterator} to the file content in {@link DataRow} form.
	 */
	Iterator<DataRow> demangle(URI file);

	/**
	 * Writes the content of the table to the given file.
	 * 
	 * @param table
	 *            The {@link BufferedDataTable} which should be translated to a
	 *            file.
	 * @param file
	 *            The file where the content of the table should be stored.
	 */
	void mangle(BufferedDataTable table, URI file);
}
