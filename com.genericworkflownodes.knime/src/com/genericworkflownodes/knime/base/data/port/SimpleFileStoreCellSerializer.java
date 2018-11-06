package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;

/**
 * Serializer for <code>SimpleFileStoreCells</code>.
 * @author Alexander Fillbrunn
 *
 */
public class SimpleFileStoreCellSerializer
        implements DataCellSerializer<SimpleFileStoreCell> {

    @Override
    public void serialize(SimpleFileStoreCell cell, DataCellDataOutput output)
            throws IOException {
        cell.save(output);
    }

    @Override
    public SimpleFileStoreCell deserialize(DataCellDataInput input)
            throws IOException {
        SimpleFileStoreCell cell = new SimpleFileStoreCell();
        cell.load(input);
        return cell;
    }

}
