package com.genericworkflownodes.knime.commandline.impl;

/**
 * An option is typically prefixed with a dash to either indicate that what
 * follows is a parameter, or to set a binary flag. The following are examples
 * of options:
 * 
 * <ul>
 * <li> {@code -level 3} - in this case, {@code -level} is an option that
 * indicates that the next element is a parameter.
 * <li> {@code --verbose} - in this case, {@code --verbose} indicates the program
 * that the user is setting the <i>verbose</i> flag.
 * </ul>
 * 
 * @author delagarza
 * 
 */
public class CommandLineOptionIdentifier extends AbstractCommandLineElement {

    public CommandLineOptionIdentifier(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getStringRepresentation() {
        return this.key;
    }

}
