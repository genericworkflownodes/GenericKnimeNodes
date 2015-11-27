package com.genericworkflownodes.knime.commandline.impl;

/**
 * A flag is a command line element that has no associated value to it. A
 * typical example of a flag is the <i>verbose</i> flag, {@code -v}.
 * 
 * @author delagarza
 * 
 */
public class CommandLineFlag extends AbstractCommandLineElement {

    public CommandLineFlag(String keyAndValue) {
        super(keyAndValue);
    }

    @Override
    public String getStringRepresentation() {
        return this.key;
    }
}
