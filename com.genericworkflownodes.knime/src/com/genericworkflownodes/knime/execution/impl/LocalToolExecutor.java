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
package com.genericworkflownodes.knime.execution.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.ui.PlatformUI;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;

/**
 * The LocalToolExecutor handles the basic tasks associated with the execution
 * of a tool on the command line.
 * 
 * @author aiche
 */
public class LocalToolExecutor implements IToolExecutor {

	/**
	 * Captures the stderr/stdout stream of the running process to avoid
	 * deadlocks.
	 * 
	 * Inspired by
	 * http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=4.
	 * 
	 * @author aiche
	 */
	private static class StreamGobbler extends Thread {
		/**
		 * The stream that is gobbled.
		 */
		InputStream is;

		/**
		 * The string where the extracted messages are stored.
		 */
		StringBuffer target;

		private static final String LINE_SEPARATOR = System
				.getProperty("line.separator");

		StreamGobbler(InputStream is) {
			this.is = is;
			target = new StringBuffer();
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					target.append(line + LINE_SEPARATOR);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		/**
		 * Gives access to the gobbled string.
		 * 
		 * @return
		 */
		public String getContent() {
			return target.toString();
		}
	}

	/**
	 * NodeLogger used for this executor.
	 */
	protected static final NodeLogger logger = NodeLogger
			.getLogger(LocalToolExecutor.class);

	/**
	 * The working directory where the process will be executed.
	 */
	private File workingDirectory;

	/**
	 * The environment variables that will be passed to the running environment.
	 */
	private final Map<String, String> environmentVariables;

	/**
	 * The return code of the process.
	 */
	private int returnCode;

	/**
	 * The std-out of the executed process.
	 */
	private String stdOut;

	/**
	 * The std-err of the executed process.
	 */
	private String stdErr;

	private Process process;

	private ICommandGenerator generator;

	private File executable;

	private List<String> commands;

	public LocalToolExecutor() {
		environmentVariables = new TreeMap<String, String>();
		returnCode = -1;
		executable = null;
		workingDirectory = null;

		stdErr = "";
		stdOut = "";
	}

	/**
	 * Sets the working directory of the process. If the directory does not
	 * exist or the @p path does not point to a directory (but a file), an
	 * exception will be thrown.
	 * 
	 * @param directory
	 *            The new working directory.
	 * @throws Exception
	 *             If the path does not exist or points to a file (and not a
	 *             directory).
	 */
	@Override
	public void setWorkingDirectory(File directory) throws Exception {
		workingDirectory = directory;
		if (!workingDirectory.isDirectory() || !workingDirectory.exists()) {
			throw new Exception(directory + " is not a directory!");
		}
	}

	/**
	 * Adds the environment variables included in @p newEnvironmentVariables to
	 * the environment variables of the tool.
	 * 
	 * @note If the environment variable is a path (e.g., PATH or
	 *       LD_LIBRARY_PATH) the environment variable will be extended and not
	 *       overwritten (i.e.,
	 *       LD_LIBRARY_PATH=<specified-value>:$LD_LIBRARY_PATH).
	 * 
	 * @note Existing values with equal keys will be overwritten.
	 * 
	 * @param newEnvironmentVariables
	 *            The environment variables that will be added.
	 */
	private void addEnvironmentVariables(
			Map<String, String> newEnvironmentVariables) {
		environmentVariables.putAll(newEnvironmentVariables);
	}

	/**
	 * Returns the return value of the process. If the tool didn't not run or is
	 * not finished it is set to -1.
	 * 
	 * @return
	 */
	@Override
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * Returns the output generated by the tool as single string.
	 * 
	 * @return The ouput of the tool.
	 */
	@Override
	public String getToolOutput() {
		return stdOut;
	}

	/**
	 * Kills the running process.
	 */
	@Override
	public void kill() {
		process.destroy();
	}

	/**
	 * Returns the working directory.
	 * 
	 * @return
	 */
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * The execute method used by derived classes to execute their command.
	 * 
	 * Calling this method will block until completion (successful or failed) of
	 * the command.
	 * 
	 * @return The return value of the executed process.
	 * @throws Exception
	 */
	@Override
	public int execute() throws Exception {

		try {
			List<String> command = new ArrayList<String>();
			command.add(executable.getCanonicalPath());
			command.addAll(commands);

			// build process
			ProcessBuilder builder = new ProcessBuilder(command);

			setupProcessEnvironment(builder);

			if (workingDirectory != null) {
				builder.directory(workingDirectory);
			}

			// execute
			process = builder.start();

			// prepare capture of cerr/cout streams
			StreamGobbler stdOutGobbler = new StreamGobbler(
					process.getInputStream());
			StreamGobbler stdErrGobbler = new StreamGobbler(
					process.getErrorStream());

			// start separate threads to capture the cerr/cout streams
			stdErrGobbler.start();
			stdOutGobbler.start();

			// fetch return code
			returnCode = process.waitFor();

			// extract messages from stderr and stdout
			stdOut = stdOutGobbler.getContent();
			stdErr = stdErrGobbler.getContent();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return returnCode;
	}

	/**
	 * @param builder
	 */
	private void setupProcessEnvironment(ProcessBuilder builder) {
		for (String key : environmentVariables.keySet()) {
			String value = environmentVariables.get(key);
			builder.environment().put(key, value);
		}
	}

	/**
	 * Initialization method of the executor.
	 * 
	 * @param nodeConfiguration
	 * @param pluginConfiguration
	 */
	@Override
	public void prepareExecution(INodeConfiguration nodeConfiguration,
			IPluginConfiguration pluginConfiguration) throws Exception {
		findExecutable(nodeConfiguration, pluginConfiguration);
		addEnvironmentVariables(pluginConfiguration.getEnvironmentVariables());

		commands = generator.generateCommands(nodeConfiguration,
				pluginConfiguration, workingDirectory);
	}

	/**
	 * Tries to find the needed tool by searching in the
	 * PluginPreferenceToolLocator and the plugin package.
	 * 
	 * @return
	 * @throws Exception
	 */
	private void findExecutable(INodeConfiguration nodeConfiguration,
			IPluginConfiguration pluginConfiguration) throws Exception {

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator == null) {
			throw new Exception("Could not find matching ToolLocatorService.");
		}

		executable = toolLocator.getToolPath(new ExternalTool(
				pluginConfiguration.getPluginId(), nodeConfiguration.getName(),
				nodeConfiguration.getExecutableName()));

		if (executable == null) {
			throw new Exception("Neither externally configured nor shipped "
					+ "binaries exist for this node. Aborting execution.");
		}
	}

	@Override
	public void setCommandGenerator(ICommandGenerator generator) {
		this.generator = generator;
	}

	@Override
	public String getToolErrorOutput() {
		return stdErr;
	}
}
