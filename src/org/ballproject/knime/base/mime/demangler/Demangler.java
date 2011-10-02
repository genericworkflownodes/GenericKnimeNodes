package org.ballproject.knime.base.mime.demangler;

import java.io.Serializable;
import java.util.Iterator;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

public interface Demangler extends Serializable
{
	DataType getSourceType();
	DataType getTargetType();
	Iterator<DataCell> demangle(MIMEFileCell cell);
	MIMEFileCell mangle(Iterator<DataCell> iter);
}
