package com.genericworkflownodes.knime.commandline.impl;

/**
 * Represents a fixed string in a command line.
 * 
 * @author delagarza
 * 
 */
public class CommandLineFixedString extends AbstractCommandLineElement {

    public CommandLineFixedString(final String name) {
        super(name);
    }

    @Override
    public String getStringRepresentation() {
        return this.key;
    }

    @Override
    public String getExternalStringRepresentation() {
        return getStringRepresentation();
    }
}
