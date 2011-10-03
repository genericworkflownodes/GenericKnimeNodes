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

package org.ballproject.knime.base.mime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.base.mime.demangler.Demangler;
import org.knime.core.data.DataType;


public class DefaultMIMEtypeRegistry implements MIMEtypeRegistry
{
	protected Map<String,MIMEtype>  ext2mt    = new HashMap<String,MIMEtype>();
	protected Map<String,DataType>  ext2type  = new HashMap<String,DataType>();
	protected Map<DataType,String>  type2ext  = new HashMap<DataType,String>();
	protected Map<DataType,List<Demangler>>  demanglers = new HashMap<DataType,List<Demangler>>(); 
	protected Map<DataType,List<Demangler>>  manglers   = new HashMap<DataType,List<Demangler>>();
	
	@Override
	public List<Demangler> getDemangler(DataType type)
	{
		return demanglers.get(type);
	}

	@Override
	public void addDemangler(Demangler demangler)
	{
		if(!demanglers.containsKey(demangler.getSourceType()))
			demanglers.put(demangler.getSourceType(),new ArrayList<Demangler>());
		if(!manglers.containsKey(demangler.getTargetType()))
			manglers.put(demangler.getTargetType(),new ArrayList<Demangler>());
		demanglers.get(demangler.getSourceType()).add(demangler);
		manglers.get(demangler.getTargetType()).add(demangler);
	}

	@Override
	public List<Demangler> getMangler(DataType type)
	{
		return manglers.get(type);
	}
	
	@Override
	public void registerMIMEtype(MIMEtype mt)
	{
		DataType dt = DataType.getType(mt.getKNIMEClass());
		String extension = mt.getExt();
		ext2mt.put(extension, mt);
		ext2type.put(extension.toLowerCase(),dt);
		type2ext.put(dt,extension.toLowerCase());
	}

	@Override
	public boolean isCompatible(DataType dt1, DataType dt2)
	{
		String ext1 = type2ext.get(dt1);
		String ext2 = type2ext.get(dt2);
		
		if(ext1==null || ext2==null)
			return false;
		
		return ext1.equals(ext2);
	}
	
	@Override
	public MIMEFileCell getCell(String name)
	{
		MIMEFileCell cell = null;
		List<MIMEFileCell> candidates = new ArrayList<MIMEFileCell>();
		for(String ext: ext2mt.keySet())
		{
			if(name.toLowerCase().endsWith(ext))
			{
				try
				{
					candidates.add( (MIMEFileCell) ext2mt.get(ext).getKNIMEClass().newInstance() );
				} 
				catch (InstantiationException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		Collections.sort(candidates, new Comparator<MIMEFileCell>(){
			@Override
			public int compare(MIMEFileCell x, MIMEFileCell y)
			{
				return x.getExtension().compareToIgnoreCase(y.getExtension());
			}}
		);
		
		if(candidates.size()>0)
			cell = candidates.get(0);
		
		return cell;
	}
	
	@Override
	public MIMEtype getMIMEtype(String name)
	{
		MIMEtype mt = null;
		List<MIMEtype> candidates = new ArrayList<MIMEtype>();
		for(String ext: ext2mt.keySet())
		{
			if(name.toLowerCase().endsWith(ext))
			{
				candidates.add(ext2mt.get(ext));
			}
		}
		
		Collections.sort(candidates, new Comparator<MIMEtype>(){
			@Override
			public int compare(MIMEtype x, MIMEtype y)
			{
				return x.getExt().compareToIgnoreCase(y.getExt());
			}}
		);
		
		if(candidates.size()>0)
			mt = candidates.get(0); 
		return mt;
	}
}
