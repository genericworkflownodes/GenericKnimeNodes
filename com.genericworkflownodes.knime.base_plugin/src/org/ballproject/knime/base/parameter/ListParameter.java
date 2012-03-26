package org.ballproject.knime.base.parameter;

import java.util.List;

/**
 * The ListParameter interface is implemented by {@link Parameter} classes that store lists of plain datatypes. 
 * 
 * @author roettig
 *
 */
public interface ListParameter
{
	/**
	 * returns a list of string representations of the stored values.
	 * 
	 * This is mainly for display purposes within GUIs and console.
	 * 
	 * @return list of strings
	 */
	List<String> getStrings();
	
	/**
	 * fill the {@link Parameter} object from a list of strings.
	 * 
	 * @param values list of strings
	 */
	void fillFromStrings(String[] values);
}
