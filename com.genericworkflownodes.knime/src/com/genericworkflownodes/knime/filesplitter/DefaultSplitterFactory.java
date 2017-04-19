package com.genericworkflownodes.knime.filesplitter;

public abstract class DefaultSplitterFactory implements SplitterFactory {

    @Override
    public abstract boolean isApplicable(String mimetype);

    @Override
    public boolean hasSettingsPanel() {
        return false;
    }

    @Override
    public SplitterPanel getSettingsPanel() {
        return null;
    }

    @Override
    public abstract String getDisplayName();

    @Override
    public abstract Splitter createSplitter();

}
