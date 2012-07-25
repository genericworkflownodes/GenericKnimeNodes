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
package com.genericworkflownodes.knime.outputconverter.util;

import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.genericworkflownodes.knime.outputconverter.IOutputConverter;
import com.genericworkflownodes.knime.outputconverter.IOutputConverterFactory;
import com.genericworkflownodes.knime.outputconverter.config.Converter;

/**
 * Helper class to get an extension of {@link IOutputConverter} by a given name.
 * 
 * @author aiche
 */
public final class OutputConverterHelper {

	/**
	 * Id of the extension point we want to query.
	 */
	private static String EXTENSION_ID = "com.genericworkflownods.knime.outputconverter.OutputConverter";

	/**
	 * Name of the attribute that stores the converter id.
	 */
	private static String ID_ATTRIBUTE = "converter-id";

	/**
	 * Name of the attribute that stores the class name of the factory to create
	 * the {@link IOutputConverter}.
	 */
	private static String FACTORY_ATTRIBUTE = "factoryclass";

	/**
	 * Private c'tor to avoid instantiation of util class.
	 */
	private OutputConverterHelper() {
	}

	/**
	 * Retrieves an {@link IOutputConverter} from the extension registry and
	 * configures it using the {@link IOutputConverterFactory}.
	 * 
	 * @param converterID
	 *            The id of the converter used for registration.
	 * @param configuration
	 *            The configuration of the converter.
	 * @return A fully configured {@link IOutputConverter}.
	 * @throws Exception
	 *             If no extension with the given id can be found.
	 */
	public static IOutputConverter getConfiguredConverterByName(
			final String converterID, final Properties configuration)
			throws Exception {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg
				.getConfigurationElementsFor(EXTENSION_ID);

		IOutputConverter configuredConverter = null;

		for (IConfigurationElement elem : elements) {
			// find the correct extension
			if (converterID.equals(elem.getAttribute(ID_ATTRIBUTE))) {
				// instantiate
				IOutputConverterFactory factory = (IOutputConverterFactory) elem
						.createExecutableExtension(FACTORY_ATTRIBUTE);
				configuredConverter = factory.create(configuration);
				break;
			}
		}

		if (configuredConverter == null) {
			throw new Exception("No IOutputConverter with the " + ID_ATTRIBUTE
					+ ": " + converterID + " was registered.");
		}

		return configuredConverter;
	}

	/**
	 * Retrieves an {@link IOutputConverter} from the extension registry and
	 * configures it using the {@link IOutputConverterFactory}.
	 * 
	 * @param converter
	 *            The {@link Converter} containing the properties and the
	 *            converter-id.
	 * @return A fully configured {@link IOutputConverter}.
	 * 
	 * @throws Exception
	 *             If no extension with the given id can be found.
	 */
	public static IOutputConverter getConfiguredOutputConverter(
			final Converter converter) throws Exception {
		return getConfiguredConverterByName(converter.getClazz(),
				converter.getConverterProperties());
	}
}
