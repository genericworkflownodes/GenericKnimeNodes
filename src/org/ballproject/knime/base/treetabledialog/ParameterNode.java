package org.ballproject.knime.base.treetabledialog;

import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.parameter.Parameter;

public class ParameterNode extends Node<Parameter<?>>
{	
	protected List<Node<Parameter<?>>> children2 = new ArrayList<Node<Parameter<?>>>();
	
	public ParameterNode(Node<Parameter<?>> p, Parameter<?> payload, String name)
	{
		super(p,payload,name);
	}

	@Override
	public void addChild(Node<Parameter<?>> child)
	{
		super.addChild(child);
		if(child.getPayload()!=null&&!child.getPayload().isAdvanced())
			children2.add(child);
	}

	@Override
	public Node<Parameter<?>> getChild(int idx)
	{
		return (ParameterNode) super.getChild(idx);
	}
	
	public Node<Parameter<?>> getChild(int idx, boolean showAdvanced)
	{
		if(showAdvanced)
			return (ParameterNode) super.getChild(idx);
		else
			return (ParameterNode) children2.get(idx);
	}
	
	public int getNumChildren(boolean showAdvanced)
	{
		if(showAdvanced)
			return super.getNumChildren();
		else
			return children2.size();
	}
	
	
	/*
	public int getNumChildren(boolean includingAdvanced)
	{
		if(includingAdvanced)
		{
			return super.getNumChildren();
		}
		else
		{			
			int N = 0;
			for(int i=0;i<super.getNumChildren();i++)
			{
				Node<Parameter<?>> np = super.getChild(i);
				System.out.println(np);
				System.out.println(np.getPayload());
				if(np.getPayload()==null)
					continue;
				if(!np.getPayload().isAdvanced())
					N++;
			}
			return N;
		}
	}

	public ParameterNode getChild(int idx, boolean includingAdvanced)
	{
		ParameterNode ret = null;
		if(includingAdvanced)
		{
			//ret =  super.getChild(idx);
		}
		else
		{
			int i = 0;
			for(Node<Parameter<?>> np : this.children)
			{
				if(!np.getPayload().isAdvanced())
				{
					if(i==idx)
					{
						//ret = np;
						break;
					}
					i++;
				}
			}
		}
		return ret;
	}
	*/
}
