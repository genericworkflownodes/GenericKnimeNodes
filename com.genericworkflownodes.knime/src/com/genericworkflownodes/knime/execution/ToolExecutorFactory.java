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

/**
 * Manages the matching and instantiation of a matching {@link IToolExecutor} to
 * the given parameters.
 * 
 * @author aiche
 */
public class ToolExecutorFactory {

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

        // precheck the input
        if (executorClassName == null || "".equals(executorClassName)) {
            throw new UnknownToolExecutorException("");
        }

        if (commandGeneratorClassName == null
                || "".equals(commandGeneratorClassName)) {
            throw new UnknownToolExecutorException("");
        }

        IToolExecutor executor;

        try {
            executor = (IToolExecutor) Class.forName(executorClassName)
                    .newInstance();
        } catch (IllegalAccessException ex) {
            throw new UnknownToolExecutorException(executorClassName, ex);
        } catch (ClassNotFoundException ex) {
            throw new UnknownToolExecutorException(executorClassName, ex);
        } catch (InstantiationException ex) {
            throw new UnknownToolExecutorException(executorClassName, ex);
        }

        try {
            // configure the m_executor
            ICommandGenerator generator = (ICommandGenerator) Class.forName(
                    commandGeneratorClassName).newInstance();
            executor.setCommandGenerator(generator);
        } catch (IllegalAccessException ex) {
            throw new UnknownCommandGeneratorException(
                    commandGeneratorClassName, ex);
        } catch (ClassNotFoundException ex) {
            throw new UnknownCommandGeneratorException(
                    commandGeneratorClassName, ex);
        } catch (InstantiationException ex) {
            throw new UnknownCommandGeneratorException(
                    commandGeneratorClassName, ex);
        }

        return executor;
    }
}
