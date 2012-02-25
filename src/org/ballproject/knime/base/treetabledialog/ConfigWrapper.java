/*
 * Copyright (c) 2011, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ballproject.knime.base.treetabledialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.Parameter;

public class ConfigWrapper 
{
	private NodeConfiguration config;
	
	public ConfigWrapper(NodeConfiguration config)
	{
		this.config = config;
		init();
	}
	
	private ParameterNode root = new ParameterNode(null,null,"root");
	
	public ParameterNode getRoot()
	{
		return root;
	}
	
	private static List<String> getPrefixes(String key)
	{
		List<String> ret = new ArrayList<String>();
		String[] toks = key.split("\\.");
		String pref="";
		for(String tok: toks)
		{
			pref+=tok+".";
			ret.add(pref.substring(0,pref.length()-1));
		}
		return ret;
	}
	
	public static String getSuffix(String s)
	{
		String[] toks = s.split("\\.");
		return toks[toks.length-1];
	}
	
	private void init()
	{
		Map<String,ParameterNode> key2node = new HashMap<String,ParameterNode>();
		
		for(String key: config.getParameterKeys())
		{
			List<String> prefixes = getPrefixes(key);
			
			// OpenMS/CADDSuite workaround for leading '1' NODE
			if(prefixes.get(0).equals("1"))
				prefixes.remove(0);
			
			ParameterNode last = root;
			for(int i=0;i<prefixes.size()-1;i++)
			{
				String prefix = prefixes.get(i);
				
				if(!key2node.containsKey(prefix))
				{
					
					ParameterNode nn = new ParameterNode(last, null, getSuffix(prefix));
					last.addChild(nn);
					last = nn;
					key2node.put(prefix, last);
				}
				else
				{
					last = key2node.get(prefix);
				}
			}
			
			Parameter<?>  p = config.getParameter(key);
			ParameterNode n = new ParameterNode(last, p, p.getKey());
			last.addChild(n);
		}
	}
}
