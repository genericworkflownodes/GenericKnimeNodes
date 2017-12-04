package com.genericworkflownodes.knime.nodegeneration.model.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.xml.sax.SAXException;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.config.reader.InvalidCTDFileException;

public class CTDFile extends File implements INodeConfigurationFile {

    private static final long serialVersionUID = -3472073355559210376L;

    private INodeConfiguration nodeConfiguration;

    public CTDFile(File file) throws InvalidCTDFileException, FileNotFoundException {
        super(file.getPath());

        try {
            CTDConfigurationReader reader = new CTDConfigurationReader();
            nodeConfiguration = reader.read(new FileInputStream(file));
        } catch (InvalidCTDFileException e) {
            System.err.println(e.getMessage());
            System.err.println("Error while reading file: "
                    + file.getAbsolutePath());
            if (e.getCause() != null) {
            	System.err.println("Caused by:");
            	System.err.println(e.getCause().getMessage());
            }
          throw e;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.err.println("CTD file not found: "
                    + file.getAbsolutePath());
            throw e;
        }
    }

    @Override
    public INodeConfiguration getNodeConfiguration() {
        return nodeConfiguration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((nodeConfiguration == null) ? 0 : nodeConfiguration
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CTDFile other = (CTDFile) obj;
        if (nodeConfiguration == null) {
            if (other.nodeConfiguration != null) {
                return false;
            }
        } else if (!nodeConfiguration.equals(other.nodeConfiguration)) {
            return false;
        }
        return true;
    }

}
