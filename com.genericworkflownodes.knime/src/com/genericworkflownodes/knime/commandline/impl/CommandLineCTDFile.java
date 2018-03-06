package com.genericworkflownodes.knime.commandline.impl;

import java.io.File;
import java.io.IOException;

import com.genericworkflownodes.knime.parameter.FileParameter;

/**
 * Represents a CTD file fed into a program via the command line.
 * 
 * @author delagarza
 * 
 */
public class CommandLineCTDFile extends CommandLineFile {

    /**
     * Constructor.
     * 
     * @param ctdFile
     *            the ctd file.
     * @throws IOException
     */
    public CommandLineCTDFile(final File ctdFile) throws IOException {
        super(new FileParameter("ctdfile", ctdFile.getCanonicalPath()));
    }

}
