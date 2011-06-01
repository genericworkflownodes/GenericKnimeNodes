package org.ballproject.knime.base.parameter;

public abstract class NumberParameter<T> extends Parameter<T>
{

	protected T lowerBound;
	protected T upperBound;
	
	public NumberParameter(String key, T value)
	{
		super(key, value);
	}

	public T getLowerBound()
	{
		return lowerBound;
	}

	public void setLowerBound(T lowerBound)
	{
		this.lowerBound = lowerBound;
	}

	public T getUpperBound()
	{
		return upperBound;
	}

	public void setUpperBound(T upperBound)
	{
		this.upperBound = upperBound;
	}
}
