package org.ballproject.knime.base.mime;

public interface MIMEtypeRegistry
{
	void addResolver(MIMEtypeRegistry resolver);
	MIMEFileCell getCell(String name) throws Exception;
}
