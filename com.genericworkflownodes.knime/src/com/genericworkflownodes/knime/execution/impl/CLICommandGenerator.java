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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.commandline.impl.CommandLineFile;
import com.genericworkflownodes.knime.commandline.impl.CommandLineFixedString;
import com.genericworkflownodes.knime.commandline.impl.CommandLineParameter;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.PlainNodeConfigurationWriter;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringParameter;

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
    public List<CommandLineElement> generateCommands(
            final INodeConfiguration nodeConfiguration,
            final IPluginConfiguration pluginConfiguration,
            final File workingDirectory) throws Exception {

        // ease the passing around of variables
        nodeConfig = nodeConfiguration;

        // export the node configuration as plain text, for debugging and
        // logging
        exportPlainConfiguration(workingDirectory);

        List<CommandLineElement> parameters;

        try {
            parameters = processCLI();
        } catch (final Exception e) {
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
        for (final CLIElement cliElement : nodeConfig.getCLI()
                .getCLIElement()) {
            logger.info("CLIElement: " + cliElement.getOptionIdentifier());

            if (!StringUtils.isBlank(cliElement.getOptionIdentifier())
                    && cliElement.getMapping().size() == 0) {
                // simple fixed argument for the command line, no mapping to
                // params given

                // to avoid problems with spaces in commands we split fixed
                // values
                final String[] splitResult = cliElement.getOptionIdentifier()
                        .split(" ");
                for (final String splittedCommand : splitResult) {
                    commands.add(
                            new CommandLineFixedString(splittedCommand));
                }
            } else if (isMappedToBooleanParameter(cliElement)) {
                // it is mapped to bool
                handleBooleanParameter(commands, cliElement);
            } else {

                final List<List<? extends CommandLineElement>> extractedParameterValues = extractParameterValues(
                        cliElement);
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
     * @param extractedParameterValues
     * @param cliElement
     * @param commands
     */
    protected void expandParameters(
            final List<List<? extends CommandLineElement>> extractedParameterValues,
            final CLIElement cliElement,
            final List<CommandLineElement> commands) {
        // in each iteration we expand the value(s) of the mapped parameters
        for (final List<? extends CommandLineElement> innerList : extractedParameterValues) {
            // add the command prefix in each iteration
            if (!StringUtils.isBlank(cliElement.getOptionIdentifier())) {
                commands.add(new CommandLineFixedString(
                        cliElement.getOptionIdentifier()));
            }
            commands.addAll(innerList);
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
        final List<List<? extends CommandLineElement>> extractedParameterValues)
                    throws Exception {

        int currentSize = -1;
        for (final List<? extends CommandLineElement> currentList : extractedParameterValues) {
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
    private List<List<? extends CommandLineElement>> extractParameterValues(
            final CLIElement cliElement) {

        final List<List<? extends CommandLineElement>> extractedParameterValues = new ArrayList<List<? extends CommandLineElement>>();

        for (final CLIMapping cliMapping : cliElement.getMapping()) {
            if (nodeConfig.getParameterKeys()
                    .contains(cliMapping.getReferenceName())) {

                final Parameter<?> p = nodeConfig
                        .getParameter(cliMapping.getReferenceName());
                if (!p.isNull()) {
                    if (p instanceof ListParameter) {
                        handleListParameter(extractedParameterValues,
                                (ListParameter) p);
                    } else {
                        handleNonListParameter(extractedParameterValues, p);
                    }
                }
            }
        }

        return extractedParameterValues;
    }

    private void handleNonListParameter(
            final List<List<? extends CommandLineElement>> extractedParameterValues,
            final Parameter<?> p) {
        final List<CommandLineElement> l = new ArrayList<CommandLineElement>();
        final CommandLineElement commandLineElement;
        if (p instanceof FileParameter) {
            commandLineElement = new CommandLineFile((FileParameter) p);
        } else {
            commandLineElement = new CommandLineParameter(p);
        }
        l.add(commandLineElement);
        extractedParameterValues.add(l);
    }

    private void handleListParameter(
            final List<List<? extends CommandLineElement>> extractedParameterValues,
            final ListParameter listParameter) {
        final int nValues = listParameter.getStrings().size();
        final String key = ((Parameter<?>) listParameter).getKey();
        final List<CommandLineElement> tmpList = new LinkedList<CommandLineElement>();
        // we know it's a list, but we don't need to use sequence numbers if
        // there's only one element in the list
        if (nValues > 1) {
            int sequence = 0;
            for (final String value : listParameter.getStrings()) {
                final CommandLineElement commandLineElement;
                if (listParameter instanceof IFileParameter) {
                    commandLineElement = new CommandLineFile(
                            new FileParameter(key, value));
                } else {
                    commandLineElement = new CommandLineParameter(
                            new StringParameter(key, value));
                }
                commandLineElement.setSequenceNumber(sequence++);
                tmpList.add(commandLineElement);
            }
        } else {
            // only one value in the list, no need to use sequence numbers
            final String value = listParameter.getStrings().get(0);
            if (listParameter instanceof IFileParameter) {
                tmpList.add(new CommandLineFile(new FileParameter(key, value)));
            } else {
                tmpList.add(new CommandLineParameter(
                        new StringParameter(key, value)));
            }
        }
        extractedParameterValues.add(tmpList);
    }

    /**
     * Returns true if the given CLIElement maps to a boolean parameter.
     * 
     * @param cliElement
     * @return
     */
    protected boolean isMappedToBooleanParameter(final CLIElement cliElement) {
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
    protected void handleBooleanParameter(final List<CommandLineElement> commands,
            final CLIElement cliElement) {
        if (((BoolParameter) nodeConfig.getParameter(
                cliElement.getMapping().get(0).getReferenceName()))
                        .getValue()) {
            commands.add(new CommandLineFixedString(
                    cliElement.getOptionIdentifier()));
        }
    }

    /**
     * Exports all configuration settings to the working directory.
     * 
     * @param configStore
     * @throws IOException
     */
    protected void exportPlainConfiguration(final File workingDirectory)
            throws IOException {
        final PlainNodeConfigurationWriter writer = new PlainNodeConfigurationWriter();
        writer.init(nodeConfig);
        writer.write(workingDirectory.getAbsolutePath() + File.separator
                + "params.ini");
    }
}
