/*
 * Copyright (c) 2011, Marc Röttig.
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

package org.ballproject.knime.base.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.schemas.SchemaProvider;
import org.ballproject.knime.base.schemas.SimpleErrorHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;

public class CTDFileNodeConfigurationReader implements INodeConfigurationReader {

	@SuppressWarnings("unused")
	private static Logger log = Logger
			.getLogger(CTDFileNodeConfigurationReader.class.getCanonicalName());

	private Document doc;
	private NodeConfiguration config = new NodeConfiguration();

	public CTDFileNodeConfigurationReader() {
	}

	protected String SECTION_NODE_NAME = "NODE";
	protected String INPUTFILE_TAG = "input file";
	protected String OUTPUTFILE_TAG = "output file";

	protected Set<String> captured_ports = new HashSet<String>();

	private static List<Port> in_ports;
	private static List<Port> out_ports;

	private void readPorts() throws Exception {
		in_ports = new ArrayList<Port>();
		out_ports = new ArrayList<Port>();

		Node node = this.doc.selectSingleNode("/tool/PARAMETERS");
		Element root = (Element) node;
		this.processIOPorts(root);

		this.config.setInports(in_ports.toArray(new Port[in_ports.size()]));
		this.config.setOutports(out_ports.toArray(new Port[out_ports.size()]));
	}

	@SuppressWarnings("unchecked")
	public void processIOPorts(Element root) throws Exception {
		List<Node> items = root.selectNodes("//ITEM[contains(@tags,'"
				+ this.OUTPUTFILE_TAG + "')]");
		for (Node n : items) {
			this.createPortFromNode(n, false);
		}
		items = root.selectNodes("//ITEM[contains(@tags,'" + this.INPUTFILE_TAG
				+ "')]");
		for (Node n : items) {
			this.createPortFromNode(n, false);
		}
		items = root.selectNodes("//ITEMLIST[contains(@tags,'"
				+ this.INPUTFILE_TAG + "')]");
		for (Node n : items) {
			this.createPortFromNode(n, true);
		}
		items = root.selectNodes("//ITEMLIST[contains(@tags,'"
				+ this.OUTPUTFILE_TAG + "')]");
		for (Node n : items) {
			this.createPortFromNode(n, true);
		}
	}

	@SuppressWarnings("unchecked")
	public void readParameters() throws Exception {
		Node root = this.doc.selectSingleNode("/tool/PARAMETERS");
		List<Node> items = root.selectNodes("//ITEM[not(contains(@tags,'"
				+ this.OUTPUTFILE_TAG + "')) and not(contains(@tags,'"
				+ this.INPUTFILE_TAG + "'))]");
		for (Node n : items) {
			this.processItem(n);
		}
		items = root.selectNodes("//ITEMLIST[not(contains(@tags,'"
				+ this.OUTPUTFILE_TAG + "')) and not(contains(@tags,'"
				+ this.INPUTFILE_TAG + "'))]");
		for (Node n : items) {
			this.processMultiItem(n);
		}
	}

	public String getPath(Node n) {

		List<String> path_nodes = new ArrayList<String>();
		while (n != null && !n.getName().equals("PARAMETERS")) {
			path_nodes.add(n.valueOf("@name"));
			n = n.getParent();
		}

		Collections.reverse(path_nodes);

		String ret = "";
		int N = path_nodes.size();
		for (int i = 0; i < N; i++) {
			if (i == N - 1) {
				ret += path_nodes.get(i);
			} else {
				ret += path_nodes.get(i) + ".";
			}
		}
		return ret;
	}

	private void createPortFromNode(Node node, boolean multi) throws Exception {

		Element elem = (Element) node;

		String name = node.valueOf("@name");
		String descr = node.valueOf("@description");
		String tags = node.valueOf("@tags");

		if (name.equals("write_ini") || name.equals("write_par")
				|| name.equals("par") || name.equals("help")) {
			return;
		}

		Port port = new Port();

		port.setMultiFile(multi);

		if (tags.contains(this.INPUTFILE_TAG)
				|| tags.contains(this.OUTPUTFILE_TAG)) {
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

			String path = this.getPath(node);
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
				port.addMimeType(new MIMEtype(mt.trim()));
			}

		}
		if (tags.contains(this.OUTPUTFILE_TAG)) {
			out_ports.add(port);
			this.captured_ports.add(port.getName());

			if (multi) {
				String path = this.getPath(node);
				FileListParameter param = new FileListParameter(name,
						new ArrayList<String>());
				param.setPort(port);
				param.setDescription(descr);
				param.setIsOptional(false);
				this.config.addParameter(path, param);
			}
		}
		if (tags.contains(this.INPUTFILE_TAG)) {
			in_ports.add(port);
			this.captured_ports.add(port.getName());
		}

	}

	/**
	 * Converts a single item into a {@link Parameter}.
	 * 
	 * @param elem
	 *            The current node.
	 */
	public void processItem(Node elem) {
		String name = elem.valueOf("@name");

		String path = this.getPath(elem);

		if (this.captured_ports.contains(path)) {
			return;
		}

		if (name.equals("write_ini") || name.equals("write_par")
				|| name.equals("par") || name.equals("help")) {
			return;
		}

		Parameter<?> param = this.getParameterFromNode(elem);
		this.config.addParameter(path, param);
	}

	/**
	 * Converts an item corresponding to a list (ITEMLIST) to an equivalent
	 * {@link Parameter}.
	 * 
	 * @param elem
	 *            The xml element.
	 */
	public void processMultiItem(Node elem) {
		String name = elem.valueOf("@name");

		String path = this.getPath(elem);

		if (this.captured_ports.contains(path)) {
			return;
		}

		if (name.equals("write_ini") || name.equals("write_par")
				|| name.equals("par") || name.equals("help")) {
			return;
		}

		Parameter<?> param = this.getMultiParameterFromNode(elem);
		this.config.addParameter(path, param);
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
		Node node = this.doc.selectSingleNode("/tool");
		if (node == null) {
			throw new Exception("CTD has no root named tool");
		}

		String lstatus = node.valueOf("@status");
		if (lstatus != null && lstatus.equals("")) {
			throw new Exception("CTD has no status");
		}

		node = this.doc.selectSingleNode("/tool/name");
		if (node == null) {
			throw new Exception("CTD has no tool name");
		}
		String name = node.valueOf("text()");
		if (name.equals("")) {
			throw new Exception("CTD has no tool name");
		}
		this.config.setName(name);

		node = this.doc.selectSingleNode("/tool/description");
		String sdescr = "";
		if (node != null) {
			sdescr = node.valueOf("text()");
		}
		this.config.setDescription(sdescr);

		node = this.doc.selectSingleNode("/tool/path");
		String spath = "";
		if (node != null) {
			spath = node.valueOf("text()");
		}

		this.config.setCommand(spath);

		node = this.doc.selectSingleNode("/tool/manual");
		String ldescr = "";
		if (node != null) {
			ldescr = node.valueOf("text()");
		}
		this.config.setManual(ldescr);

		node = this.doc.selectSingleNode("/tool/version");
		String lversion = "";
		if (node != null) {
			lversion = node.valueOf("text()");
		}
		this.config.setVersion(lversion);

		node = this.doc.selectSingleNode("/tool/docurl");
		String docurl = "";
		if (node != null) {
			docurl = node.valueOf("text()");
		}
		this.config.setDocUrl(docurl);

		node = this.doc.selectSingleNode("/tool/category");
		String cat = "";
		if (node != null) {
			cat = node.valueOf("text()");
		}
		this.config.setCategory(cat);
	}

	/**
	 * Convert an node in the CTD document into a {@link Parameter}.
	 * 
	 * @param node
	 *            The xml node of CTD document.
	 * @return a {@link Parameter} equivalent to the xml node.
	 */
	private Parameter<?> getParameterFromNode(final Node node) {
		Parameter<?> ret = null;
		String type = node.valueOf("@type");
		String name = node.valueOf("@name");
		String value = node.valueOf("@value");
		String restrs = node.valueOf("@restrictions");
		String descr = node.valueOf("@description");

		String tags = node.valueOf("@tags");
		Set<String> tagset = tokenSet(tags);

		if (type.toLowerCase().equals("double")
				|| type.toLowerCase().equals("float")) {
			ret = this.processDoubleParameter(name, value, restrs, tags);
		} else {
			if (type.toLowerCase().equals("int")) {
				ret = this.processIntParameter(name, value, restrs, tags);
			} else {
				if (type.toLowerCase().equals("string")) {
					ret = this
							.processStringParameter(name, value, restrs, tags);
				}
			}
		}

		ret.setDescription(descr);

		if (tagset.contains("mandatory") || tagset.contains("required")) {
			ret.setIsOptional(false);
		}

		if (tagset.contains("advanced")) {
			ret.setAdvanced(true);
		}

		return ret;
	}

	/**
	 * Creates a {@link ListParameter} from the given xml node.
	 * 
	 * @param node
	 *            The xml node.
	 * @return A {@link ListParameter} corresponding to the xml node.
	 */
	private Parameter<?> getMultiParameterFromNode(Node node) {
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
			param = this.processDoubleListParameter(name, values, restrs, tags);
		} else {
			if (type.toLowerCase().equals("int")) {
				param = this
						.processIntListParameter(name, values, restrs, tags);
			} else {
				if (type.toLowerCase().equals("string")) {
					param = this.processStringListParameter(name, values,
							restrs, tags);
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
	 * @return A {@link ListParameter}.
	 */
	private Parameter<?> processStringListParameter(String name,
			List<String> values, String restrs, String tags) {
		return new StringListParameter(name, values);
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
	 * @return A {@link ListParameter}.
	 */
	private Parameter<?> processIntListParameter(String name,
			List<String> values, String restrs, String tags) {
		List<Integer> vals = new ArrayList<Integer>();
		for (String currentValue : values) {
			vals.add(Integer.parseInt(currentValue));
		}

		IntegerListParameter ret = new IntegerListParameter(name, vals);

		Integer[] bounds = new Integer[2];
		this.getIntegerBoundsFromRestrictions(restrs, bounds);
		ret.setLowerBound(bounds[0]);
		ret.setUpperBound(bounds[1]);

		return ret;
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
	 * @return A {@link ListParameter}.
	 */
	private Parameter<?> processDoubleListParameter(String name,
			List<String> values, String restrs, String tags) {
		List<Double> vals = new ArrayList<Double>();
		for (String currentValue : values) {
			vals.add(Double.parseDouble(currentValue));
		}

		DoubleListParameter ret = new DoubleListParameter(name, vals);

		Double[] bounds = new Double[2];
		this.getDoubleBoundsFromRestrictions(restrs, bounds);
		ret.setLowerBound(bounds[0]);
		ret.setUpperBound(bounds[1]);

		return ret;
	}

	/**
	 * Extracts upper and lower bounds from the given restrictions string.
	 * 
	 * @param restrictions
	 *            The string containing the restrictions.
	 * @param bounds
	 *            The array where the restrictions will be stored.
	 */
	private void getDoubleBoundsFromRestrictions(String restrictions,
			Double[] bounds) {
		Double upperBound = Double.POSITIVE_INFINITY;
		Double lowerBound = Double.NEGATIVE_INFINITY;

		if (restrictions.equals("")) {
			bounds[0] = lowerBound;
			bounds[1] = upperBound;
			return;
		}

		String[] toks = restrictions.split(":");
		if (toks.length != 0) {
			if (toks[0].equals("")) {
				// upper bounded only
				double ub;
				try {
					ub = Double.parseDouble(toks[1]);
				} catch (NumberFormatException e) {
					throw new RuntimeException(e);
				}
				upperBound = ub;
			} else {
				// lower and upper bounded
				if (toks.length == 2) {
					double lb;
					double ub;
					try {
						lb = Double.parseDouble(toks[0]);
						ub = Double.parseDouble(toks[1]);
					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
					}
					lowerBound = lb;
					upperBound = ub;
				} else {
					// lower bounded only
					double lb;
					try {
						lb = Double.parseDouble(toks[0]);
					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
					}
					lowerBound = lb;
				}
			}
		}
		bounds[0] = lowerBound;
		bounds[1] = upperBound;
	}

	private Parameter<?> processDoubleParameter(String name, String value,
			String restrs, String tags) {
		DoubleParameter retd = new DoubleParameter(name, value);
		Double[] bounds = new Double[2];
		this.getDoubleBoundsFromRestrictions(restrs, bounds);
		retd.setLowerBound(bounds[0]);
		retd.setUpperBound(bounds[1]);
		return retd;
	}

	private static Set<String> tokenSet(String s) {
		Set<String> ret = new HashSet<String>();
		String[] toks = s.split(",");
		for (String tok : toks) {
			ret.add(tok);
		}
		return ret;
	}

	/**
	 * Extracts upper and lower bounds from the given restrictions string.
	 * 
	 * @param restrictions
	 *            The string containing the restrictions.
	 * @param bounds
	 *            The array where the restrictions will be stored.
	 */
	private void getIntegerBoundsFromRestrictions(String restrictions,
			Integer[] bounds) {
		Integer upperBound = Integer.MAX_VALUE;
		Integer lowerBound = Integer.MIN_VALUE;

		if (restrictions.equals("")) {
			bounds[0] = lowerBound;
			bounds[1] = upperBound;
			return;
		}

		String[] toks = restrictions.split(":");
		if (toks.length != 0) {
			if (toks[0].equals("")) {
				// upper bounded only
				int ub;
				try {
					ub = Integer.parseInt(toks[1]);
				} catch (NumberFormatException e) {
					throw new RuntimeException(e);
				}
				upperBound = ub;
			} else {
				// lower and upper bounded
				if (toks.length == 2) {
					int lb;
					int ub;
					try {
						lb = Integer.parseInt(toks[0]);
						ub = Integer.parseInt(toks[1]);
					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
					}
					lowerBound = lb;
					upperBound = ub;
				} else {
					// lower bounded only
					int lb;
					try {
						lb = Integer.parseInt(toks[0]);
					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
					}
					lowerBound = lb;
				}
			}
		}
		bounds[0] = lowerBound;
		bounds[1] = upperBound;
	}

	private Parameter<?> processIntParameter(String name, String value,
			String restrs, String tags) {
		IntegerParameter reti = new IntegerParameter(name, value);
		Integer[] bounds = new Integer[2];
		this.getIntegerBoundsFromRestrictions(restrs, bounds);
		reti.setLowerBound(bounds[0]);
		reti.setUpperBound(bounds[1]);
		return reti;
	}

	private Parameter<?> processStringParameter(String name, String value,
			String restrs, String tags) {
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
	public INodeConfiguration read(InputStream xmlstream)
			throws CTDNodeConfigurationReaderException {
		try {
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

			SimpleErrorHandler errorHandler = new SimpleErrorHandler();

			reader.setErrorHandler(errorHandler);

			this.doc = reader.read(xmlstream);

			if (!errorHandler.isValid()) {
				System.err.println(errorHandler.getErrorReport());
				throw new Exception("CTD file is not valid !");
			}

			this.readPorts();
			this.readParameters();
			this.readDescription();
			this.readCLI();
			this.config.setXml(this.doc.asXML());

			return this.config;
		} catch (Exception e) {
			throw new CTDNodeConfigurationReaderException(e);
		}
	}

	/**
	 * Extracts the cli mapping information from the CLI part of the CTD.
	 * 
	 * @throws Exception
	 *             An exception is thrown if invalid information are contained
	 *             in the CLI elements.
	 */
	private void readCLI() throws Exception {

		Node cliRoot = this.doc.selectSingleNode("/tool/cli");

		// check if this CTD contains a cli part
		if (cliRoot == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<Node> cliElements = cliRoot.selectNodes("//clielement");
		for (Node elem : cliElements) {
			this.processCLIElement(elem);
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
	private void processCLIElement(Node elem) throws Exception {
		CLIElement cliElement = new CLIElement();
		// set attributes based on xml values
		cliElement.setName(elem.valueOf("@name"));
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
		this.config.getCLI().getCLIElement().add(cliElement);
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
				if (config.getParameter(mapping.getRefName()) != null) {
					// check that it is not boolean
					if (config.getParameter(mapping.getRefName()) instanceof BoolParameter) {
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
	private CLIMapping processMappingElement(Node mappingElement)
			throws Exception {
		CLIMapping cliMapping = new CLIMapping();
		String mappingRefName = mappingElement.valueOf("@ref_name");
		cliMapping.setRefName(mappingRefName);

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
		if (config.getParameter(cliMapping.getRefName()) == null
				&& !portWithRefNameExists(cliMapping.getRefName())) {
			throw new Exception("Unknown Parameter " + cliMapping.getRefName());
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
		hasPortWithMappingName |= findInPortList(mappingRefName, in_ports);
		hasPortWithMappingName |= findInPortList(mappingRefName, out_ports);

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
