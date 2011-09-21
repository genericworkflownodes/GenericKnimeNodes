package org.ballproject.knime.base.mime.demangler;

import java.util.Iterator;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

public interface Demangler
{
	DataType getSourceType();
	DataType getTargetType();
	Iterator<DataCell> demangle(MIMEFileCell cell);
	MIMEFileCell mangle(Iterator<DataCell> iter);
	void close();
}
