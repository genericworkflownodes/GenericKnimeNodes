package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataType;
import org.knime.core.data.filestore.FileStore;

/**
 * Cell for storing the content of <code>AbstractFileStoreURIPortObject</code>.
 * @author Alexander Fillbrunn
 *
 */
public class SimpleFileStoreCell extends SerializableFileStoreCell {

    /**
     * The cell type.
     */
    public static final DataType TYPE = DataType.getType(SimpleFileStoreCell.class);
    
    private static final long serialVersionUID = 1L;
    private List<String> m_relativePaths;
    
    /**
     * Creates a new <code>PrefixFileStoreCell</code> from a file store.
     * @param fs the file store to use.
     * @param relativePaths the paths of the files in the port.
     */
    public SimpleFileStoreCell(FileStore fs, List<String> relativePaths) {
        super(fs);
        m_relativePaths = relativePaths;
    }
    
    /**
     * Creates a new <code>PrefixFileStoreCell</code> from a <code>AbstractFileStoreURIPortObject</code>.
     * @param po the <code>AbstractFileStoreURIPortObject</code> to initialize the cell with
     */
    public SimpleFileStoreCell(AbstractFileStoreURIPortObject po) {
        this(po.getInternalFileStore(), po.getRelativePaths());
    }

    /**
     * Framework c'tor
     */
    SimpleFileStoreCell() {}

    /**
     * Saves the cell to an output.
     * @param output the output to save to
     * @throws IOException when an I/O error occurs
     */
    public void save(final DataCellDataOutput output) throws IOException {
        output.writeInt(m_relativePaths.size());
        for (String s : m_relativePaths) {
            output.writeUTF(s);
        }
    }

    /**
     * Loads the cell content from an input.
     * @param input the input to load from
     * @throws IOException when an I/O error occurs
     */
    public void load(final DataCellDataInput input) throws IOException {
        List<String> relPaths = new ArrayList<>();
        int n = input.readInt();
        for (int i = 0; i < n; i++) {
            relPaths.add(input.readUTF());
        }
        m_relativePaths = relPaths;
    }
    
    @Override
    public AbstractFileStoreURIPortObject getPortObject() {
        FileStoreURIPortObject po = new FileStoreURIPortObject(this.getFileStore());
        for (String s : m_relativePaths) {
            po.registerFile(s);
        }
        return po;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("File store:")
            .append(System.lineSeparator())
            .append("\t")
            .append(this.getFileStore().getFile().getAbsolutePath())
            .append(System.lineSeparator());
        sb.append("Files:");
        for (String p : m_relativePaths) {
            sb.append(System.lineSeparator()).append("\t").append(p);
        }
        return sb.toString();
    }
}
