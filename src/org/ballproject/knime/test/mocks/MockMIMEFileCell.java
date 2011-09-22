package org.ballproject.knime.test.mocks;

import java.util.Arrays;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;

public class MockMIMEFileCell extends MIMEFileCell implements MockMIMEFileValue
{
	@Override
	public String getExtension()
	{
		return "unk";
	}

	@Override
	protected boolean equalsDataCell(DataCell dc)
	{
		MIMEFileCell mfc = (MIMEFileCell) dc;
		return Arrays.equals(getData(), mfc.getData());
	}

	@Override
	public String toString()
	{
		return "UNKMimeFileCell";
	}

}
