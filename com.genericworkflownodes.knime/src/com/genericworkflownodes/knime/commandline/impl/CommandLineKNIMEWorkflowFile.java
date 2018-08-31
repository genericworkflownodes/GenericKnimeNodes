package com.genericworkflownodes.knime.commandline.impl;

import java.io.File;
import java.io.IOException;

import com.genericworkflownodes.knime.parameter.FileParameter;

/**
 * It is possible to invoke KNIME workflows in <i>batch mode</i>. Instances of
 * this element represent an archive in which a KNIME workflow is found. The
 * command line is similar to {@code -workflowFile=/tmp/wf.zip}.
 * 
 * @author delagarza
 * 
 */
public class CommandLineKNIMEWorkflowFile extends CommandLineFile {

    /**
     * Default key.
     */
    public static final String KNIME_MINI_WORKFLOW_KEY = "knmwf.zip";

    public CommandLineKNIMEWorkflowFile(final File workflowFile)
            throws IOException {
        super(new FileParameter(KNIME_MINI_WORKFLOW_KEY,
                workflowFile.getCanonicalPath()), "-workflowFile=", "");
    }
}
