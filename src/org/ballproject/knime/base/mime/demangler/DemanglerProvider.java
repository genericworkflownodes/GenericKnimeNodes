package org.ballproject.knime.base.mime.demangler;

import java.util.List;

public interface DemanglerProvider
{
	List<Demangler> getDemanglers();
}
