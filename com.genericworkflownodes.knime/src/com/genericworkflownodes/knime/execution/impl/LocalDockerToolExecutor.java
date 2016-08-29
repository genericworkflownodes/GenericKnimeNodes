/**
 * Copyright (c) 2016, benjamin Schubert.
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.File;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.config.NoBinaryAvailableException;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutionFailedException;
import com.genericworkflownodes.knime.execution.impl.LocalToolExecutor.StreamGobbler;
import com.genericworkflownodes.util.Helper;
import com.genericworkflownodes.util.StringUtils;
import com.genericworkflownodes.knime.GenericNodesPlugin;

public class LocalDockerToolExecutor extends LocalToolExecutor implements IToolExecutor {
    
    private static final String DOCKER_CHECK = "docker-machine status ";
    private static final String DOCKER_START = "docker-machine start ";
    private static final String DOCKER_STOPPED = "Stopped";
    private static final String DOCKER_SET_ENV_WIN = "__DOCKER_PATH__docker-machine env __DOCKER_VM__";
    private static final String DOCKER_SET_ENV_MAC = "__DOCKER_PATH__docker-machine env __DOCKER_VM__";
 
    @Override
    public int execute() throws ToolExecutionFailedException {
        try {
            List<String> command = new ArrayList<String>();
            command.addAll(m_commands);
    
            // emit command
            LOGGER.debug("Executing: " + StringUtils.join(command, " "));
    
            // build process
            ProcessBuilder builder = new ProcessBuilder(command);
            setupProcessEnvironment(builder);
    
            if (m_workingDirectory != null) {
                builder.directory(m_workingDirectory);
            }
    
            // execute
            m_process = builder.start();
    
            // prepare capture of cerr/cout streams
            StreamGobbler stdOutGobbler = new StreamGobbler(
                    m_process.getInputStream());
            StreamGobbler stdErrGobbler = new StreamGobbler(
                    m_process.getErrorStream());
    
            // start separate threads to capture the cerr/cout streams
            stdErrGobbler.start();
            stdOutGobbler.start();
    
            // fetch return code
            m_returnCode = m_process.waitFor();
    
            // extract messages from stderr and stdout
            m_stdOut = stdOutGobbler.getContent();
            m_stdErr = stdErrGobbler.getContent();
        } catch (Exception e) {
            LOGGER.warn("Failed to execute tool " + m_executable.getName(), e);
            throw new ToolExecutionFailedException("Failed to execute tool "
                    + m_executable.getName(), e);
        }
        return m_returnCode;    
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
        super.prepareExecution(nodeConfiguration, pluginConfiguration);
        Map<String, String> env = super.getEnvironmentVariables();
        if(env.containsKey("PATH")){
            env.put("PATH", env.get("PATH")
                    +File.pathSeparator+GenericNodesPlugin.getDockerInstallationDir()
                    +File.pathSeparator+GenericNodesPlugin.getVmInstllationDir());  
        }else{
            env.put("PATH", GenericNodesPlugin.getDockerInstallationDir()
                    +File.pathSeparator+GenericNodesPlugin.getVmInstllationDir());
        }
        activateDockerMachine(pluginConfiguration);
    }
    
    /**
     * Checks if a docker-machine has to be activated (only necessary on Mac and Windows machines), 
     * if so which and 
     * @throws ToolExecutionFailedException 
     */
    private void activateDockerMachine(IPluginConfiguration pluginConfiguration) 
            throws ToolExecutionFailedException {
        String dockerPath = GenericNodesPlugin.getDockerInstallationDir()+File.separator;
        if((Helper.isMac()|| Helper.isWin()) && GenericNodesPlugin.isDockerToolBox()){
            if(executeDockerCommand(dockerPath+DOCKER_CHECK+pluginConfiguration.getDockerMachine()).equals(DOCKER_STOPPED)){
                executeDockerCommand(dockerPath+DOCKER_START+pluginConfiguration.getDockerMachine());
                }
            if(Helper.isMac()){
                setDockerMacEnv(executeDockerCommand(DOCKER_SET_ENV_MAC.replace("__DOCKER_VM__", 
                        pluginConfiguration.getDockerMachine()).replace("__DOCKER_PATH__", 
                                dockerPath)));
            }else{
                setDockerWinEnv(executeDockerCommand(DOCKER_SET_ENV_WIN.replace("__DOCKER_VM__", 
                        pluginConfiguration.getDockerMachine()).replace("__DOCKER_PATH__",
                                dockerPath)));
            }
        }
        
    }

    /**
     * Parses the output of docker-machine env __DOCKER_VM___ 
     * and adds the environmental variables to the env-dicktionary
     * works only for cmd.exe not for powershell
     * @param executeDockerCommand
     */
    //TODO: generalize to other windows supported shells
     private void setDockerWinEnv(final String executeDockerCommand) {
        Map<String, String> env = super.getEnvironmentVariables();
        for(String line: executeDockerCommand.split(System.getProperty("line.separator"))){
            if(line.startsWith("SET")){
                String[] envCommand = line.split("\\s+"); 
                String[] tmp = envCommand[1].split("=");
                env.put(tmp[0],tmp[1].replace("\"", ""));
            }
        }
    }

    /**
      * Parses the output of docker-machine env __DOCKER_VM___ 
      * and adds the environmental variables to the env-dicktionary
      * @param executeDockerCommand
      */
    private void setDockerMacEnv(final String dockerEnvOutput) {
        Map<String, String> env = super.getEnvironmentVariables();
        for(String line: dockerEnvOutput.split(System.getProperty("line.separator"))){
            if(!line.startsWith("#")){
               //TODO: generalize this to all supported shells
               String[] envCommand = line.split("\\s+"); 
               String[] tmp = envCommand[1].split("=");
               //this should work under bash
               env.put(tmp[0],tmp[1].replace("\"", ""));
             }
         }

        
    }

    /**
     * Execute docker-machine specific commands and return STDOUT as string
     * @param command docker-machine command
     * @param env environmental variables needed to execute docker-machine
     * @return STDOUT
     * @throws ToolExecutionFailedException
     */
    private String executeDockerCommand(String command) 
            throws ToolExecutionFailedException {
        
        String name = (getExecutable() == null) ? "docker-machine default" : getExecutable().getName();
        try{
            final ProcessBuilder pb = new ProcessBuilder(command.split("\\s+"));
            super.setupProcessEnvironment(pb);
            final Process p = pb.start();
            
            // prepare capture of cerr/cout streams
            StreamGobbler stdOutGobbler = new StreamGobbler(
                            p.getInputStream());
            StreamGobbler stdErrGobbler = new StreamGobbler(
                            p.getErrorStream());
    
            // start separate threads to capture the cerr/cout streams
            stdErrGobbler.start();
            stdOutGobbler.start();
    
            // fetch return code
            int returnCode = p.waitFor();
    
            // extract messages from stderr and stdout
            LinkedList<String> stdOut = stdOutGobbler.getContent();
            LinkedList<String> stdErr = stdErrGobbler.getContent();
            
            if(returnCode != 0){
                StringBuilder builder = new StringBuilder();
                for(String s: stdErr){
                    builder.append(s+ System.getProperty("line.separator"));
                }
                LOGGER.warn("Failed to execute tool " + name +" "+builder.toString(), null);
                throw new ToolExecutionFailedException("Failed to execute tool "
                            + name +" "+builder.toString(), null);
            }
            
            StringBuilder builder = new StringBuilder();
            for(String s: stdOut){
                builder.append(s+ System.getProperty("line.separator"));
            }
            return builder.toString().trim();
            
        } catch (Exception e) {
            LOGGER.warn("Failed to execute tool " + name, e);
            throw new ToolExecutionFailedException("Failed to execute tool "
                        + name, e);
        }
    }
    
    @Override
    protected void findExecutable(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration)
            throws NoBinaryAvailableException {
    }
    
}
