package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.filestore.FileStore;

/**
 * Cell for storing the content of <code>FileStorePrefixURIPortObjects</code>.
 * @author Alexander Fillbrunn
 *
 */
public class PrefixFileStoreCell extends SerializableFileStoreCell {
    
    private static final long serialVersionUID = 23123123L;
    private List<String> m_relativePaths;
    private String m_prefix;
    
    /**
     * Creates a new <code>PrefixFileStoreCell</code> from a file store.
     * @param fs the file store to use.
     * @param prefix the prefix of the stored <code>FileStorePrefixURIPortObjects</code>.
     * @param relativePaths the paths of the files in the port.
     */
    public PrefixFileStoreCell(FileStore fs, String prefix, List<String> relativePaths) {
        super(fs);
        m_prefix = prefix;
        m_relativePaths = relativePaths;
    }

    /**
     * Framework c'tor
     */
    PrefixFileStoreCell() {}

    /**
     * Creates a new <code>PrefixFileStoreCell</code> from a <code>FileStorePrefixURIPortObjects</code>.
     * @param po the <code>FileStorePrefixURIPortObjects</code> to initialize the cell with
     */
    public PrefixFileStoreCell(FileStorePrefixURIPortObject po) {
        this(po.getInternalFileStore(), po.getPlainPrefix(), po.getRelativePaths());
    }

    /**
     * Saves the cell to an output.
     * @param output the output to save to
     * @throws IOException when an I/O error occurs
     */
    public void save(final DataCellDataOutput output) throws IOException {
        output.writeUTF(m_prefix);
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
        m_prefix = input.readUTF();
        int n = input.readInt();
        for (int i = 0; i < n; i++) {
            relPaths.add(input.readUTF());
        }
        m_relativePaths = relPaths;
    }
    
    @Override
    public AbstractFileStoreURIPortObject getPortObject() {
        FileStorePrefixURIPortObject po = new FileStorePrefixURIPortObject(getFileStore(), m_prefix);
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
        sb.append("Prefix:")
            .append(System.lineSeparator())
            .append("\t")
            .append(m_prefix)
            .append(System.lineSeparator());
        sb.append("Files:");
        for (String p : m_relativePaths) {
            sb.append(System.lineSeparator()).append("\t").append(p);
        }
        return sb.toString();
    }
}
