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

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.StringValue;
import org.knime.core.data.filestore.FileStoreCell;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIDataValue;
import org.knime.core.node.NodeLogger;

/**
 * 
 * @author aiche
 */
public class PortObjectHandlerCell extends FileStoreCell implements
        URIDataValue, StringValue {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(PortObjectHandlerCell.class);

    private static final PortObjectHandlerCellSerializer SERIALIZER = new PortObjectHandlerCellSerializer();

    /**
     * DataCellSerializer for PortObjectHandlerCell.
     * 
     * @author aiche
     */
    private static final class PortObjectHandlerCellSerializer implements
            DataCellSerializer<PortObjectHandlerCell> {
        @Override
        public void serialize(PortObjectHandlerCell cell,
                DataCellDataOutput output) throws IOException {
            cell.save(output);
        }

        @Override
        public PortObjectHandlerCell deserialize(DataCellDataInput input)
                throws IOException {
            PortObjectHandlerCell cell = new PortObjectHandlerCell();
            cell.load(input);
            return cell;
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = -3128874200295481196L;

    /**
     * Relative path of the file inside the file store.
     */
    private String m_relativePath;

    /**
     * URIContent associated to the underlying file.
     */
    private URIContent m_uriContent;

    /**
     * The TYPE.
     */
    public static final DataType TYPE = DataType
            .getType(PortObjectHandlerCell.class);

    /**
     * C'tor to wrap an exisitng file store port object.
     * 
     * @param portObject
     */
    public PortObjectHandlerCell(AbstractFileStoreURIPortObject portObject) {
        // we reference the file store of the portObject
        super(portObject.getInternalFileStore());

        if (portObject.getURIContents().size() > 1) {
            LOGGER.warn("Incoming port object contains more then one file but. We will only be able to persist the first one.");
        }

        // get contents from portObject
        m_relativePath = portObject.getRelativePaths().get(0);
        m_uriContent = portObject.getURIContents().get(0);
    }

    /**
     * Framework c'tor.
     */
    PortObjectHandlerCell() {
    }

    @Override
    protected void postConstruct() throws IOException {
        // fix rel-path
        File relocatedFile = new File(getFileStore().getFile(), m_relativePath);
        if (!relocatedFile.exists()) {
            throw new IOException(String.format(
                    "Could not locate file %s in PortObjectHandlerCell.",
                    m_relativePath));
        }

        // re-create uricontent
        m_uriContent = new URIContent(relocatedFile.toURI(),
                m_uriContent.getExtension());
    }

    /**
     * Saves the content of the {@link PortObjectHandlerCell} to the given
     * {@link DataCellDataOutput}.
     * 
     * @param output
     *            The output where the {@link PortObjectHandlerCell} should be
     *            saved.
     * @throws IOException
     *             If writing fails.
     */
    public void save(final DataCellDataOutput output) throws IOException {
        // save uri-content
        m_uriContent.save(output);
        // save rel-path
        output.writeUTF(m_relativePath);
    }

    /**
     * Loads the content of the {@link PortObjectHandlerCell} from the given
     * {@link DataCellDataInput}.
     * 
     * @param input
     *            The input from where the cell should be loaded.
     * @throws IOException
     *             If reading fails.
     */
    public void load(final DataCellDataInput input) throws IOException {
        // load uri-content
        m_uriContent = URIContent.load(input);
        // load rel-path
        m_relativePath = input.readUTF();
    }

    /**
     * Access the matching DataCellSerializer.
     * 
     * @return The DataCellSerializer.
     */
    public static final DataCellSerializer<PortObjectHandlerCell> getCellSerializer() {
        return SERIALIZER;
    }

    @Override
    public String getStringValue() {
        return m_uriContent.getURI().toString();
    }

    @Override
    public URIContent getURIContent() {
        return m_uriContent;
    }
}