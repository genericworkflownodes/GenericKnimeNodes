/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.outputconverter.Relocator;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.schemas.SchemaProvider;
import com.genericworkflownodes.knime.schemas.SimpleErrorHandler;
import com.genericworkflownodes.util.StringUtils.DoubleRangeExtractor;
import com.genericworkflownodes.util.StringUtils.IntegerRangeExtractor;

/**
 * Class to read an {@link INodeConfiguration} from a CTD file.
 * 
 * @author roettig, aiche
 */
@Deprecated
public class CTDNodeConfigurationReader implements INodeConfigurationReader {

	/**
	 * The logger used to indicate problems.
	 */
	@SuppressWarnings("unused")
	private static Logger LOG = Logger
			.getLogger(CTDNodeConfigurationReader.class.getCanonicalName());

	/**
	 * The XML document.
	 */
	private Document doc;

	/**
	 * The final node configuration.
	 */
	private final NodeConfiguration config = new NodeConfiguration();

	/**
	 * C'tor.
	 */
	public CTDNodeConfigurationReader() {
	}

	/**
	 * Tag used to identify input ports.
	 */
	private static final String INPUTFILE_TAG = "input file";

	/**
	 * Tag used to identify output ports.
	 */
	private static final String OUTPUTFILE_TAG = "output file";

	/**
	 * List of identified ports.
	 */
	private final Set<String> capturedPorts = new HashSet<String>();

	/**
	 * List of identified input ports.
	 */
	private static List<Port> INPUT_PORTS;

	/**
	 * List of identified output ports.
	 */
	private static List<Port> OUTPUT_PORTS;

	/**
	 * List of port names that will not be created.
	 */
	private static final List<String> PORT_BLACKLIST = Arrays.asList(
			"write_ini", "write_par", "par", "help", "ini");

	/**
	 * Extracts the port information from the CTD file.
	 * 
	 * @throws Exception
	 *             An exception is thrown if invalid port information is .
	 *             contained in the CTD.
	 */
	private void readPorts() throws Exception {
		INPUT_PORTS = new ArrayList<Port>();
		OUTPUT_PORTS = new ArrayList<Port>();

		Node node = doc.selectSingleNode("/tool/PARAMETERS");
		Element root = (Element) node;
		processIOPorts(root);

		config.setInports(INPUT_PORTS.toArray(new Port[INPUT_PORTS.size()]));
		config.setOutports(OUTPUT_PORTS.toArray(new Port[OUTPUT_PORTS.size()]));
	}

	/**
	 * Extract IO port information from the XML document.
	 * 
	 * @param root
	 *            The root of all parameter information in the CTD.
	 * @throws Exception
	 *             An exception is thrown if invalid port information is .
	 *             contained in the CTD.
	 */
	@SuppressWarnings("unchecked")
	private void processIOPorts(final Element root) throws Exception {
		List<Node> items = root.selectNodes("//ITEM[contains(@tags,'"
				+ OUTPUTFILE_TAG + "')]");
		for (Node n : items) {
			createPortFromNode(n);
		}

		items = root.selectNodes("//ITEM[contains(@tags,'" + INPUTFILE_TAG
				+ "')]");
		for (Node n : items) {
			createPortFromNode(n);
		}

		items = root.selectNodes("//ITEMLIST[contains(@tags,'" + INPUTFILE_TAG
				+ "')]");
		for (Node n : items) {
			createPortFromNode(n);
		}

		items = root.selectNodes("//ITEMLIST[contains(@tags,'" + OUTPUTFILE_TAG
				+ "')]");
		for (Node n : items) {
			createPortFromNode(n);
		}
	}

	/**
	 * Extract parameter (non-port) information from the CTD.
	 */
	@SuppressWarnings("unchecked")
	private void readParameters() {
		Node root = doc.selectSingleNode("/tool/PARAMETERS");
		List<Node> items = root.selectNodes("//ITEM[not(contains(@tags,'"
				+ OUTPUTFILE_TAG + "')) and not(contains(@tags,'"
				+ INPUTFILE_TAG + "'))]");
		for (Node n : items) {
			processItem(n);
		}
		items = root.selectNodes("//ITEMLIST[not(contains(@tags,'"
				+ OUTPUTFILE_TAG + "')) and not(contains(@tags,'"
				+ INPUTFILE_TAG + "'))]");
		for (Node n : items) {
			processMultiItem(n);
		}
	}

	/**
	 * Given an xml element (node) the method extracts the path to parameter
	 * root and returns it as string (e.g.,
	 * algorithm.parameter-group.parametername).
	 * 
	 * @param node
	 *            The parameter node for which the path should be extracted.
	 * @return The path to given node as string.
	 */
	private String getPath(final Node node) {
		Node iteratable = node;

		List<String> pathNodes = new ArrayList<String>();
		while (iteratable != null && !iteratable.getName().equals("PARAMETERS")) {
			pathNodes.add(iteratable.valueOf("@name"));
			iteratable = iteratable.getParent();
		}

		Collections.reverse(pathNodes);

		String finalPath = "";
		int numberOfNodes = pathNodes.size();
		for (int i = 0; i < numberOfNodes; i++) {
			if (i == numberOfNodes - 1) {
				finalPath += pathNodes.get(i);
			} else {
				finalPath += pathNodes.get(i) + ".";
			}
		}
		return finalPath;
	}

	/**
	 * Creates an in- or output port from the given Node in the CTD document.
	 * 
	 * @param node
	 *            The node from which the port should be generated.
	 * @throws Exception
	 *             An exception is thrown if the given port information is
	 *             incomplete or invalid.
	 */
	private void createPortFromNode(final Node node) throws Exception {

		Element elem = (Element) node;
		final boolean multi = "ITEMLIST".equals(elem.getName());

		String name = node.valueOf("@name");
		String descr = node.valueOf("@description");
		String tags = node.valueOf("@tags");

		if (isIgnoredPort(name)) {
			return;
		}

		Port port = new Port();

		port.setMultiFile(multi);

		if (tags.contains(INPUTFILE_TAG) || tags.contains(OUTPUTFILE_TAG)) {
			String[] fileExtensions = null;

			if (elem.attributeValue("supported_formats") == null) {
				if (elem.attributeValue("restrictions") != null) {
					String formats = node.valueOf("@restrictions");
					fileExtensions = formats.split(",");
					for (int i = 0; i < fileExtensions.length; i++) {
						fileExtensions[i] = fileExtensions[i].replace("*.", "");
					}
				} else {
					throw new Exception(
							"i/o item '"
									+ elem.attributeValue("name")
									+ "' with missing attribute supported_formats detected");
				}
			} else {
				String formats = node.valueOf("@supported_formats");
				fileExtensions = formats.split(",");
				for (int i = 0; i < fileExtensions.length; i++) {
					fileExtensions[i] = fileExtensions[i].trim();
				}
			}

			String path = getPath(node);
			port.setName(path);

			port.setDescription(descr);

			boolean optional = true;
			if (tags.contains("mandatory") || tags.contains("required")) {
				optional = false;
			} else {
				optional = true;
			}
			port.setOptional(optional);

			for (String mt : fileExtensions) {
				port.addMimeType(mt.trim());
			}

		}
		if (tags.contains(OUTPUTFILE_TAG)) {
			OUTPUT_PORTS.add(port);
			capturedPorts.add(port.getName());

			if (multi) {
				String path = getPath(node);
				FileListParameter param = new FileListParameter(name,
						new ArrayList<String>());
				param.setPort(port);
				param.setDescription(descr);
				param.setIsOptional(false);
				config.addParameter(path, param);
			}
		}
		if (tags.contains(INPUTFILE_TAG)) {
			INPUT_PORTS.add(port);
			capturedPorts.add(port.getName());
		}

	}

	/**
	 * Checks if the port should not be integrated into the final node (e.g.,
	 * debug or parameter outputs).
	 * 
	 * @param name
	 *            The name of the potential port.
	 * @return True if the port is on the blacklist of ports to ignore.
	 */
	private boolean isIgnoredPort(final String name) {
		return PORT_BLACKLIST.contains(name);
	}

	/**
	 * Converts a single item into a {@link Parameter}.
	 * 
	 * @param elem
	 *            The current node.
	 */
	private void processItem(final Node elem) {
		String name = elem.valueOf("@name");
		String path = getPath(elem);

		if (capturedPorts.contains(path)) {
			return;
		}

		if (isIgnoredPort(name)) {
			return;
		}

		Parameter<?> param = getParameterFromNode(elem);
		config.addParameter(path, param);
	}

	/**
	 * Converts an item corresponding to a list (ITEMLIST) to an equivalent
	 * {@link Parameter}.
	 * 
	 * @param elem
	 *            The xml element.
	 */
	private void processMultiItem(final Node elem) {
		String name = elem.valueOf("@name");
		String path = getPath(elem);

		if (capturedPorts.contains(path)) {
			return;
		}

		if (isIgnoredPort(name)) {
			return;
		}

		Parameter<?> param = getMultiParameterFromNode(elem);
		config.addParameter(path, param);
	}

	/**
	 * Extract the descriptive entries from the CTD (e.g., tool-name,
	 * description, manual).
	 * 
	 * @throws Exception
	 *             An exception is thrown if required elements are not contained
	 *             in the document.
	 */
	private void readDescription() throws Exception {
		Node node = doc.selectSingleNode("/tool");
		if (node == null) {
			throw new Exception("CTD has no root named tool");
		}

		node = doc.selectSingleNode("/tool/name");
		if (node == null) {
			throw new Exception("CTD has no tool name");
		}
		String name = node.valueOf("text()");
		if (name.equals("")) {
			throw new Exception("CTD has no tool name");
		}
		config.setName(name);

		node = doc.selectSingleNode("/tool/description");
		String sdescr = "";
		if (node != null) {
			sdescr = node.valueOf("text()");
		}
		config.setDescription(sdescr);

		node = doc.selectSingleNode("/tool/manual");
		String ldescr = "";
		if (node != null) {
			ldescr = node.valueOf("text()");
		}
		config.setManual(ldescr);

		node = doc.selectSingleNode("/tool/version");
		String lversion = "";
		if (node != null) {
			lversion = node.valueOf("text()");
		}
		config.setVersion(lversion);

		node = doc.selectSingleNode("/tool/docurl");
		String docurl = "";
		if (node != null) {
			docurl = node.valueOf("text()");
		}
		config.setDocUrl(docurl);

		node = doc.selectSingleNode("/tool/category");
		String cat = "";
		if (node != null) {
			cat = node.valueOf("text()");
		}
		config.setCategory(cat);
	}

	/**
	 * Convert an node in the CTD document into a {@link Parameter}.
	 * 
	 * @param node
	 *            The xml node of CTD document.
	 * @return a {@link Parameter} equivalent to the xml node.
	 */
	private Parameter<?> getParameterFromNode(final Node node) {
		Parameter<?> createdParameter = null;
		String type = node.valueOf("@type");
		String name = node.valueOf("@name");
		String value = node.valueOf("@value");
		String restrs = node.valueOf("@restrictions");
		String descr = node.valueOf("@description");

		String tags = node.valueOf("@tags");
		Set<String> tagset = tokenSet(tags);

		if (type.toLowerCase().equals("double")
				|| type.toLowerCase().equals("float")) {
			createdParameter = processDoubleParameter(name, value, restrs, tags);
		} else {
			if (type.toLowerCase().equals("int")) {
				createdParameter = processIntParameter(name, value, restrs,
						tags);
			} else {
				if (type.toLowerCase().equals("string")) {
					createdParameter = processStringParameter(name, value,
							restrs, tags);
				}
			}
		}

		createdParameter.setDescription(descr);

		if (tagset.contains("mandatory") || tagset.contains("required")) {
			createdParameter.setIsOptional(false);
		}

		if (tagset.contains("advanced")) {
			createdParameter.setAdvanced(true);
		}

		return createdParameter;
	}

	/**
	 * Creates a {@link Parameter} from the given xml node.
	 * 
	 * @param node
	 *            The xml node.
	 * @return A {@link Parameter} corresponding to the xml node.
	 */
	private Parameter<?> getMultiParameterFromNode(final Node node) {
		String type = node.valueOf("@type");
		String name = node.valueOf("@name");
		String restrs = node.valueOf("@restrictions");
		String descr = node.valueOf("@description");

		String tags = node.valueOf("@tags");
		Set<String> tagset = tokenSet(tags);

		@SuppressWarnings("unchecked")
		List<Node> subnodes = node.selectNodes("LISTITEM");

		List<String> values = new ArrayList<String>();
		for (Node n : subnodes) {
			values.add(n.valueOf("@value"));
		}

		Parameter<?> param = null;

		if (type.toLowerCase().equals("double")
				|| type.toLowerCase().equals("float")) {
			param = processDoubleListParameter(name, values, restrs, tags);
		} else {
			if (type.toLowerCase().equals("int")) {
				param = processIntListParameter(name, values, restrs, tags);
			} else {
				if (type.toLowerCase().equals("string")) {
					param = processStringListParameter(name, values, restrs,
							tags);
				}
			}
		}

		param.setDescription(descr);

		if (tagset.contains("mandatory") || tagset.contains("required")) {
			param.setIsOptional(false);
		}

		if (tagset.contains("advanced")) {
			param.setAdvanced(true);
		}

		return param;
	}

	/**
	 * Creates a {@link StringListParameter} based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param values
	 *            The values of the parameter.
	 * @param restrs
	 *            Restrictions set for the given parameter.
	 * @param tags
	 *            Tags.
	 * @return A {@link Parameter}.
	 */
	private Parameter<?> processStringListParameter(final String name,
			final List<String> values, final String restrs, final String tags) {
		StringListParameter slp = new StringListParameter(name, values);
		if (restrs != null && !restrs.isEmpty()) {
			String[] toks = restrs.split(",");
			slp.setRestrictions(Arrays.asList(toks));
		}
		return slp;
	}

	/**
	 * Creates a {@link IntegerListParameter} based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param values
	 *            The values of the parameter.
	 * @param restrs
	 *            Restrictions set for the given parameter.
	 * @param tags
	 *            Tags.
	 * @return A {@link Parameter}.
	 */
	private Parameter<?> processIntListParameter(final String name,
			final List<String> values, final String restrs, final String tags) {
		List<Integer> vals = new ArrayList<Integer>();
		for (String currentValue : values) {
			vals.add(Integer.parseInt(currentValue));
		}

		IntegerListParameter integerListParameter = new IntegerListParameter(
				name, vals);

		integerListParameter.setLowerBound(new IntegerRangeExtractor()
				.getLowerBound(restrs));
		integerListParameter.setUpperBound(new IntegerRangeExtractor()
				.getUpperBound(restrs));

		return integerListParameter;
	}

	/**
	 * Creates a {@link DoubleListParameter} based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param values
	 *            The values of the parameter.
	 * @param restrs
	 *            Restrictions set for the given parameter.
	 * @param tags
	 *            Tags.
	 * @return A {@link Parameter}.
	 */
	private Parameter<?> processDoubleListParameter(final String name,
			final List<String> values, final String restrs, final String tags) {
		List<Double> vals = new ArrayList<Double>();
		for (String currentValue : values) {
			vals.add(Double.parseDouble(currentValue));
		}

		DoubleListParameter doubleListParameter = new DoubleListParameter(name,
				vals);

		doubleListParameter.setLowerBound(new DoubleRangeExtractor()
				.getLowerBound(restrs));
		doubleListParameter.setUpperBound(new DoubleRangeExtractor()
				.getUpperBound(restrs));

		return doubleListParameter;
	}

	/**
	 * Creates an double parameter based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param value
	 *            The value of the parameter.
	 * @param restrs
	 *            The restrictions for the parameter.
	 * @param tags
	 *            The tags of the parameter.
	 * @return An {@link DoubleParameter} corresponding to the passed
	 *         information.
	 */
	private Parameter<?> processDoubleParameter(final String name,
			final String value, final String restrs, final String tags) {
		DoubleParameter doubleParameter = new DoubleParameter(name, value);
		doubleParameter.setLowerBound(new DoubleRangeExtractor()
				.getLowerBound(restrs));
		doubleParameter.setUpperBound(new DoubleRangeExtractor()
				.getUpperBound(restrs));
		return doubleParameter;
	}

	/**
	 * Returns a list single tags delimited by a "," as given in the passed
	 * string.
	 * 
	 * @param str
	 *            The string from which the single tags/tokens should be
	 *            extracted.
	 * @return A {@link Set} containing all tags/tokens included in the passed
	 *         string.
	 */
	private static Set<String> tokenSet(final String str) {
		String[] tokens = str.split(",");
		Set<String> tokenSet = new HashSet<String>();
		Collections.addAll(tokenSet, tokens);
		return tokenSet;
	}

	/**
	 * Creates an integer parameter based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param value
	 *            The value of the parameter.
	 * @param restrs
	 *            The restrictions for the parameter.
	 * @param tags
	 *            The tags of the parameter.
	 * @return An {@link IntegerParameter} corresponding to the passed
	 *         information.
	 */
	private Parameter<?> processIntParameter(final String name,
			final String value, final String restrs, final String tags) {
		IntegerParameter integerParameter = new IntegerParameter(name, value);
		integerParameter.setLowerBound(new IntegerRangeExtractor()
				.getLowerBound(restrs));
		integerParameter.setUpperBound(new IntegerRangeExtractor()
				.getUpperBound(restrs));
		return integerParameter;
	}

	/**
	 * Creates an string parameter based on the given information.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @param value
	 *            The value of the parameter.
	 * @param restrs
	 *            The restrictions for the parameter.
	 * @param tags
	 *            The tags of the parameter.
	 * @return An {@link StringParameter} corresponding to the passed
	 *         information.
	 */
	private Parameter<?> processStringParameter(final String name,
			final String value, final String restrs, final String tags) {
		Parameter<?> rets = null;

		String[] toks = restrs.split(",");

		for (int i = 0; i < toks.length; i++) {
			toks[i] = toks[i].trim();
		}

		if (restrs.length() > 0) {
			if ((toks[0].equals("true") && toks[1].equals("false"))
					|| (toks[0].equals("false") && toks[1].equals("true"))) {
				rets = new BoolParameter(name, value);
			} else {
				rets = new StringChoiceParameter(name, toks);
				((StringChoiceParameter) rets).setValue(value);
			}
		} else {
			rets = new StringParameter(name, value);
		}

		return rets;
	}

	@Override
	public INodeConfiguration read(InputStream xmlStream)
			throws CTDNodeConfigurationReaderException {
		try {
			readCTDDocument(xmlStream);

			readPorts();
			readParameters();
			readDescription();
			readCLI();
			readOutputConverters();
			config.setXml(doc.asXML());

			return config;
		} catch (Exception e) {
			throw new CTDNodeConfigurationReaderException(e);
		}
	}

	/**
	 * Processes the &lt;outputConverters/> part of the CTD document.
	 * 
	 * @throws Exception
	 *             Is thrown if the configuration is invalid.
	 */
	private void readOutputConverters() throws Exception {
		Node convertersRoot = doc.selectSingleNode("/tool/outputConverters");

		// check if this CTD contains a cli part
		if (convertersRoot == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<Node> relocators = convertersRoot.selectNodes("//relocators");
		for (Node elem : relocators) {
			processRelocator(elem);
		}

	}

	private void processRelocator(final Node elem) {
		String reference = elem.valueOf("@reference");
		String pattern = elem.valueOf("@pattern");

		config.getRelocators().add(new Relocator(reference, pattern));
	}

	/**
	 * Reads the CTD file given in the xmlStream into the local document.
	 * 
	 * @param xmlStream
	 *            The xmlStream providing the CTD.
	 * @throws Exception
	 *             An {@link Exception} is thrown if the CTD file could not be
	 *             validated or produced other errors while reading it.
	 */
	private void readCTDDocument(InputStream xmlStream) throws Exception {
		SimpleErrorHandler errorHandler = new SimpleErrorHandler();

		try {
			SAXReader reader = initializeSAXReader();
			reader.setErrorHandler(errorHandler);
			doc = reader.read(xmlStream);
		} catch (SAXException ex) {
			throw new Exception("Errror while reading CTD file!", ex);
		} catch (ParserConfigurationException ex) {
			throw new Exception("Errror while reading CTD file!", ex);
		} catch (DocumentException ex) {
			throw new Exception("Errror while reading CTD file!", ex);
		}

		if (!errorHandler.isValid()) {
			System.err.println(errorHandler.getErrorReport());
			throw new Exception("CTD file is not valid !");
		}
	}

	/**
	 * Initializes a SAXReader with the Param and CTD schema.
	 * 
	 * @return A fully configured {@link SAXReader}.
	 * @throws SAXException
	 *             See {@link SAXReader} documentation.
	 * @throws ParserConfigurationException
	 *             See {@link SAXReader} documentation.
	 */
	private SAXReader initializeSAXReader() throws SAXException,
			ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		factory.setSchema(schemaFactory.newSchema(new Source[] {
				new StreamSource(SchemaProvider.class
						.getResourceAsStream("CTD.xsd")),
				new StreamSource(SchemaProvider.class
						.getResourceAsStream("Param_1_3.xsd")) }));

		SAXParser parser = factory.newSAXParser();

		SAXReader reader = new SAXReader(parser.getXMLReader());
		reader.setValidation(false);
		return reader;
	}

	/**
	 * Extracts the cli mapping information from the CLI part of the CTD.
	 * 
	 * @throws Exception
	 *             An exception is thrown if invalid information are contained
	 *             in the CLI elements.
	 */
	private void readCLI() throws Exception {

		Node cliRoot = doc.selectSingleNode("/tool/cli");

		// check if this CTD contains a cli part
		if (cliRoot == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<Node> cliElements = cliRoot.selectNodes("//clielement");
		for (Node elem : cliElements) {
			processCLIElement(elem);
		}
	}

	/**
	 * Convert a single clielement into the corresponding class.
	 * 
	 * @param elem
	 *            The xml node.
	 * @throws Exception
	 *             An exception is thrown if the information contained is
	 *             invalid.
	 */
	private void processCLIElement(final Node elem) throws Exception {
		CLIElement cliElement = new CLIElement();
		// set attributes based on xml values
		cliElement.setOptionIdentifier(elem.valueOf("@optionIdentifier"));
		cliElement.setIsList(elem.valueOf("@isList") == "true");
		cliElement.setRequired(elem.valueOf("@required") == "true");

		// set mapping
		@SuppressWarnings("unchecked")
		List<Node> mappingElements = elem.selectNodes("./mapping");
		for (Node mapping : mappingElements) {
			cliElement.getMapping().add(processMappingElement(mapping));
		}

		validateCLIElement(cliElement);

		// add to the config
		config.getCLI().getCLIElement().add(cliElement);
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

	/**
	 * Processes a single mapping element contained in a {@link CLIElement}.
	 * 
	 * @param mappingElement
	 *            The node containing the mapping information.
	 * @return A fully configured {@link CLIMapping}.
	 * @throws Exception
	 *             An exception is thrown if it is not possible to construct a
	 *             valid {@link CLIMapping} from the given xml node.
	 */
	private CLIMapping processMappingElement(final Node mappingElement)
			throws Exception {
		CLIMapping cliMapping = new CLIMapping();
		String mappingRefName = mappingElement.valueOf("@referenceName");
		cliMapping.setReferenceName(mappingRefName);

		// check if a parameter with the given name was registered
		checkIfMappedParameterExists(cliMapping);
		return cliMapping;
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
		hasPortWithMappingName |= findInPortList(mappingRefName, INPUT_PORTS);
		hasPortWithMappingName |= findInPortList(mappingRefName, OUTPUT_PORTS);

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
}
