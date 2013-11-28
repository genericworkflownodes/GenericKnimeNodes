package org.ballproject.knime.base.mime.mocks;

import java.util.Arrays;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.knime.core.data.DataCell;

public class MockMIMEFileCell extends MIMEFileCell implements MockMIMEFileValue {
    private static final long serialVersionUID = 285473161299662606L;

    @Override
    public String getExtension() {
        return "unk";
    }

    @Override
    protected boolean equalsDataCell(DataCell dc) {
        MIMEFileCell mfc = (MIMEFileCell) dc;
        return Arrays.equals(this.getData(), mfc.getData());
    }

    @Override
    public String toString() {
        return "UNKMimeFileCell";
    }
}
