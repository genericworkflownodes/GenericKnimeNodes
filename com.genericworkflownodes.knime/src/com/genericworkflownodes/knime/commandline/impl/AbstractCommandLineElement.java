package com.genericworkflownodes.knime.commandline.impl;

import com.genericworkflownodes.knime.commandline.CommandLineElement;

public abstract class AbstractCommandLineElement implements CommandLineElement {

    protected final String key;
    protected String associatedPortName;
    protected Object value;
    protected int sequenceNumber = CommandLineElement.NO_SEQUENCE;

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
    public Object getValue() {
        return value;
    }

    @Override
    public final void setValue(final Object value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.genericworkflownodes.knime.commandline.CommandLineElement#isSequenced
     * ()
     */
    @Override
    public boolean isSequenced() {
        return this.sequenceNumber != CommandLineElement.NO_SEQUENCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.genericworkflownodes.knime.commandline.CommandLineElement#
     * getSequenceNumber()
     */
    @Override
    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.genericworkflownodes.knime.commandline.CommandLineElement#
     * setSequenceNumber(int)
     */
    @Override
    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
