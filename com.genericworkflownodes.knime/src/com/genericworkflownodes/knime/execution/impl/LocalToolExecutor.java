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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.knime.core.util.ThreadUtils.ThreadWithContext;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
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
    
    private class StreamGobbler extends ThreadWithContext
    {
        InputStream is;
        String type;
        
        @Override
        protected void runWithContext()
        {
            try
            {
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
                Object streamtype;
                if (type == "OUT")
                {
                    streamtype = enumElements[0];
                }
                else
                {
                    streamtype = enumElements[1];
                }
                Constructor<ViewUpdateNotice> ctor = ViewUpdateNotice.class.getDeclaredConstructor(enumClass);
                ctor.setAccessible(true);

                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                if (type == "OUT")
                {
                    while ( (line = br.readLine()) != null)
                    {
                        // TODO this is rather inefficient. Unfortunately adding just a line is not possible with the API
                        // However, I think we need to set this member. 
                        m_model.setStdOut(m_stdOut);
                        m_stdOut.add(line);
                        ViewUpdateNotice v = ctor.newInstance(streamtype);
                        v.setNewLine(line);
                        m_model.update(new Observable(),v);
                    }
                }
                else
                {
                    while ( (line = br.readLine()) != null)
                    {
                        // TODO this is rather inefficient. Unfortunately adding just a line is not possible with the API
                        // However, I think we need to set this member. 
                        m_model.setStdErr(m_stdErr);
                        m_stdErr.add(line);
                        ViewUpdateNotice v = ctor.newInstance(streamtype);
                        v.setNewLine(line);
                        m_model.update(new Observable(),v);
                    }
                }
            }
            catch (IOException ioe)
            {
               ioe.printStackTrace();  
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        StreamGobbler(InputStream is, String type)
        {
            this.is = is;
            this.type = type;
        }
    }
    
    public class MyTailerOutListener extends TailerListenerAdapter {
        private Constructor<ViewUpdateNotice> ctor;
        private Object streamtype;
        public MyTailerOutListener() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
            //HACK since org.knime.base.node.util.exttool.ViewUpdateNotice.ViewType is a private enum
            Class<?> enumClass = Class.forName("org.knime.base.node.util.exttool.ViewUpdateNotice$ViewType");
            Object[] enumElements = enumClass.getEnumConstants();
            streamtype = enumElements[0]; // out stream
            ctor = ViewUpdateNotice.class.getDeclaredConstructor(enumClass);
            ctor.setAccessible(true);
        }
        public void handle(String line) {
            
            m_stdOut.add(line);
            m_model.setStdOut(m_stdOut);
            ViewUpdateNotice v;
            try {
                v = ctor.newInstance(streamtype);
                v.setNewLine(line);
                m_model.update(new Observable(),v);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public class MyTailerErrListener extends TailerListenerAdapter {
        private Constructor<ViewUpdateNotice> ctor;
        private Object streamtype;
        public MyTailerErrListener() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
            //HACK since org.knime.base.node.util.exttool.ViewUpdateNotice.ViewType is a private enum
            Class<?> enumClass = Class.forName("org.knime.base.node.util.exttool.ViewUpdateNotice$ViewType");
            Object[] enumElements = enumClass.getEnumConstants();
            streamtype = enumElements[1]; // err stream
            ctor = ViewUpdateNotice.class.getDeclaredConstructor(enumClass);
            ctor.setAccessible(true);
        }
        public void handle(String line) {
            
            m_stdErr.add(line);
            m_model.setStdErr(m_stdErr);
            ViewUpdateNotice v;
            try {
                v = ctor.newInstance(streamtype);
                v.setNewLine(line);
                m_model.update(new Observable(),v);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
     * The NodeModel from which this Executor was generated
     * TODO This is more a hack. I think we should use the
     * Observer pattern and synchronize the fields stdErr and stdOut
     * with the ExtToolOutputNodeModel that the GKNModel extends.
     */
    private GenericKnimeNodeModel m_model;

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

            File logFile;
            File errLogFile;
            if (m_workingDirectory != null) {
                builder.directory(m_workingDirectory);
                logFile = new File(m_workingDirectory,"lastLog.txt");
                errLogFile = new File(m_workingDirectory,"lastErrLog.txt");
            } else {
                logFile = File.createTempFile("GKN-", ".log.tmp");
                errLogFile = File.createTempFile("GKN-", "err.log.tmp");
            }
            
            LOGGER.debug("Created log file: " + logFile.getAbsolutePath());
            LOGGER.debug("Created errlog file: " + errLogFile.getAbsolutePath());
            
            builder.redirectOutput(logFile);
            builder.redirectError(errLogFile);
            MyTailerOutListener listener = new MyTailerOutListener();
            MyTailerErrListener errlistener = new MyTailerErrListener();
            Tailer tailer = new Tailer(logFile, listener, 250);
            Tailer errtailer = new Tailer(errLogFile, errlistener, 250);
            Thread thread = ThreadUtils.threadWithContext(tailer);
            Thread errthread = ThreadUtils.threadWithContext(errtailer);
            thread.setDaemon(true); // optional
            errthread.setDaemon(true); // optional
            thread.start();
            errthread.start();
            
            // execute
            
            m_process = builder.start();
            
            /* Old streamgobbler implementation that had problems on windows 
             * (probably the stream buffer went full and it did not read on time
             * and then you got a deadlock)
            StreamGobbler stdout = new StreamGobbler(m_process.getInputStream(), "OUT");
            StreamGobbler stderr = new StreamGobbler(m_process.getInputStream(), "ERR");
            stdout.start();
            stderr.start();*/

            // fetch return code
            m_returnCode = m_process.waitFor();
            m_process.destroy();
            Thread.sleep(300); //wait at least a bit more than the wait in the Tailers (in case the tool fails immediately)
            tailer.stop();
            errtailer.stop();
            thread.join();
            errthread.join();
            try {
              logFile.delete();
              errLogFile.delete();
            } catch (final Exception e){
                LOGGER.warn("Warning: Could not delete log files: " + logFile.getAbsolutePath() + " or " + errLogFile.getAbsolutePath());
            }
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
    public void setModel(GenericKnimeNodeModel model) {
          m_model = model;
    }
}
