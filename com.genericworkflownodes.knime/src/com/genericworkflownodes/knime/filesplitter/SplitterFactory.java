package com.genericworkflownodes.knime.filesplitter;

public interface SplitterFactory {
    
    /**
     * Determines whether the splitter is applicable for the given mimetype.
     * @param mimetype the mimetype
     * @return the true if the splitter created by this factory can split files of the given mimetype
     */
    public boolean isApplicable(String mimetype);
    
    /**
     * Determines whether the splitter has a settings panel.
     * @return true if the splitter has a settings panel
     */
    public boolean hasSettingsPanel();
    
    /**
     * @return the splitters settings panel or <code>null</code>
     *             if {@link #hasSettingsPanel() hasSettingsPanel} returns false.
     */
    public SplitterPanel getSettingsPanel();
    
    /**
     * @return the factory's name as shown in the GUI
     */
    public String getDisplayName();
    
    /**
     * @return the factory's unique ID
     */
    public String getID();
    
    /**
     * @return a new instance of a splitter
     */
    public Splitter createSplitter();
}
