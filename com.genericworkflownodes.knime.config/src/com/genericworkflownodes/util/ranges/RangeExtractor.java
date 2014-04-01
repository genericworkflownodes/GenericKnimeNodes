package com.genericworkflownodes.util.ranges;

/**
 * Abstract class to extract ranges from strings of the form "0:10".
 * 
 * @author aiche
 * 
 * @param <T>
 *            The type of Number to extract.
 */
abstract class RangeExtractor<T extends Number> {

    /**
     * This method returns the default upper bound for the given number type
     * T.
     * 
     * @return The default upper bound.
     */
    protected abstract T getUpperBoundDefault();

    /**
     * This method returns the default lower bound for the given number type
     * T.
     * 
     * @return The default lower bound.
     */
    protected abstract T getLowerBoundDefault();

    /**
     * Tries to convert the given string into the specified number type T.
     * 
     * @param str
     *            The string to convert.
     * @return A number corresponding to the passed string.
     */
    protected abstract T parseString(String str);

    /**
     * Extracts the given lower bound from the string.
     * 
     * @param str
     *            The string from which the lower bound should be extracted.
     * @return The lower bound contained in the string or the default lower
     *         bound.
     */
    public T getLowerBound(final String str) {

        String[] toks = str.split(":");

        if (toks.length > 0 && !"".equals(toks[0])) {
            return parseString(toks[0]);
        }

        return getLowerBoundDefault();
    }

    /**
     * Extracts the given upper bound from the string.
     * 
     * @param str
     *            The string from which the upper bound should be extracted.
     * @return The upper bound contained in the string or the default upper
     *         bound.
     */
    public T getUpperBound(final String str) {
        String[] toks = str.split(":");

        if (toks.length > 1 && !"".equals(toks[1])) {
            return parseString(toks[1]);
        }

        return getUpperBoundDefault();
    }
}