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
import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.commandline.impl.CommandLineFile;
import com.genericworkflownodes.knime.commandline.impl.CommandLineOptionIdentifier;
import com.genericworkflownodes.knime.commandline.impl.CommandLineParameter;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.PlainNodeConfigurationWriter;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
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

    protected INodeConfiguration nodeConfig;

    @Override
    public List<CommandLineElement> extractParameters(
            INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration, File workingDirectory)
            throws Exception {

        // ease the passing around of variables
        nodeConfig = nodeConfiguration;

        // export the node configuration as plain text, for debugging and
        // logging
        exportPlainConfiguration(workingDirectory);

        List<CommandLineElement> parameters;

        try {
            parameters = processCLI();
        } catch (Exception e) {
            throw e;
        } finally {
            nodeConfig = null;
        }

        return parameters;
    }

    /**
     * Converts the CLI part of the configuration to a list of commands that can
     * be send to the shell.
     * 
     * @return A configured list of commands.
     * @throws Exception
     *             Is thrown if the configuration values are invalid.
     */
    protected List<CommandLineElement> processCLI() throws Exception {
        List<CommandLineElement> commands = new ArrayList<CommandLineElement>();

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
                    commands.add(new CommandLineOptionIdentifier(
                            splittedCommand));
                }
            } else if (isMappedToBooleanParameter(cliElement)) {
                // it is mapped to bool
                handleBooleanParameter(commands, cliElement);
            } else {

                List<List<CommandLineElement>> extractedParameterValues = extractParameterValues(cliElement);
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
    protected void expandParameters(
            List<List<CommandLineElement>> extractedParameterValues,
            CLIElement cliElement, List<CommandLineElement> commands) {
        // since we assume that the outer list is not empty this will always
        // work
        int listSize = extractedParameterValues.get(0).size();

        // in each iteration we expand the i-th element of each internal list to
        // the command line prefixed with the cliElement optionIdentifier (if it
        // has one)
        for (int i = 0; i < listSize; ++i) {
            // add the command prefix in each iteration
            if (!"".equals(cliElement.getOptionIdentifier())) {
                commands.add(new CommandLineOptionIdentifier(cliElement
                        .getOptionIdentifier()));
            }

            // add the actual values
            for (List<CommandLineElement> innerList : extractedParameterValues) {
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
    protected void validateExtractedParameters(
            List<List<CommandLineElement>> extractedParameterValues)
            throws Exception {

        int currentSize = -1;
        for (List<CommandLineElement> currentList : extractedParameterValues) {
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
    private List<List<CommandLineElement>> extractParameterValues(
            CLIElement cliElement) {

        List<List<CommandLineElement>> extractedParameterValues = new ArrayList<List<CommandLineElement>>();

        for (CLIMapping cliMapping : cliElement.getMapping()) {
            if (nodeConfig.getParameterKeys().contains(
                    cliMapping.getReferenceName())) {

                Parameter<?> p = nodeConfig.getParameter(cliMapping
                        .getReferenceName());
                if (!p.isNull()) {
                    if (p instanceof ListParameter) {
                        ListParameter lp = (ListParameter) p;
                        if (lp.getStrings().size() > 0) {
                            final List<CommandLineElement> tempExtractedParameterValues = new ArrayList<CommandLineElement>();
                            for (final String value : lp.getStrings()) {
                                final CommandLineElement commandLineElement;
                                if (p instanceof FileListParameter) {
                                    commandLineElement = new CommandLineFile(
                                            new FileParameter(p.getKey(), value));
                                } else {
                                    commandLineElement = new CommandLineParameter(
                                            p);
                                }
                                tempExtractedParameterValues
                                        .add(commandLineElement);
                            }
                            extractedParameterValues
                                    .add(tempExtractedParameterValues);
                        }
                    } else {
                        List<CommandLineElement> l = new ArrayList<CommandLineElement>();
                        final CommandLineElement commandLineElement;
                        if (p instanceof FileParameter) {
                            commandLineElement = new CommandLineFile(
                                    (FileParameter) p);
                        } else {
                            commandLineElement = new CommandLineParameter(p);
                        }
                        l.add(commandLineElement);
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
    protected boolean isMappedToBooleanParameter(CLIElement cliElement) {
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
    protected void handleBooleanParameter(List<CommandLineElement> commands,
            CLIElement cliElement) {
        if (((BoolParameter) nodeConfig.getParameter(cliElement.getMapping()
                .get(0).getReferenceName())).getValue()) {
            commands.add(new CommandLineOptionIdentifier(cliElement
                    .getOptionIdentifier()));
        }
    }

    /**
     * Exports all configuration settings to the working directory.
     * 
     * @param configStore
     * @throws IOException
     */
    protected void exportPlainConfiguration(File workingDirectory)
            throws IOException {
        PlainNodeConfigurationWriter writer = new PlainNodeConfigurationWriter();
        writer.init(nodeConfig);
        writer.write(workingDirectory.getAbsolutePath() + File.separator
                + "params.ini");
    }
}
