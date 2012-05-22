/**
 * 
 */
package com.genericworkflownodes.knime.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.config.CTDNodeConfigurationWriter;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;

import com.genericworkflownodes.knime.config.IPluginConfiguration;

/**
 * @author aiche
 * 
 */
public class BALLExecutor extends AbstractToolExecutor {

	private final static String PAR_SWITCH = "-par";
	private final static String PAR_FILE_NAME = "params.xml";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> prepareCall() throws Exception {
		List<String> commands = new ArrayList<String>();
		commands.add(getExecutable().getCanonicalPath());
		commands.add(PAR_SWITCH);
		commands.add(PAR_FILE_NAME);
		return commands;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void localPrepareExecution(INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration) throws Exception {

		CTDNodeConfigurationWriter ctdWriter = new CTDNodeConfigurationWriter(
				nodeConfiguration.getXML());
		ctdWriter.init(configStore);
		ctdWriter.writeCTD(new File(getWorkingDirectory(), PAR_FILE_NAME));
	}

}
