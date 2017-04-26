package com.genericworkflownodes.knime.commandline;

/**
 * Represents the umbrella of the different individual elements that conform the
 * command line, such as, flags, input files, output files, parameters, etc.
 * 
 * Implementations of this interface must wrap a single command line element,
 * not a list of elements. If several instances of this interface are related to
 * the same parameter (i.e., they have the same key), sequence numbers can be
 * used.
 * 
 * @author delagarza
 * 
 */
public interface CommandLineElement {

    /**
     * Since not all instances are related to other instance by their keys,
     * implementations could
     */
    public final static int NO_SEQUENCE = -1;

    /**
     * This forces implementing classes to provide a non-default string
     * representation (i.e. {@link #toString()}).
     * 
     * @return The string representation of this element.
     */
    public String getStringRepresentation();

    /**
     * Certain command line elements can be referred to using a key, for
     * instance, the name of a parameter.
     * 
     * @return The key of this element.
     */
    public String getKey();

    /**
     * Determines if an instance of this interface is associated to other
     * command line elements by having the same key. For instance, parameters
     * that take a list of values would have the same key and therefore should
     * be sequenced.
     * 
     * @return {@code true} if this instance is related to other instances.
     */
    public boolean isSequenced();

    /**
     * Instances related to other instances (i.e., having the same {@code key})
     * use different sequence numbers. This method provides a way to obtain the
     * sequence number of a particular instance.
     * 
     * @return the sequence number of this instance, or
     */
    public int getSequenceNumber();

    /**
     * Simple getter method to set the sequence number. Instead of forcing
     * implementations to add this as a part of their constructors, this method
     * has been provided.
     * 
     * @param sequenceNumber
     *            The sequence number.
     */
    public void setSequenceNumber(final int sequenceNumber);

    /**
     * Certain command line elements might have a value that takes special
     * meaning. For instance, input files might have a value referring to the
     * path of the file while simple integer parameters might just be that, a
     * number.
     * 
     * @return
     */
    public Object getValue();

    /**
     * The value of a parameter might change according to the execution context,
     * for instance, if this command line element represents an input file, its
     * value might change if the associated job is executed on a cluster.
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(final Object value);
}
