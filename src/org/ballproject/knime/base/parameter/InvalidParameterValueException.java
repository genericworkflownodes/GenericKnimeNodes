package org.ballproject.knime.base.parameter;

public class InvalidParameterValueException extends Exception
{
	public InvalidParameterValueException(String msg)
	{
		super(msg);
	}
	
	public InvalidParameterValueException(String msg, Throwable t)
	{
		super(msg,t);
	}
}
