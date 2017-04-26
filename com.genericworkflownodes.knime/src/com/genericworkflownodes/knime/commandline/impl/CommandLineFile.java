package com.genericworkflownodes.knime.commandline.impl;

import com.genericworkflownodes.knime.commandline.ICommandLineFile;
import com.genericworkflownodes.knime.parameter.FileParameter;

public class CommandLineFile extends CommandLineParameter
        implements ICommandLineFile {

    public CommandLineFile(final FileParameter fileParameter) {
        super(fileParameter);
    }

    public CommandLineFile(final FileParameter fileParameter,
            final String prefix, final String suffix) {
        super(fileParameter, prefix, suffix);
    }

    @Override
    public FileParameter getValue() {
        return (FileParameter) super.getValue();
    }

}
