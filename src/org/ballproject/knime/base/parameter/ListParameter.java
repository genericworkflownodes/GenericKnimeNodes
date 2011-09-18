package org.ballproject.knime.base.parameter;

import java.util.List;

public interface ListParameter
{
	List<String> getStrings();
	void fillFromStrings(String[] values);
}
