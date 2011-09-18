package org.ballproject.knime.base.treetabledialog.itemlist;

import org.ballproject.knime.base.parameter.DoubleListParameter;
import org.ballproject.knime.base.parameter.IntegerListParameter;
import org.ballproject.knime.base.parameter.ListParameter;
import org.ballproject.knime.base.parameter.Parameter;


public class ListParameterModel extends ItemListFillerDialogModel
{
	private Parameter<?> param;
	
	public ListParameterModel(Parameter<?> param)
	{
		super();
		this.param = param;
		init();
	}

	private void init()
	{
		if(param instanceof ListParameter)
		{
			ListParameter lp = (ListParameter) param;
			this.data = lp.getStrings();
			if(param instanceof DoubleListParameter)
			{
				DoubleListParameter dlp = (DoubleListParameter) param;	
				DoubleValidator val = new DoubleValidator(); 
				val.setLowerBound(dlp.getLowerBound());
				val.setUpperBound(dlp.getUpperBound());
				this.setValidator(val);
			}
			if(param instanceof IntegerListParameter)
			{
				IntegerListParameter ilp = (IntegerListParameter) param;	
				IntegerValidator val = new IntegerValidator(); 
				val.setLowerBound(ilp.getLowerBound());
				val.setUpperBound(ilp.getUpperBound());
				this.setValidator(val);
			}
		}
		else
		{
			throw new RuntimeException();
		}
	}
}
