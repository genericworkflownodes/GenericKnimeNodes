package com.genericworkflownodes.util.ranges;

/**
 * {@link RangeExtractor} for doubles.
 * 
 * @author aiche
 */
public class DoubleRangeExtractor extends RangeExtractor<Double> {

	@Override
	protected Double getUpperBoundDefault() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	protected Double getLowerBoundDefault() {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	protected Double parseString(final String str) {
		return Double.parseDouble(str);
	}

}