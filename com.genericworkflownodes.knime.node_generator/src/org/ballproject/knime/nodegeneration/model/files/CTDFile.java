package org.ballproject.knime.nodegeneration.model.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


import com.genericworkflownodes.knime.config.CTDNodeConfigurationReader;
import com.genericworkflownodes.knime.config.CTDNodeConfigurationReaderException;
import com.genericworkflownodes.knime.config.INodeConfiguration;

public class CTDFile extends File implements INodeConfigurationFile {

	private static final long serialVersionUID = -3472073355559210376L;

	private INodeConfiguration nodeConfiguration;

	public CTDFile(File file) throws FileNotFoundException,
			CTDNodeConfigurationReaderException {
		super(file.getPath());

		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		this.nodeConfiguration = reader.read(new FileInputStream(file));
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
