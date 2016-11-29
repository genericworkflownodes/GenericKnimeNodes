package com.genericworkflownodes.knime.filesplitter.impl;

import com.genericworkflownodes.knime.filesplitter.DefaultSplitterFactory;
import com.genericworkflownodes.knime.filesplitter.Splitter;

public class LineSplitterFactory extends DefaultSplitterFactory {

    @Override
    public String getID() {
        return "com.genericworkflownodes.knime.filesplitter.impl.LineSplitter";
    }

    @Override
    public boolean isApplicable(String mimetype) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Line Splitter";
    }

    @Override
    public Splitter createSplitter() {
        return new LineSplitter();
    }

}
