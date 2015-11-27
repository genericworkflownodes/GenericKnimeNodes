package com.genericworkflownodes.knime.commandline.impl;

import com.genericworkflownodes.knime.commandline.CommandLineElement;

public abstract class AbstractCommandLineElement implements CommandLineElement {

    protected final String key;
    protected Object value;

    /**
     * Builds a new command line element using the same string for key and
     * value.
     * 
     * @param name
     *            The string to use for both key and value.
     */
    public AbstractCommandLineElement(final String name) {
        this(name, name);
    }

    /**
     * Constructor using key and value.
     * 
     * @param key
     *            The key to use.
     * @param value
     *            The value to use.
     */
    public AbstractCommandLineElement(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final Object getValue() {
        return value;
    }

    @Override
    public final void setValue(final Object value) {
        this.value = value;
    }

}
