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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.config.PlainNodeConfigurationWriter;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Generates the command line call given a {@link INodeConfiguration} containing
 * CLI information.
 * 
 * @author aiche
 */
public class CLICommandGenerator implements ICommandGenerator {

    protected static final NodeLogger logger = NodeLogger
            .getLogger(CLICommandGenerator.class);

    private INodeConfiguration nodeConfig;

    @Override
    public List<String> generateCommands(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration, File workingDirectory)
            throws Exception {

        // ease the passing around of variables
        nodeConfig = nodeConfiguration;

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
    private List<String> processCLI() throws Exception {
        List<String> commands = new ArrayList<String>();

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
            } else if (isMappedToBooleanParameter(cliElement)) {
                // it is mapped to bool
                handleBooleanParameter(commands, cliElement);
            } else {

                List<List<String>> extractedParameterValues = extractParamterValues(cliElement);
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
        return commands;
    }

    /**
     * Add the extracted parameter values to the command line.
     * 
     * @note This method requires, that all contained lists have the same size.
     * 
     * @param extractedParameterValues
     * @param cliElement
     * @param commands
     */
    private void expandParameters(List<List<String>> extractedParameterValues,
            CLIElement cliElement, List<String> commands) {
        // since we assume that the outer list is not empty this will always
        // work
        int listSize = extractedParameterValues.get(0).size();

        // in each iteration we expand the i-th element of each internal list to
        // the command line prefixed with the cliElement optionIdentifier (if it
        // has one)
        for (int i = 0; i < listSize; ++i) {
            // add the command prefix in each iteration
            if (!"".equals(cliElement.getOptionIdentifier())) {
                commands.add(cliElement.getOptionIdentifier());
            }

            // add the actual values
            for (List<String> innerList : extractedParameterValues) {
                commands.add(innerList.get(i));
            }
        }
    }

    /**
     * Given the provided list of parameter values this method should ensure
     * that all mapped lists have the same length.
     * 
     * @param extractedParameterValues
     * @throws Exception
     *             If not all contained lists have the same size.
     */
    private void validateExtractedParameters(
            List<List<String>> extractedParameterValues) throws Exception {

        int currentSize = -1;
        for (List<String> currentList : extractedParameterValues) {
            if (currentSize != -1 && currentSize != currentList.size()) {
                throw new Exception(
                        "All mapped value lists must have the same size.");
            }
            currentSize = currentList.size();
        }
    }

    /**
     * Given the cliElement create a list containing for each mapped parameter a
     * list with the mapped values.
     * 
     * @param cliElement
     *            The current cliElement.
     * @return
     */
    private List<List<String>> extractParamterValues(CLIElement cliElement) {

        List<List<String>> extractedParameterValues = new ArrayList<List<String>>();

        for (CLIMapping cliMapping : cliElement.getMapping()) {
            if (nodeConfig.getParameterKeys().contains(
                    cliMapping.getReferenceName())) {

                Parameter<?> p = nodeConfig.getParameter(cliMapping
                        .getReferenceName());
                if (!p.isNull()) {
                    if (p instanceof ListParameter) {
                        extractedParameterValues.add(((ListParameter) p)
                                .getStrings());
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

    /**
     * Returns true if the given CLIElement maps to a boolean parameter.
     * 
     * @param cliElement
     * @return
     */
    private boolean isMappedToBooleanParameter(CLIElement cliElement) {
        return cliElement.getMapping().size() == 1
                && nodeConfig.getParameter(cliElement.getMapping().get(0)
                        .getReferenceName()) instanceof BoolParameter;
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
                .get(0).getReferenceName())).getValue()) {
            commands.add(cliElement.getOptionIdentifier());
        }
    }

    /**
     * Exports all configuration settings to the working directory.
     * 
     * @param configStore
     * @throws IOException
     */
    private void exportPlainConfiguration(File workingDirectory)
            throws IOException {
        PlainNodeConfigurationWriter writer = new PlainNodeConfigurationWriter();
        writer.init(nodeConfig);
        writer.write(workingDirectory.getAbsolutePath() + File.separator
                + "params.ini");
    }
}
