/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Nov 9, 2012 (Patrick Winter): created
 */
package com.genericworkflownodes.knime.nodes.io.dirimporter;


import org.knime.base.node.io.listfiles.ListFiles.Filter;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.StringHistory;

/**
 * Configuration for the node.
 *
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
class ListDirectoryConfiguration {

    /** Key to store the location settings. */
    public static final String LOCATION_SETTINGS = "directory";

    /** Key to store the RECURSIVE_SETTINGS. */
    public static final String RECURSIVE_SETTINGS = "recursive";

    /** ID for the file history. */
    private static final String LIST_FILES_HISTORY_ID = "List Files History ID";

    /** ID for the extension history. */
    private static final String LIST_FILES_EXT_HISTORY_ID =
            "LIST_FILES_EXT_HISTORY_ID";
 
    /** ID for the expression history. */
    private static final String LIST_FILES_EXP_HISTORY_ID =
            "LIST_FILES_EXP_HISTORY_ID";

    /** Key to store the Filter Settings. */
    public static final String FILTER_SETTINGS = "FILTER_SETTINGS";

    /** Key to store the case sensitive settings. */
    public static final String CASE_SENSITIVE_STRING = "CASESENSITVE";

    /** Key to store the extension_settings. */
    public static final String EXTENSION_SETTINGS = "EXTENSION";
    /** Key to store the extension_settings. */
    public static final String EXPRESSIONS_SETTINGS = "EXPRESSIONS";

    private String m_directory;

    private boolean m_recursive = true;

    /** contains the log-format of the files. */
    private String m_extensionsString;
    
    /** contains the log-format of the expressions. */
    private String m_expressionsString;

    /** Flag to switch between case sensitive and insensitive. */
    private boolean m_caseSensitive = false;

    /** Filter type. */
    private Filter m_filter = Filter.None;
    
    /** Filter type. */
    private Filter m_extfilter = Filter.Extensions;

    /**
     * @return the directory
     */
    public String getDirectory() {
        return m_directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(final String directory) {
        m_directory = directory;
    }

    /**
     * @return the recursive
     */
    public boolean getRecursive() {
        return m_recursive;
    }

    /**
     * @param recursive the recursive to set
     */
    public void setRecursive(final boolean recursive) {
        m_recursive = recursive;
    }

    /**
     * Save the configuration.
     *
     *
     * @param settings The <code>NodeSettings</code> to write to
     */
    void saveSettingsTo(final NodeSettingsWO settings) {
        settings.addString(LOCATION_SETTINGS, m_directory);
        settings.addBoolean(RECURSIVE_SETTINGS, m_recursive);
        settings.addString(EXTENSION_SETTINGS, m_extensionsString);
        settings.addString(EXPRESSIONS_SETTINGS, m_expressionsString);
        settings.addString(FILTER_SETTINGS, m_filter.name());
        settings.addBoolean(CASE_SENSITIVE_STRING, m_caseSensitive);

        if (m_directory != null) {
            StringHistory h = StringHistory.getInstance(LIST_FILES_HISTORY_ID);
            h.add(m_directory);
        }

        if (m_extensionsString != null) {
            StringHistory h =
                    StringHistory.getInstance(LIST_FILES_EXT_HISTORY_ID);
            h.add(m_extensionsString);

        }
        if (m_expressionsString != null) {
            StringHistory h =
                    StringHistory.getInstance(LIST_FILES_EXP_HISTORY_ID);
            h.add(m_expressionsString);

        }
    }

    /**
     * Load the configuration.
     *
     *
     * @param settings The <code>NodeSettings</code> to read from
     */
    void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_directory = settings.getString(LOCATION_SETTINGS, "");
        m_extensionsString = settings.getString(EXTENSION_SETTINGS, "");
        m_expressionsString = settings.getString(EXPRESSIONS_SETTINGS, "");
        m_recursive = settings.getBoolean(RECURSIVE_SETTINGS, true);
        final Filter defFilter = Filter.None;
        String filterS = settings.getString(FILTER_SETTINGS, defFilter.name());
        if (filterS == null) {
            filterS = defFilter.name();
        }
        try {
            m_filter = Filter.valueOf(filterS);
        } catch (IllegalArgumentException iae) {
            m_filter = defFilter;
        }
        m_caseSensitive = settings.getBoolean(CASE_SENSITIVE_STRING, false);
    }

    /**
     * Load the configuration and check for validity.
     *
     *
     * @param settings The <code>NodeSettings</code> to read from
     * @throws InvalidSettingsException If one of the settings is not valid
     */
    void loadSettingsInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_directory = settings.getString(LOCATION_SETTINGS);
        m_extensionsString = settings.getString(EXTENSION_SETTINGS, "");
        m_expressionsString = settings.getString(EXPRESSIONS_SETTINGS, "");
        m_recursive = settings.getBoolean(RECURSIVE_SETTINGS);
        String filterS = settings.getString(FILTER_SETTINGS, Filter.None.name());
        m_filter = Filter.valueOf(filterS);
        m_caseSensitive = settings.getBoolean(CASE_SENSITIVE_STRING, false);
    }

    /**
     * Checks if the setting is not null or empty.
     *
     *
     * @param name The name that will be displayed in case of error
     * @param setting The setting to check
     * @throws InvalidSettingsException If the string is null or empty
     */
    public void validate(final String name, final String setting) throws InvalidSettingsException {
        if (setting == null || setting.length() == 0) {
            throw new InvalidSettingsException(name + " missing");
        }
    }

    /** @return the caseSensitive */
    public boolean isCaseSensitive() {
        return m_caseSensitive;
    }

    /** @param caseSensitive the caseSensitive to set */
    public void setCaseSensitive(final boolean caseSensitive) {
        m_caseSensitive = caseSensitive;
    }

    /** @return the extensionsString */
    public String getExtensionsString() {
        return m_extensionsString;
    }

    /** @param extensionsString the extensionsString to set */
    public void setExtensionsString(final String extensionsString) {
        m_extensionsString = extensionsString;
    }
    
    /** @return the extensionsString */
    public String getExpressionsString() {
        return m_expressionsString;
    }

    /** @param extensionsString the extensionsString to set */
    public void setExpressionsString(final String expressionsString) {
        m_expressionsString = expressionsString;
    }
    
    /**
     * @param filter the extension filter to set
     * @throws NullPointerException If argument is null.
     */
    public void setExtFilter(final Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Argument must not be null.");
        }
        m_extfilter = filter;
    }
    
    /** @return the filter */
    public Filter getExtFilter() {
        return m_extfilter;
    }


    /** @return the filter */
    public Filter getFilter() {
        return m_filter;
    }

    /**
     * @param filter the filter to set
     * @throws NullPointerException If argument is null.
     */
    public void setFilter(final Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Argument must not be null.");
        }
        m_filter = filter;
    }

    /** @return the previous selected extension field strings. */
    static String[] getExtensionHistory() {
        StringHistory h = StringHistory.getInstance(LIST_FILES_EXT_HISTORY_ID);
        return h.getHistory();
    }
    
    /** @return the previous selected expression field strings. */
    static String[] getExpressionHistory() {
        StringHistory h = StringHistory.getInstance(LIST_FILES_EXP_HISTORY_ID);
        return h.getHistory();
    }
}
