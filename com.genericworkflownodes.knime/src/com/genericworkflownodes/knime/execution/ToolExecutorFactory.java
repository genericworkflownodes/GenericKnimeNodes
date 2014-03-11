/**
 * Copyright (c) 2014, Stephan Aiche.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Manages the matching and instantiation of a matching {@link IToolExecutor} to
 * the given parameters.
 * 
 * @author aiche
 */
public class ToolExecutorFactory {

    private static final String EXECUTOR_EXTENSION_POINT_ID = "com.genericworkflownodes.knime.execution.Executor";
    private static final String COMMAND_GENERATOR_EXTENSION_POINT_ID = "com.genericworkflownodes.knime.execution.CommandGenerator";

    /**
     * Creates a configured {@link IToolExecutor}.
     * 
     * @param executorClassName
     *            The class name of the specific {@link IToolExecutor} that
     *            should be generated.
     * @param commandGeneratorClassName
     *            The class name of the {@link ICommandGenerator} that should be
     *            used to configure the {@link IToolExecutor}.
     * @return A configured {@link IToolExecutor}.
     * @throws UnknownToolExecutorException
     *             Is thrown if the given executorClassName is null or empty or
     *             if class matching the given name could be found by the class
     *             loader.
     * @throws UnknownCommandGeneratorException
     *             Is thrown if the given commandGeneratorClassName is null or
     *             empty or if class matching the given name could be found by
     *             the class loader.
     */
    public static IToolExecutor createToolExecutor(
            final String executorClassName,
            final String commandGeneratorClassName)
            throws UnknownToolExecutorException,
            UnknownCommandGeneratorException {

        IToolExecutor executor = getExecutor(executorClassName);
        executor.setCommandGenerator(getCommandGenerator(commandGeneratorClassName));
        return executor;
    }

    /**
     * Queries the extension registry for a matching command generator.
     * 
     * @param commandGeneratorClassName
     *            The class name we're looking for.
     * @return The ICommandGenerator matching the given class name.
     * @throws UnknownCommandGeneratorException
     *             If no matching ICommandGenerator was found or the given class
     *             name was empty.
     */
    private static ICommandGenerator getCommandGenerator(
            final String commandGeneratorClassName)
            throws UnknownCommandGeneratorException {
        if (commandGeneratorClassName == null
                || "".equals(commandGeneratorClassName)) {
            throw new UnknownCommandGeneratorException("");
        }

        try {
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IConfigurationElement[] elements = reg
                    .getConfigurationElementsFor(COMMAND_GENERATOR_EXTENSION_POINT_ID);

            // try to find matching extension
            for (IConfigurationElement elem : elements) {
                // check if we have the IToolExecutor we were looking for
                if (elem.getAttribute("name").equals(commandGeneratorClassName)) {
                    final Object o = elem.createExecutableExtension("class");
                    // cast is guaranteed to work based on the extension point
                    // definition
                    return (ICommandGenerator) o;
                }
            }
            // we didn't find a matching ICommandGenerator
            throw new UnknownCommandGeneratorException(
                    commandGeneratorClassName);
        } catch (CoreException e) {
            throw new UnknownCommandGeneratorException(
                    commandGeneratorClassName, e);
        }
    }

    /**
     * Queries the extension registry for a matching tool executor.
     * 
     * @param executorClassName
     *            The class name we're looking for.
     * @return The {@link IToolExecutor} matching the given class name.
     * @throws UnknownToolExecutorException
     *             If no matching IToolExecutor was found or the given class
     *             name was empty.
     */
    private static IToolExecutor getExecutor(final String executorClassName)
            throws UnknownToolExecutorException {
        // precheck the input
        if (executorClassName == null || "".equals(executorClassName)) {
            throw new UnknownToolExecutorException("");
        }

        try {
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IConfigurationElement[] elements = reg
                    .getConfigurationElementsFor(EXECUTOR_EXTENSION_POINT_ID);

            // try to find matching extension
            for (IConfigurationElement elem : elements) {
                // check if we have the IToolExecutor we were looking for
                if (elem.getAttribute("name").equals(executorClassName)) {
                    final Object o = elem.createExecutableExtension("class");
                    // cast is guaranteed to work based on the extension point
                    // definition
                    return (IToolExecutor) o;
                }
            }
            // we didn't find a matching IToolExecutor
            throw new UnknownToolExecutorException(executorClassName);
        } catch (CoreException e) {
            throw new UnknownToolExecutorException(executorClassName, e);
        }
    }
}
