package org.ballproject.knime.nodegeneration.model.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ballproject.knime.base.config.CTDFileNodeConfigurationReader;
import org.ballproject.knime.base.config.CTDNodeConfigurationReaderException;
import org.ballproject.knime.base.config.INodeConfiguration;

public class CTDFile extends File implements INodeConfigurationFile {

	private static final long serialVersionUID = -3472073355559210376L;

	private INodeConfiguration nodeConfiguration;

	public CTDFile(File file) throws FileNotFoundException,
			CTDNodeConfigurationReaderException {
		super(file.getPath());

		CTDFileNodeConfigurationReader reader = new CTDFileNodeConfigurationReader();
		this.nodeConfiguration = reader.read(new FileInputStream(file));
	}

	@Override
	public INodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}

}
