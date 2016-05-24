/**
 * Copyright (c) 2012, Benjamin Schubert.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.GenericNodesPlugin;
/**
 * Implements a Docker tool specific generation of a command line.
 * 
 * @author schubert
 */
public class DockerCommandGenerator extends CLICommandGenerator implements ICommandGenerator {

    protected static final NodeLogger logger = NodeLogger
            .getLogger(CLICommandGenerator.class);
   
    protected static final String DOCKER_COMMAND = "docker";
    protected static final String DOCKER_EXECUTION = "run";
    protected static final String DOCKER_MOUNT_COMMAND = "-v";
    protected static final String DOCKER_INTERNAL_MOUNT = "/var/shared/";
    protected static final String DOCKER_DIR_SEP = "/";
    
    //private INodeConfiguration nodeConfig;
    private IPluginConfiguration pluginConfig;
    
    public List<String> generateCommands(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration, File workingDirectory)
            throws Exception {

        // ease the passing around of variables
        nodeConfig = nodeConfiguration;
        pluginConfig = pluginConfiguration;
               
        // export the node configuration as plain text, for debugging and
        // logging
        exportPlainConfiguration(workingDirectory);

        List<String> commands;

        try {
            commands = processCLI();
        } catch (Exception e) {
            throw e;
        } finally {
            nodeConfig = null;
        }

        return commands;
    }
    
    
    /**
     * Converts the CLI part of the configuration to a list of commands that can
     * be send to the shell.
     * 
     * @return A configured list of commands.
     * @throws Exception
     *             Is thrown if the configuration values are invalid.
     */
    protected List<String> processCLI() throws Exception {
        List<String> commands = new ArrayList<String>();
        List<String> dockerCommands = new ArrayList<String>();
        Map<String, String> hostDockerMap = new HashMap<String, String>();
        dockerCommands.add(GenericNodesPlugin.getDockerInstallationDir()
                            +File.separator+DOCKER_COMMAND);
        dockerCommands.add(DOCKER_EXECUTION);
        commands.add(nodeConfig.getExecutablePath()+nodeConfig.getExecutableName());
        for (CLIElement cliElement : nodeConfig.getCLI().getCLIElement()) {
            logger.info("CLIElement: " + cliElement.getOptionIdentifier());

            if (!"".equals(cliElement.getOptionIdentifier())
                    && cliElement.getMapping().size() == 0) {
                // simple fixed argument for the command line, no mapping to
                // params given

                // to avoid problems with spaces in commands we split fixed
                // values
                String[] splitResult = cliElement.getOptionIdentifier().split(
                        " ");
                for (String splittedCommand : splitResult) {
                    commands.add(splittedCommand);
                }
            } else if (super.isMappedToBooleanParameter(cliElement)) {
                // it is mapped to bool
                super.handleBooleanParameter(commands, cliElement);
                
            } else {

                List<List<String>> extractedParameterValues = extractParamterValues(cliElement, dockerCommands,hostDockerMap);
                validateExtractedParameters(extractedParameterValues);

                // we only add those parameters to the command line if they
                // contain any values, this removes optional parameters if they
                // were not set
                if (extractedParameterValues.size() != 0) {
                    expandParameters(extractedParameterValues, cliElement,
                            commands);
                }
            }
        }
        try{
            String dockerContainer = pluginConfig.getToolProperty(nodeConfig.getName()).getProperty("dockerImage", null);
            dockerCommands.add(dockerContainer.replace("\"", ""));
            dockerCommands.addAll(commands);
            return dockerCommands;
        }catch (NullPointerException e){
            throw new Exception(String.format("Docker-Node %s has no image defined", nodeConfig.getName()));
        }
    }


    private List<List<String>> extractParamterValues(CLIElement cliElement,
            List<String> dockerCommands, Map<String, String> hostDockerMap) throws IOException {
        
        List<List<String>> extractedParameterValues = new ArrayList<List<String>>();
        
        for (CLIMapping cliMapping : cliElement.getMapping()) {
            if (nodeConfig.getParameterKeys().contains(
                    cliMapping.getReferenceName())) {

                Parameter<?> p = nodeConfig.getParameter(cliMapping
                        .getReferenceName());
                if (!p.isNull()) {
                    if (p instanceof ListParameter) {
                        ListParameter lp = (ListParameter) p;
                        if (lp.getStrings().size() > 0) {
                            extractedParameterValues.add(lp.getStrings());
                        }
                    } else if (p instanceof FileParameter){
                        extractedParameterValues.add(
                                handleFileParameter( ((FileParameter) p).getValue(), 
                                        dockerCommands, 
                                        hostDockerMap) );
                        
                    } else if (p instanceof FileListParameter) {
                        List<String> fl = ((FileListParameter) p).getValue();
                        if (fl.size() > 0){
                            for(String hostFile:fl){
                                extractedParameterValues.add(
                                        handleFileParameter(hostFile, 
                                                dockerCommands, 
                                                hostDockerMap)
                                        );
                            }
                        }
                    } else {
                        List<String> l = new ArrayList<String>();
                        l.add(p.getStringRep());
                        extractedParameterValues.add(l);
                    }
                }
            }
        }
        return extractedParameterValues;
    }

    /***
     * 
     * Process a file parameter by specifying the docker mount point and
     * altering the file paths to fit the internal docker path
     * 
     * @param hostFile string to file on host system
     * @param dockerCommands a list of specific docker commands
     * @param hostDockerMap a map of host paths to docker paths that have already been mapped
     * @return List of extracted commands
     * @throws IOException
     */
    private List<String> handleFileParameter(String hostFile,
            List<String> dockerCommands, Map<String, String> hostDockerMap) 
            throws IOException {
        
        String dockerMount;
        File fileParam = new File(hostFile);
        String hostPath = toUnixPath(fileParam.getParentFile().getCanonicalPath());
        
        if ( hostDockerMap.containsKey(hostPath)){
            dockerMount = hostDockerMap.get(hostPath);
            
        }else{
             dockerMount = DOCKER_INTERNAL_MOUNT
                    +dockerCommands.size()
                    +DOCKER_DIR_SEP;
             hostDockerMap.put(hostPath, dockerMount);
             dockerCommands.add(DOCKER_MOUNT_COMMAND);
             dockerCommands.add(hostPath+":"+dockerMount);
        }  

            
        List<String> l = new ArrayList<String>();
        l.add(dockerMount+fileParam.getName());
        
        return l;
    }

    /***
     * Normalizes paths to unix basted paths
     * 
     * Note1: Docker 1.9 assumes path used for mounting
     * are Unix-paths
     * 
     * Note2: For occurring problems see 
     * https://github.com/docker/docker/issues/12590#issuecomment-96767796
     * 
     * @param hostFile
     * @return
     */
    private String toUnixPath(final String hostFile) {
       if(!hostFile.startsWith("/")){
         String drive = hostFile.substring(0, 1);  
         return "/"+hostFile.replace("\\", 
                 "/").replace(":", "").replaceFirst(drive, 
                         drive.toLowerCase());
       }else{
         return hostFile;
       }
    }
    

}
