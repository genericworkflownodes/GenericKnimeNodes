package com.genericworkflownodes.knime.commandline;

/**
 * Represents the umbrella of the different elements that conform the command
 * line, such as, flags, input files, output files, parameters, etc.
 * 
 * @author delagarza
 * 
 */
public interface CommandLineElement {

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
