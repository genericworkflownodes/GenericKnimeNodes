package com.genericworkflownodes.knime.filesplitter;

public interface SplitterFactory {
    
    public boolean isApplicable(String mimetype);
    public boolean hasSettingsPanel();
    public SplitterPanel getSettingsPanel();
    public String getDisplayName();
    public String getID();
    public Splitter createSplitter();
}
