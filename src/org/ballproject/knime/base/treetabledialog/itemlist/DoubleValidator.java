package org.ballproject.knime.base.treetabledialog.itemlist;

public class DoubleValidator implements Validator
{
	private Double UB = Double.POSITIVE_INFINITY;
	private Double LB = Double.NEGATIVE_INFINITY;
	private String reason="N/A";
	
	@Override
	public boolean validate(String s)
	{
		Double d = null;
		try
		{
			d = Double.parseDouble(s);	
		}
		catch(NumberFormatException e)
		{
			reason = "invalid number format";
			return false;	
		}
		
		if(d>UB)
		{
			reason = "higher than upper bound";
			return false;
		}
		if(d<LB)
		{
			reason = "lower than lower bound";
			return false;
		}
		
		return true;
	}

	@Override
	public String getName()
	{
		return "double";
	}
	
	public void setLowerBound(Double d)
	{
		LB = d;
	}
	
	public void setUpperBound(Double d)
	{
		UB = d;
	}

	@Override
	public String getReason()
	{
		return reason;
	}
}
