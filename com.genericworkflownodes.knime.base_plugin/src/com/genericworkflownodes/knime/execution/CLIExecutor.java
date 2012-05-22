/**
 * Copyright (c) 2012, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.execution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;
import org.ballproject.knime.base.config.PlainNodeConfigurationWriter;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.ListParameter;
import org.ballproject.knime.base.parameter.Parameter;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.config.IPluginConfiguration;

/**
 * The CLIExecutor handles the execution of command line tools that cannot
 * handle configuration files on their own and therefore must be called with a
 * fully configured command line.
 * 
 * @author aiche
 */
public class CLIExecutor extends AbstractToolExecutor {

	private INodeConfiguration nodeConfig;
	private NodeConfigurationStore configStore;

	@Override
	protected List<String> prepareCall() throws Exception {
		List<String> commands = new ArrayList<String>();
		commands.add(getExecutable().getCanonicalPath());

		for (CLIElement cliElement : nodeConfig.getCLI().getCLIElement()) {
			logger.info("CLIElement: " + cliElement.getText());

			if (!"".equals(cliElement.getText())
					&& cliElement.getMapping().size() == 0) {
				// simple fixed argument for the command line, no mapping to
				// params given
				commands.add(cliElement.getText());
			} else if (isMappedToBooleanParameter(cliElement)) {
				// it is mapped to bool
				handleBooleanParameter(commands, cliElement);
			} else {
				// TODO: handle optional parameters correctly -> if not set, do
				// not add to command line

				// extract mapped parameters
				List<Parameter<?>> mappedParameters = getMappedParameters(cliElement);

				// check if the mapped parameters fulfill all constraints
				validateParamters(mappedParameters);

				// it could be possible that we need to add multiple instances
				// of the parameters to the cli, therefore we are counting in
				// which iteration we are to always add the correct parameter

				// we know that we have at least one mapping element and that
				// (if multiple mappings are available) all have the same amount
				// of values
				for (int currentIdx = 0; currentIdx < configStore
						.getMultiParameterValue(
								cliElement.getMapping().get(0).getRefName())
						.size(); ++currentIdx) {

					// add the command prefix in each iteration
					if (!"".equals(cliElement.getText())) {
						commands.add(cliElement.getText());
					}

					// expand all available mappings to their current value
					for (CLIMapping cliMapping : cliElement.getMapping()) {
						commands.add(configStore.getMultiParameterValue(
								cliMapping.getRefName()).get(currentIdx));
					}
				}
			}
		}

		return commands;
	}

	/**
	 * Checks the following constraints that should be met by the
	 * mappedParamters.
	 * 
	 * <ul>
	 * <li>all or no list parameters</li>
	 * <li>equal size of multiple list parameters</li>
	 * </ul>
	 * 
	 * @param mappedParameters
	 *            The parameters that will be checked.
	 * @throws Exception
	 *             If one of the constrains is violated.
	 */
	private void validateParamters(List<Parameter<?>> mappedParameters)
			throws Exception {
		// we only impose constraints if we have more then one parameter
		if (mappedParameters.size() > 1) {
			checkAllOrNoneListParamters(mappedParameters);
			checkListSizes(mappedParameters);
		}
	}

	/**
	 * Checks if all list parameters have the same amount of values, stored. The
	 * method will abort if a non-list value is in the list of mapped
	 * parameters.
	 * 
	 * @param mappedParameters
	 *            The parameters that will be checked.
	 * @throws Exception
	 *             If the list parameters have different sizes.
	 */
	private void checkListSizes(List<Parameter<?>> mappedParameters)
			throws Exception {
		int size = -1;

		for (Parameter<?> p : mappedParameters) {
			if (p instanceof ListParameter) {
				ListParameter lp = (ListParameter) p;
				if (size != -1 && lp.getStrings().size() != size) {
					throw new Exception(
							"All list paramters need to have the same size, to correctly expand on the command line.");
				}
			} else {
				// we previously checked if all or none parameters are list
				// parameters, so we can assume here, that if we see at least
				// one non list, the rest is also non-list paramters
				break;
			}
		}
	}

	/**
	 * @param mappedParameters
	 * @throws Exception
	 */
	private void checkAllOrNoneListParamters(List<Parameter<?>> mappedParameters)
			throws Exception {
		boolean allList = false;
		boolean hasList = false;
		for (Parameter<?> p : mappedParameters) {
			if (p instanceof ListParameter) {
				allList &= true;
				hasList |= true;
			}
		}

		if (hasList && !allList) {
			throw new Exception(
					"If the mapping contains a list parameter, all parameter need to be list parameters.");
		}
	}

	/**
	 * Extracts all mapped parameters from the given CLIElement.
	 * 
	 * @note We do not support mixing of mapped parameters and ports in a single
	 *       element.
	 * 
	 * @param cliElement
	 * @return
	 */
	private List<Parameter<?>> getMappedParameters(CLIElement cliElement) {
		List<Parameter<?>> mappedParameters = new ArrayList<Parameter<?>>();

		for (CLIMapping mapping : cliElement.getMapping()) {
			mappedParameters.add(nodeConfig.getParameter(mapping.getRefName()));
		}
		return mappedParameters;
	}

	/**
	 * Returns true if the given CLIElement maps to a boolean parameter.
	 * 
	 * @param cliElement
	 * @return
	 */
	private boolean isMappedToBooleanParameter(CLIElement cliElement) {
		return cliElement.getMapping().size() == 1
				&& nodeConfig.getParameter(cliElement.getMapping().get(0)
						.getRefName()) instanceof BoolParameter;
	}

	/**
	 * Interpret boolean parameter on the command line -> if true, add text
	 * field, if false, do not add text field.
	 * 
	 * @param commands
	 *            The list of commands that will be executed later.
	 * @param cliElement
	 *            The currently interpreted clielement.
	 */
	private void handleBooleanParameter(List<String> commands,
			CLIElement cliElement) {
		if (((BoolParameter) nodeConfig.getParameter(cliElement.getMapping()
				.get(0).getRefName())).getValue()) {
			commands.add(cliElement.getText());
		}
	}

	@Override
	protected void localPrepareExecution(INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration) throws Exception {

		this.nodeConfig = nodeConfiguration;
		this.configStore = configStore;

		exportPlainConfiguration(configStore);
	}

	/**
	 * @param configStore
	 * @throws IOException
	 */
	private void exportPlainConfiguration(NodeConfigurationStore configStore)
			throws IOException {
		PlainNodeConfigurationWriter writer = new PlainNodeConfigurationWriter();
		configStore.setParameterValue("jobdir", getWorkingDirectory()
				.getAbsolutePath());
		writer.init(configStore);
		writer.write(getWorkingDirectory().getAbsolutePath() + File.separator
				+ "params.ini");
	}

}
