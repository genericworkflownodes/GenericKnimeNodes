package org.ballproject.knime.base.mime;

import org.knime.core.data.DataValue;

public interface MIMEFileValue extends DataValue
{
	byte[] getData();
	void   setData(byte[] data);
}
