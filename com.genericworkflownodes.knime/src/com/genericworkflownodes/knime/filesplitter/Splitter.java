package com.genericworkflownodes.knime.filesplitter;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public interface Splitter {

    /**
     * Splits a file and writes the parts to the given output files.
     * @param input the file to split
     * @param output the files to write the output parts to
     * @throws IOException when the input or output files cannot be accessed
     */
    public void split(File input, File... output) throws IOException;
    
    /**
     * Load splitter settings.
     * @param settings the settings to load from
     */
    public void loadSettingsFrom(NodeSettingsRO settings);
    
    /**
     * Saves settings of this splitter.
     * @param settings the settings to save into
     */
    public void saveSettingsTo(NodeSettingsWO settings);
    
}
