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

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.filestore.FileStoreCell;

/**
 * 
 * @author aiche
 */
public class PortObjectHandlerCell extends FileStoreCell {
    /**
     * 
     */
    private static final long serialVersionUID = -3128874200295481196L;

    /**
     * The TYPE.
     */
    public static final DataType TYPE = DataType
            .getType(PortObjectHandlerCell.class);

    /**
     * 
     * @param fs
     */
    public PortObjectHandlerCell(AbstractFileStoreURIPortObject fs) {
        super(fs.getInternalFileStore());
    }

    /**
     * 
     */
    public PortObjectHandlerCell() {
    }

    /**
     * 
     * @return
     */
    public static final DataCellSerializer<PortObjectHandlerCell> getCellSerializer() {
        return new DataCellSerializer<PortObjectHandlerCell>() {

            @Override
            public void serialize(PortObjectHandlerCell cell,
                    DataCellDataOutput output) throws IOException {
                // do nothing
            }

            @Override
            public PortObjectHandlerCell deserialize(DataCellDataInput input)
                    throws IOException {
                return new PortObjectHandlerCell();
            }
        };
    }
}