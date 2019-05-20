package com.genericworkflownodes.knime.nodes.io.index;



import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Index-Type and its registered extensions.
 * 
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexTypeEntry {

    private String m_type;

    private List<String> m_extensions;

    /**
     * @param type Name of this Index-Type
     */
    IndexTypeEntry(final String type) {
        m_type = type;
        m_extensions = new LinkedList<String>();
    }

    /**
     * @return The Index-Types name
     */
    public String getType() {
        return m_type;
    }

    /**
     * @return The extensions of this Index-Type
     */
    public List<String> getExtensions() {
        return m_extensions;
    }

    /**
     * @param extension Extension to register with this type
     */
    void addExtension(final String extension) {
        m_extensions.add(extension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(m_type);
        for (int i = 0; i < m_extensions.size(); i++) {
            result.append(" " + m_extensions.get(i));
        }
        return result.toString();
    }

}
