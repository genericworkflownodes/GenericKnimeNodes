package org.ballproject.knime.base.mime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMIMEtypeRegistry implements MIMEtypeRegistry
{
	protected List<MIMEtypeRegistry>   resolvers = new ArrayList<MIMEtypeRegistry>();
	
	@Override
	public MIMEFileCell getCell(String name)
	{
		MIMEFileCell cell = null;
		for(MIMEtypeRegistry resolver: resolvers)
		{
			try
			{
				cell = resolver.getCell(name);
			} 
			catch (Exception e)
			{
				cell = null;
			}
		}
		return cell;
	}

	@Override
	public void addResolver(MIMEtypeRegistry resolver)
	{
		resolvers.add(resolver);
	}

}
