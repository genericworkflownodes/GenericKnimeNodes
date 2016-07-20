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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.config.NoBinaryAvailableException;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutionFailedException;
import com.genericworkflownodes.util.StringUtils;

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
     * and
     * 
     * org.knime.base.node.util.exttool.CommandExecution#StdErrCatchRunnable
     * 
     * @author aiche
     */
    protected static class StreamGobbler extends Thread {
        /**
         * The stream that is gobbled.
         */
        final InputStream m_is;

        /**
         * The string where the extracted messages are stored.
         */
        final LinkedList<String> m_buffer;

        StreamGobbler(final InputStream is) {
            m_is = is;
            m_buffer = new LinkedList<String>();
        }

        @Override
        public void run() {
            final InputStreamReader isr = new InputStreamReader(m_is);
            final BufferedReader br = new BufferedReader(isr);

            try {
                String line = null;
                while ((line = br.readLine()) != null) {
                    synchronized (m_buffer) {
                        m_buffer.add(line);
                    }
                }
            } catch (final IOException ioe) {
                LOGGER.error("LocalToolExecutor: Error in stream gobbler.",
                        ioe);
            } finally {
                try {
                    br.close();
                } catch (final IOException ioe) {
                    // then don't close it..
                }
            }
        }

        /**
         * Gives access to the gobbled string.
         * 
         * @return
         */
        public LinkedList<String> getContent() {
            return m_buffer;
        }
    }

    /**
     * NodeLogger used for this executor.
     */
    protected static final NodeLogger LOGGER = NodeLogger
            .getLogger(LocalToolExecutor.class);

    /**
     * The working directory where the process will be executed.
     */
    protected File m_workingDirectory;

    /**
     * The environment variables that will be passed to the running environment.
     */
    protected final Map<String, String> m_environmentVariables;

    /**
     * The return code of the process.
     */
    protected int m_returnCode;

    /**
     * The std-out of the executed process.
     */
    protected LinkedList<String> m_stdOut;

    /**
     * The std-err of the executed process.
     */
    protected LinkedList<String> m_stdErr;

    protected Process m_process;

    protected ICommandGenerator m_generator;
    
    protected List<CommandLineElement> m_commands;

    /**
     * The executable.
     */
    protected File m_executable;

    /**
     * @return the m_environmentVariables
     */
    public Map<String, String> getEnvironmentVariables() {
        return m_environmentVariables;
    }    
    
    /**
     * C'tor.
     */
    public LocalToolExecutor() {
        m_environmentVariables = new TreeMap<String, String>();
        m_returnCode = -1;
        m_stdErr = new LinkedList<String>();
        m_stdOut = new LinkedList<String>();
    }

    /**
     * Sets the working directory of the process. If the directory does not
     * exist or the @p path does not point to a directory (but a file), an
     * exception will be thrown.
     * 
     * @param directory
     *            The new working directory.
     * @throws IOException
     *             If the path does not exist or points to a file (and not a
     *             directory).
     */
    @Override
    public void setWorkingDirectory(final File directory) throws IOException {
        m_workingDirectory = directory;
        if (!m_workingDirectory.isDirectory() || !m_workingDirectory.exists()) {
            throw new IOException(directory + " is not a directory!");
        }
    }

    /**
     * Adds the environment variables included in
     * <code>newEnvironmentVariables</code> to the environment variables of the
     * tool.
     * 
     * @note If the given variables reference existing variables with the syntax
     *       <code>${VNAME}</code> they will be extended by the corresponding
     *       system values.
     * 
     * @note Existing values with equal keys will be overwritten.
     * 
     * @param newEnvironmentVariables
     *            The environment variables that will be added.
     */
    private void addEnvironmentVariables(
            final Map<String, String> newEnvironmentVariables) {
        m_environmentVariables.putAll(newEnvironmentVariables);
    }

    /**
     * Returns the return value of the process. If the tool didn't not run or is
     * not finished it is set to -1.
     * 
     * @return
     */
    @Override
    public int getReturnCode() {
        return m_returnCode;
    }

    /**
     * Returns the output generated by the tool as single string.
     * 
     * @return The ouput of the tool.
     */
    @Override
    public LinkedList<String> getToolOutput() {
        return m_stdOut;
    }

    /**
     * Kills the running process.
     */
    @Override
    public void kill() {
        m_process.destroy();
    }

    /**
     * Returns the working directory.
     * 
     * @return The working directory where the process will be executed.
     */
    public File getWorkingDirectory() {
        return m_workingDirectory;
    }
    
    protected void extractFromCommandLineElements(final Collection<CommandLineElement> elements, final Collection<String> commands) {
        for (final CommandLineElement element : elements) {
            commands.add(element.getStringRepresentation());
        }
    }

    @Override
    public int execute() throws ToolExecutionFailedException {

        try {
            final List<String> commands = new ArrayList<String>();
            commands.add(m_executable.getCanonicalPath());
            // this is a local execution, we need the values of all of the
            // command line elements
            // so we need the string representation of each element
            extractFromCommandLineElements(m_commands, commands);

            // emit command
            LOGGER.debug("Executing: " + StringUtils.join(commands, " "));

            // build process
            final ProcessBuilder builder = new ProcessBuilder(commands);
            setupProcessEnvironment(builder);

            if (m_workingDirectory != null) {
                builder.directory(m_workingDirectory);
            }

            // execute
            m_process = builder.start();

            // prepare capture of cerr/cout streams
            final StreamGobbler stdOutGobbler = new StreamGobbler(
                    m_process.getInputStream());
            final StreamGobbler stdErrGobbler = new StreamGobbler(
                    m_process.getErrorStream());

            // start separate threads to capture the cerr/cout streams
            stdErrGobbler.start();
            stdOutGobbler.start();

            // fetch return code
            m_returnCode = m_process.waitFor();

            // extract messages from stderr and stdout
            m_stdOut = stdOutGobbler.getContent();
            m_stdErr = stdErrGobbler.getContent();
        } catch (final Exception e) {
            LOGGER.warn("Failed to execute tool " + m_executable.getName(), e);
            throw new ToolExecutionFailedException(
                    "Failed to execute tool " + m_executable.getName(), e);
        }

        return m_returnCode;
    }

    /**
     * Expand environment variables in the given string referenced by
     * <code>${VNAME}</code>.
     * 
     * @param value
     *            The string where the variables should be replaced.
     * @return The string with replaced variables.
     */
    private String expandEnvironmentVariables(String value) {
        // matching pattern for ${VNAME}
        final Pattern variableNamePattern = Pattern.compile("\\$\\{([^}]+)\\}");

        // expand variables in value
        boolean found = true;
        while (found) {
            final Matcher m = variableNamePattern.matcher(value);
            found = m.find();
            if (found) {
                final String variableName = m.group(1);
                // extract current variable value
                String replacement = "";
                if (System.getenv(variableName) != null) {
                    replacement = System.getenv(variableName);
                }

                final Pattern specificVariablePattern = Pattern
                        .compile("\\$\\{" + variableName + "\\}");

                final Matcher replaceMatcher = specificVariablePattern
                        .matcher(value);
                value = replaceMatcher.replaceAll(replacement);
            }
        }

        return value;
    }

    /**
     * Initializes the environment variables of the given ProcessBuilder.
     * 
     * @note If the used binaries where not shipped with the plugin, this method
     *       will do nothing.
     * 
     * @param builder
     *            The builder that should be initialized.
     */
    protected void setupProcessEnvironment(ProcessBuilder builder) {
        for (String key : m_environmentVariables.keySet()) {
            String value = expandEnvironmentVariables(m_environmentVariables
                    .get(key));
            if(builder.environment().containsKey(key)){
                builder.environment().put(key, value
                        +File.pathSeparator+builder.environment().get(key));
            }else{
                builder.environment().put(key, value);
            }
        }
    }

    /**
     * Initialization method of the executor.
     * 
     * @param nodeConfiguration
     * @param pluginConfiguration
     */
    @Override
    public void prepareExecution(final INodeConfiguration nodeConfiguration,
            final IPluginConfiguration pluginConfiguration) throws Exception {
        findExecutable(nodeConfiguration, pluginConfiguration);

        addEnvironmentVariables(pluginConfiguration.getBinaryManager()
                .getProcessEnvironment(nodeConfiguration.getExecutableName()));
        m_commands = m_generator.generateCommands(nodeConfiguration,
                pluginConfiguration, m_workingDirectory);
    }

    /**
     * Tries to find the needed tool by searching in the
     * PluginPreferenceToolLocator and the plug-in package.
     * 
     * @throws NoBinaryAvailableException
     *             If no matching binary was found.
     */
    protected void findExecutable(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration)
            throws NoBinaryAvailableException {
        m_executable = pluginConfiguration.getBinaryManager().findBinary(
                nodeConfiguration.getExecutableName());
    }

    @Override
    public void setCommandGenerator(final ICommandGenerator generator) {
        m_generator = generator;
    }

    @Override
    public ICommandGenerator getCommandGenerator() {
        return m_generator;
    }

    @Override
    public LinkedList<String> getToolErrorOutput() {
        return m_stdErr;
    }
    
    public File getExecutable(){
        return m_executable; 
    }
}
