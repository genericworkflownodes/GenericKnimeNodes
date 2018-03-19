package com.genericworkflownodes.knime.cluster.filesplitter.impl;

import com.genericworkflownodes.knime.cluster.filesplitter.DefaultSplitterFactory;
import com.genericworkflownodes.knime.cluster.filesplitter.Splitter;

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
