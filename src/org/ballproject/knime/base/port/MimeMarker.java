package org.ballproject.knime.base.port;

import java.io.Serializable;

public interface MimeMarker
{
	MIMEFileDelegate getDelegate();
	String getExtension();
}
