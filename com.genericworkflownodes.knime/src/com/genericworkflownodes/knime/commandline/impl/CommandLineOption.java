package com.genericworkflownodes.knime.commandline.impl;

/**
 * An option indicates programs that what follows is a parameter, for instance
 * {@code $ java -jar someJar.jar}; in this case {@code -jar} is an option.
 * 
 * @author delagarza
 * 
 */
public class CommandLineOption extends AbstractCommandLineElement {

    public CommandLineOption(String keyAndValue) {
        super(keyAndValue);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getStringRepresentation() {
        return this.key;
    }

}
