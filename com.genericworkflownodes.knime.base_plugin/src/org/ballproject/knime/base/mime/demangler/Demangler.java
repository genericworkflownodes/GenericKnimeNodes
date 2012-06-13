package org.ballproject.knime.base.mime.demangler;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.url.MIMEType;

public interface Demangler extends Serializable {
	MIMEType getMIMEType();

	DataType getTargetType();

	Iterator<DataCell> demangle(URI file);

	MIMEFileCell mangle(Iterator<DataCell> iter);
}
