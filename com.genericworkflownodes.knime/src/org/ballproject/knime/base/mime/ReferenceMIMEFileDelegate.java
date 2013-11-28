package org.ballproject.knime.base.mime;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class ReferenceMIMEFileDelegate implements MIMEFileDelegate {
    private static final long serialVersionUID = 8947521317043895798L;
    private URI filename;

    @Override
    public byte[] getByteArrayReference() {
        String message = "Referenced file " + filename
                + " is too big to be displayed";
        return message.getBytes();
    }

    @Override
    public boolean isEqual(MIMEFileDelegate del) {
        if (del instanceof ReferenceMIMEFileDelegate) {
            return false;
        }
        ReferenceMIMEFileDelegate del_ = (ReferenceMIMEFileDelegate) del;
        return del_.filename.equals(filename);
    }

    @Override
    public int getHash() {
        return filename.hashCode();
    }

    @Override
    public void read(File file) throws IOException {
        filename = file.toURI();
    }

    @Override
    public void write(String filename) throws IOException {
        // NOP
    }

    @Override
    public File writeTemp(String directory) throws IOException {
        return new File(filename);
    }

}
