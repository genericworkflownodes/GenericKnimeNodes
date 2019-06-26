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
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knime.core.node.NodeLogger;
import org.knime.core.util.ThreadUtils;
import org.knime.base.node.util.exttool.*;
import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.config.NoBinaryAvailableException;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutionFailedException;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;
import com.genericworkflownodes.util.StringUtils;

/**
 * The LocalToolExecutor handles the basic tasks associated with the execution
 * of a tool on the command line.
 * 
 * @author aiche
 */
public class LocalToolExecutor implements IToolExecutor {

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
     * The NodeModel from which this Executor was generated
     * TODO This is more a hack. I think we should use the
     * Observer pattern and synchronize the fields stdErr and stdOut
     * with the model.
     */
    private ExtToolOutputNodeModel m_model;

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
     * Returns the command used to execute the tool as a list of strings.
     * 
     * @return The command arguments to execute the tool.
     */
    @Override
    public List<String> getCommand() {
        final List<String> commands = new LinkedList<String>();
        for (final CommandLineElement commandLineElement : m_commands) {
            commands.add(commandLineElement.getStringRepresentation());
        }
        return commands;
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
     * @return The output of the tool.
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
            
            BufferedReader stdout;
            BufferedReader stderr;

            // execute
            m_process = builder.start();
            stdout = new BufferedReader(new InputStreamReader(m_process.getInputStream(), "UTF-8"));
            stderr = new BufferedReader(new InputStreamReader(m_process.getErrorStream(), "UTF-8"));
     
            boolean done = false;
            boolean stdoutclosed = false;
            boolean stderrclosed = false;
            
            //HACK since org.knime.base.node.util.exttool.ViewUpdateNotice.ViewType is a private enum
            Class<?> enumClass = Class.forName("org.knime.base.node.util.exttool.ViewUpdateNotice$ViewType");
            //Constructor<?> ctor = enumClass.getDeclaredConstructor();

            //Enum enumInstance = (Enum) ctor.newInstance();
            //Class e = enumInstance.getClass();
            /*Class<?> noticeclass = Class.forName("org.knime.base.node.util.exttool.ViewUpdateNotice");
            Field f = noticeclass.getDeclaredField("$ViewType");
            f.setAccessible(true);
            Class e =  (Class) f.get(this);*/
            Object[] enumElements = enumClass.getEnumConstants();
            Object stdouttype = enumElements[0];
            Object stderrtype = enumElements[1];
            Constructor<ViewUpdateNotice> ctor = ViewUpdateNotice.class.getDeclaredConstructor(enumClass);
            ctor.setAccessible(true);
            
            while (!done){
                boolean readSomething = false;
                // read from the process's standard output
                if (!stdoutclosed && stdout.ready()){
                    readSomething = true;
                    String read = stdout.readLine();
                    if (read == null){
                        stdoutclosed = true;
                    } else {
                        m_stdOut.add(read);
                        ViewUpdateNotice v = ctor.newInstance(stdouttype);
                        v.setNewLine(read);
                        ((GenericKnimeNodeModel) m_model).setStdOut(m_stdOut);
                        m_model.update(new Observable(),v);
                    }
                }
                // read from the process's standard error
                if (!stderrclosed && stderr.ready()){
                    readSomething = true;
                    String read = stderr.readLine();
                    if (read == null){
                        stderrclosed = true;
                    } else {
                        m_stdErr.add(read);
                        ViewUpdateNotice v = ctor.newInstance(stderrtype);
                        v.setNewLine(read);
                        ((GenericKnimeNodeModel) m_model).setStdErr(m_stdOut);
                        m_model.update(new Observable(),v);
                    }
                }
                // Check the exit status only we haven't read anything,
                // if something has been read, the process is obviously not dead yet.
                if (!readSomething){
                    try {
                        m_process.exitValue();
                        done = true;
                    } catch (IllegalThreadStateException itx){
                        // Exit status not ready yet.
                        // Give the process a little breathing room.
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ix){
                            m_process.destroy();
                            throw new IOException("Interrupted - processes killed");
                        }
                    }
                }
            }

            stdout.close();
            stderr.close();
            // fetch return code
            m_returnCode = m_process.waitFor();

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
    protected String expandEnvironmentVariables(String value) {
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
    public void prepareExecution(final INodeConfiguration nodeConfiguration,
            final IPluginConfiguration pluginConfiguration) throws Exception {
        findExecutable(nodeConfiguration, pluginConfiguration);
        Map<String, String> nodeEnv = pluginConfiguration.getBinaryManager()
        .getProcessEnvironment(nodeConfiguration.getExecutableName());
        String pathWithJava = "";
        if (nodeEnv.containsKey("PATH")) {
            pathWithJava = nodeEnv.get("PATH") + File.pathSeparator;
        }
        pathWithJava += System.getProperty("java.home") + File.separator + "bin";
        nodeEnv.put("PATH", pathWithJava);
        
        pathWithJava = "";
        if (System.getProperty("os.name").startsWith("Windows")) {
            if (nodeEnv.containsKey("Path")) {
                pathWithJava = nodeEnv.get("Path") + File.pathSeparator;
            }
            pathWithJava += System.getProperty("java.home")+File.separator+"bin";
            nodeEnv.put("Path", pathWithJava);
        }
        
        addEnvironmentVariables(nodeEnv);
        
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
    
    public File getExecutable() {
        return m_executable; 
    }

    @Override
    public void setModel(ExtToolOutputNodeModel model) {
          m_model = model;
    }
}
