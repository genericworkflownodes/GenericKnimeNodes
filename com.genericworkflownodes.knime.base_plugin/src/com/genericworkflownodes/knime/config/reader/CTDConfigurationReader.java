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
package com.genericworkflownodes.knime.config.reader;

import java.io.InputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.INodeConfigurationReader;
import com.genericworkflownodes.knime.config.reader.handler.CTDHandler;
import com.genericworkflownodes.knime.outputconverter.Relocator;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.schemas.SchemaProvider;

/**
 * Reads {@link INodeConfiguration} from a CTD file.
 * 
 * @author aiche
 */
public class CTDConfigurationReader implements INodeConfigurationReader {

	/**
	 * The parsed configuration.
	 */
	INodeConfiguration config;

	@Override
	public INodeConfiguration read(InputStream in) throws Exception {

		// create schema and parser for validation and parsing
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema ctdSchema = schemaFactory.newSchema(SchemaProvider.class
				.getResource("CTD.xsd"));
		SAXParserFactory spfac = SAXParserFactory.newInstance();
		spfac.setValidating(false);
		spfac.setSchema(ctdSchema);

		SAXParser sp = spfac.newSAXParser();

		CTDHandler handler = new CTDHandler(sp.getXMLReader());
		sp.parse(in, handler);

		config = handler.getNodeConfiguration();

		// validate mappings of CLI config
		for (CLIElement cliElement : config.getCLI().getCLIElement()) {
			validateCLIElement(cliElement);
		}

		// validate mappings in OutputConverter
		for (Relocator relocator : config.getRelocators()) {
			validateRelocator(relocator);
		}

		// validate ports
		for (Port port : config.getInputPorts()) {
			validatePort(port, config);
		}

		for (Port port : config.getOutputPorts()) {
			validatePort(port, config);
		}

		// return parsed and validated config
		return config;
	}

	/**
	 * Checks if the constructed port is valid:
	 * 
	 * <ul>
	 * <li>The referenced parameter is a file parameter.</li>
	 * <li>The referenced parameter is a file list for multi ports and a file if
	 * it's not a multi-port.</li>
	 * <li>The port has at least one MIMEType.</li>
	 * </ul>
	 * 
	 * @param port
	 * @throws Exception
	 */
	private void validatePort(final Port port, final INodeConfiguration config)
			throws Exception {
		// check if the referenced parameter exists
		Parameter<?> p = config.getParameter(port.getName());
		if (p == null)
			throw new Exception(String.format(
					"The given port %s has no corresponding parameter.",
					port.getName()));
		if (!(p instanceof IFileParameter))
			throw new Exception(
					String.format(
							"The parameter corresponding to port %s is not a IFileParameter.",
							port.getName()));
		if (port.isMultiFile() && !(p instanceof FileListParameter))
			throw new Exception(
					String.format(
							"The given port %s is a multifile port but the corresponding parameter is a single file parameter.",
							port.getName()));
		if (!port.isMultiFile() && !(p instanceof FileParameter))
			throw new Exception(
					String.format(
							"The given port %s is a singlefile port but the corresponding parameter is a multifile parameter.",
							port.getName()));
		if (port.getMimeTypes().size() < 1)
			throw new Exception(String.format(
					"The given port %s has no MIMETypes.", port.getName()));
	}

	/**
	 * Checks if the given cli-element is semantically correct. If not an
	 * exception is thrown.
	 * 
	 * @param cliElement
	 *            The {@link CLIElement} to check.
	 * @throws Exception
	 *             Is thrown if the parameter contains invalid information.
	 */
	private void validateCLIElement(final CLIElement cliElement)
			throws Exception {
		if (cliElement.getMapping().size() > 0) {

			for (CLIMapping mapping : cliElement.getMapping()) {
				// check if a parameter with the given name was registered
				checkIfMappedParameterExists(mapping);
			}

			// if we have more then one mapped parameter they cannot be boolean
			// parameters
			if (cliElement.getMapping().size() > 1) {
				for (CLIMapping mapping : cliElement.getMapping()) {
					// find mapped parameter
					if (config.getParameter(mapping.getReferenceName()) != null) {
						// check that it is not boolean
						if (config.getParameter(mapping.getReferenceName()) instanceof BoolParameter) {
							throw new Exception();
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if the paramter given in the mapping element exists.
	 * 
	 * @param cliMapping
	 *            The {@link CLIMapping} to check.
	 * @throws Exception
	 *             Is thrown if their exists no parameter corresponding to the
	 *             given mapping.
	 */
	private void checkIfMappedParameterExists(final CLIMapping cliMapping)
			throws Exception {
		if (config.getParameter(cliMapping.getReferenceName()) == null
				&& !portWithRefNameExists(cliMapping.getReferenceName())) {
			throw new Exception("Unknown Parameter "
					+ cliMapping.getReferenceName());
		}
	}

	/**
	 * Checks whether a port with the specified name was registered.
	 * 
	 * @param mappingRefName
	 *            The name of the mapped port/parameter.
	 * @return True if a port with this name exists, false otherwise.
	 */
	private boolean portWithRefNameExists(final String mappingRefName) {
		boolean hasPortWithMappingName = false;

		// check inPorts
		hasPortWithMappingName |= findInPortList(mappingRefName,
				config.getInputPorts());
		hasPortWithMappingName |= findInPortList(mappingRefName,
				config.getOutputPorts());

		return hasPortWithMappingName;
	}

	/**
	 * Checks if a port with the given name is contained in the given port list.
	 * 
	 * @param mappingRefName
	 *            The parameter name to check.
	 * @param ports
	 *            The list of ports to check.
	 * @return True if a port with the given name exists, false otherwise.
	 */
	private boolean findInPortList(final String mappingRefName,
			final List<Port> ports) {
		for (Port p : ports) {
			if (p.getName().equals(mappingRefName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the relocator is valid (e.g., if the referenced parameter
	 * exists and if it is an output parameter).
	 * 
	 * @param relocator
	 *            The relocator to validate.
	 * @throws Exception
	 *             Is thrown if the port points to a non existing output port.
	 */
	private void validateRelocator(final Relocator relocator) throws Exception {
		// check if converter ref exists
		if (!findInPortList(relocator.getReferencedParamter(),
				config.getOutputPorts())) {
			throw new Exception(
					"Invalid Output Converter: No output port with name "
							+ relocator.getReferencedParamter() + " exists.");
		}
	}

}
