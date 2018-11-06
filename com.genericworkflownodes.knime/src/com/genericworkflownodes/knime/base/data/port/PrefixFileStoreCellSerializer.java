package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;

/**
 * Serializer for <code>PrefixFileStoreCells</code>.
 * @author Alexander Fillbrunn
 *
 */
public class PrefixFileStoreCellSerializer
        implements DataCellSerializer<PrefixFileStoreCell> {

    @Override
    public void serialize(PrefixFileStoreCell cell, DataCellDataOutput output)
            throws IOException {
        cell.save(output);
    }

    @Override
    public PrefixFileStoreCell deserialize(DataCellDataInput input)
            throws IOException {
        PrefixFileStoreCell cell = new PrefixFileStoreCell();
        cell.load(input);
        return cell;
    }
}
