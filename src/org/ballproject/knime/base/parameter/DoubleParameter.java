package org.ballproject.knime.base.parameter;

public class DoubleParameter extends NumberParameter<Double>
{

	public DoubleParameter(String key, Double value)
	{
		super(key, value);
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}
	
	public DoubleParameter(String key, String value)
	{
		super(key, (value.equals("")?null:Double.parseDouble(value)));
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}

	@Override
	public String toString()
	{
		if(value==null)
			return null;
		return String.format("%e",value);
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = null;
			return;
		}
		try
		{
			value = Double.parseDouble(s);
		}
		catch(NumberFormatException e)
		{
			throw new InvalidParameterValueException("parameter "+this.getKey()+" value is not a double",e);
		}
		if(value<this.getLowerBound()||value>this.getUpperBound())
		{
			throw new InvalidParameterValueException("parameter "+this.getKey()+" value is out of bounds");
		}
	}
	
	@Override
	public boolean validate(Double val)
	{
		if(isNull())
			return true;
		if(val>=this.lowerBound && val<=this.upperBound)
			return true;
		return false;
	}
	
	@Override
	public String getMnemonic()
	{
		String lb = (this.lowerBound==Double.NEGATIVE_INFINITY?"-inf":String.format("%e", this.lowerBound));
		String ub = (this.upperBound==Double.POSITIVE_INFINITY?"+inf":String.format("%e", this.upperBound));
		return String.format("double [%s:%s]",lb,ub);
	}
}
