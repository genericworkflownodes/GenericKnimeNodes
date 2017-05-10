package com.genericworkflownodes.knime.commandline.impl;

import com.genericworkflownodes.knime.commandline.ParametrizedCommandLineElement;

/**
 * Abstract class for all parametrized command line elements.
 * @author delagarza
 *
 */
public abstract class AbstractParametrizedCommandLineElement extends
        AbstractCommandLineElement implements ParametrizedCommandLineElement {

    /**
     * Builds a new command line element using the same string for key and
     * value.
     * 
     * @param name
     *            The string to use for both key and value.
     */
    public AbstractParametrizedCommandLineElement(final String name) {
        super(name);
    }

    /**
     * Constructor using key and value.
     * 
     * @param key
     *            The key to use.
     * @param value
     *            The value to use.
     */
    public AbstractParametrizedCommandLineElement(final String key,
            final Object value) {
        super(key, value);
    }

}
