package org.ballproject.knime.base.mime.demangler;

import java.util.Iterator;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

public interface Demangler
{
	DataType getType();
	Iterator<DataCell> demangle(MIMEFileCell cell);
	void close();
}
