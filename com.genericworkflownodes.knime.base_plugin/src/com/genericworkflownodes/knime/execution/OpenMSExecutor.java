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
public class OpenMSExecutor extends AbstractToolExecutor {

	private final static String INI_SWITCH = "-ini";
	private final static String INI_FILE_NAME = "params.ini";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> prepareCall() throws Exception {
		List<String> commands = new ArrayList<String>();
		commands.add(getExecutable().getCanonicalPath());
		commands.add(INI_SWITCH);
		commands.add(INI_FILE_NAME);
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
		ctdWriter.writeParametersOnly(new File(getWorkingDirectory(),
				INI_FILE_NAME));
	}
}
