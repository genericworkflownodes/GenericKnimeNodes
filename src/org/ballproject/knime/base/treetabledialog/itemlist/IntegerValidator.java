package org.ballproject.knime.base.treetabledialog.itemlist;

public class IntegerValidator implements Validator
{
	private String reason = "N/A";
	private Integer UB = Integer.MAX_VALUE;
	private Integer LB = Integer.MIN_VALUE;
	
	@Override
	public boolean validate(String s)
	{
		Integer i = null;
		try
		{
			i = Integer.parseInt(s);	
		}
		catch(NumberFormatException e)
		{
			reason = "invalid number format";
			return false;	
		}
		
		if(i>UB)
		{
			reason = "higher than upper bound";
			return false;
		}
		if(i<LB)
		{
			reason = "lower than lower bound";
			return false;
		}
		
		return true;
	}

	@Override
	public String getName()
	{
		return "integer";
	}

	public void setLowerBound(int i)
	{
		LB = i;
	}
	
	public void setUpperBound(int i)
	{
		UB = i;
	}

	@Override
	public String getReason()
	{
		return reason;
	}
}
