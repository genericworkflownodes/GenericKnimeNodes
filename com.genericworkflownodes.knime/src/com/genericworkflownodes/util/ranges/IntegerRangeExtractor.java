package com.genericworkflownodes.util.ranges;

/**
 * {@link RangeExtractor} for integers.
 * 
 * @author aiche
 */
public class IntegerRangeExtractor extends RangeExtractor<Integer> {

	@Override
	protected Integer getUpperBoundDefault() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected Integer getLowerBoundDefault() {
		return Integer.MIN_VALUE;
	}

	@Override
	protected Integer parseString(final String str) {
		return Integer.parseInt(str);
	}

}