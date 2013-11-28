package com.genericworkflownodes.knime.nodegeneration.model.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class MimeTypesFile extends File {

    public final class MIMETypeEntry {

        private String m_type;

        private List<String> m_extensions;

        /**
         * @param type
         *            Name of this MIME-Type
         */
        public MIMETypeEntry(final String type) {
            m_type = type;
            m_extensions = new LinkedList<String>();
        }

        /**
         * @return The MIME-Types name
         */
        public String getType() {
            return m_type;
        }

        /**
         * @return The extensions of this MIME-Type
         */
        public List<String> getExtensions() {
            return m_extensions;
        }

        /**
         * @param extension
         *            Extension to register with this type
         */
        void addExtension(final String extension) {
            m_extensions.add(extension);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            String result = m_type;
            for (int i = 0; i < m_extensions.size(); i++) {
                result += " " + m_extensions.get(i);
            }
            return result;
        }

    }

    private static final long serialVersionUID = -1620704972604551679L;

    public MimeTypesFile(String pathname) {
        super(pathname);
    }

    public List<MIMETypeEntry> getMIMETypeEntries() {
        List<MIMETypeEntry> mimetypes = new LinkedList<MIMETypeEntry>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this));
            String line;
            // Every line is a new MIME-Type
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    // Split on space or tab
                    String[] tokens = line.split("[ \t]+");
                    if (tokens.length > 0) {
                        // Create MIME-Entry (first token is always the name)
                        MIMETypeEntry entry = new MIMETypeEntry(tokens[0]);
                        // All other tokens are extensions to this MIME-Type
                        for (int i = 1; i < tokens.length; i++) {
                            entry.addExtension(tokens[i]);
                        }
                        mimetypes.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            // If file is not readable return nothing
        }
        return mimetypes;
    }
}
