package com.genericworkflownodes.knime.base.data.port;

import org.knime.core.data.DataType;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStoreCell;

/**
 * Base class for cells holding files in a file store.
 * @author Alexander Fillbrunn
 *
 */
public abstract class SerializableFileStoreCell extends FileStoreCell implements FileStoreValue {

    /**
     * The cell type.
     */
    public static final DataType TYPE = DataType.getType(SerializableFileStoreCell.class);
    
    private static final long serialVersionUID = 895634789274359L;

    /**
     * Creates a new <code>SerializableFileStoreCell</code> from a file store.
     * @param fs the file store to use for initializing this cell.
     */
    public SerializableFileStoreCell(FileStore fs) {
        super(fs);
    }
    
    /**
     * Framework c'tor
     */
    SerializableFileStoreCell() {
        super();
    }
    
    /**
     * Returns the <code>AbstractFileStoreURIPortObject</code> represented by the cell.
     */
    public abstract AbstractFileStoreURIPortObject getPortObject();
}
