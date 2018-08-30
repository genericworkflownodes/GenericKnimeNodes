package com.genericworkflownodes.knime.commandline.impl;

import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Parameters are instances of variables. These might change depending on the
 * execution context (e.g., inside KNIME, on a cluster, in Galaxy).
 * 
 * Some parameters are more complex than a mere value and can have a prefix and
 * a suffix, e.g. {@code -workflow.variable="flow_var","35","int"}, the variable
 * part would be the value (in this case {@code 35}), yet the rest of the
 * parameter stays unchanged.
 * 
 * @author delagarza
 * 
 */
public class CommandLineParameter
        extends AbstractParametrizedCommandLineElement {

    protected final String prefix;
    protected final String suffix;

    /**
     * Constructor, prefix and suffix will be set to an empty string, and
     * sequence will be set to {@link #NO_SEQUENCE}.
     * 
     * @param associatedParameter
     *            The associated parameter.
     */
    public CommandLineParameter(final Parameter<?> associatedParameter) {
        this(associatedParameter, "", "");
    }

    /**
     * Constructor.
     * 
     * @param associatedParameter
     *            the associated parameter.
     * @param prefix
     *            the prefix to use.
     * @param suffix
     *            the suffix to use.
     */
    public CommandLineParameter(final Parameter<?> associatedParameter,
            final String prefix, final String suffix) {
        super(associatedParameter.getKey(), associatedParameter);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getStringRepresentation() {
        return generateStringRepresentation(
                ((Parameter<?>) this.getValue()).getStringRep());
    }

    @Override
    public String getExternalStringRepresentation() {
        // parameters don't change across platforms, allegedly
        return getStringRepresentation();
    }

    protected String generateStringRepresentation(final String value) {
        return this.prefix + value + this.suffix;
    }

}
