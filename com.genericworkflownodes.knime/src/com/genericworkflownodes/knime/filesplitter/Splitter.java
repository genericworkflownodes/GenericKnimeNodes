package com.genericworkflownodes.knime.filesplitter;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public interface Splitter {

    public void split(File input, File... output) throws IOException;
    public void loadSettingsFrom(NodeSettingsRO settings);
    public void saveSettingsTo(NodeSettingsWO settings);
    
}
