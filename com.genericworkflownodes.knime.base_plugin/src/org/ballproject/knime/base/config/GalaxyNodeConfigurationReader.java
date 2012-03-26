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
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.schemas.SimpleErrorHandler;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class GalaxyNodeConfigurationReader implements INodeConfigurationReader {
	protected Document doc;
	protected NodeConfiguration config = new NodeConfiguration();

	@Override
	public INodeConfiguration read(InputStream in) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		SAXReader reader = new SAXReader(parser.getXMLReader());
		reader.setValidation(false);

		SimpleErrorHandler errorHandler = new SimpleErrorHandler();

		reader.setErrorHandler(errorHandler);

		doc = reader.read(in);

		if (!errorHandler.isValid()) {
			System.err.println(errorHandler.getErrorReport());
			throw new Exception("Galaxy tool xml file is not valid !");
		}

		readPorts();
		readParameters();
		readDescription();

		config.setXml(doc.asXML());

		return config;
	}

	private void readDescription() throws Exception {
		String descr = doc.valueOf("/tool/description/text()");
		config.setDescription(descr);

		String name = doc.valueOf("/tool/@name");
		config.setName(name);

		String version = doc.valueOf("/tool/@version");
		config.setVersion(version);

		String help = doc.valueOf("/tool/help/text()");
		config.setManual(help);
	}

	private void readParameters() throws Exception {
		List<Node> nodes = DOMHelper.selectNodes(doc,
				"/tool/inputs/param[@type='text']");
		for (Node n : nodes) {
			processParameter(n);
		}
		nodes = DOMHelper.selectNodes(doc, "/tool/inputs/param[@type='float']");
		for (Node n : nodes) {
			processParameter(n);
		}
		nodes = DOMHelper.selectNodes(doc,
				"/tool/inputs/param[@type='boolean']");
		for (Node n : nodes) {
			processParameter(n);
		}
		nodes = DOMHelper.selectNodes(doc,
				"/tool/inputs/param[@type='integer']");
		for (Node n : nodes) {
			processParameter(n);
		}
		nodes = DOMHelper
				.selectNodes(doc, "/tool/inputs/param[@type='select']");
		for (Node n : nodes) {
			processParameter(n);
		}
	}

	private void processParameter(Node n) throws Exception {
		Parameter<?> ret = null;
		String type = n.valueOf("@type");
		if (type.equals(""))
			throw new Exception("type information for parameter not set");

		String key = n.valueOf("@name");
		String val = n.valueOf("@value");

		System.out.println("processing param " + key + " type:" + type);

		if (type.equals("integer"))
			ret = new IntegerParameter(key, val);
		if (type.equals("float"))
			ret = new DoubleParameter(key, val);
		if (type.equals("boolean"))
			ret = new BoolParameter(key, val);
		if (type.equals("text"))
			ret = new StringParameter(key, val);
		if (type.equals("select")) {
			List<Node> options = DOMHelper.selectNodes(n, "option");
			List<String> opts = new ArrayList<String>();
			List<String> labs = new ArrayList<String>();
			for (Node option : options) {
				String optval = option.valueOf("@value");
				String label = option.valueOf("text()");
				opts.add(optval);
				labs.add(label);
			}
			ret = new StringChoiceParameter(key, opts, labs);
		}
		String descr = n.valueOf("label/text()");

		if (ret != null) {
			ret.setKey(key);
			ret.setDescription(descr);
		}

		config.addParameter(key, ret);
	}

	private void readPorts() throws Exception {
		List<Node> nodes = DOMHelper.selectNodes(doc,
				"/tool/inputs/param[@type='data']");
		for (Node n : nodes) {
			Port port = readInPort(n);
			inports.add(port);
		}

		nodes = DOMHelper.selectNodes(doc, "/tool/outputs/data");
		for (Node n : nodes) {
			Port port = readOutPort(n);
			outports.add(port);
		}

		config.setInports((Port[]) inports.toArray(new Port[inports.size()]));
		config.setOutports((Port[]) outports.toArray(new Port[outports.size()]));

	}

	protected List<Port> inports = new ArrayList<Port>();
	protected List<Port> outports = new ArrayList<Port>();

	private Port readInPort(Node portnode) throws Exception {
		Port port = new Port();

		port.setOptional(true);

		Node n = DOMHelper.selectSingleNode(portnode, "label");

		String portdescr = n.valueOf("text()");
		port.setDescription(portdescr);

		String extension = DOMHelper.valueOf(portnode, "@format");
		port.addMimeType(new MIMEtype(extension));

		String portname = DOMHelper.valueOf(portnode, "@name");
		port.setName(portname);

		String optional = DOMHelper.valueOf(portnode, "@optional");
		if (optional.equals("false"))
			port.setOptional(false);

		return port;
	}

	private Port readOutPort(Node portnode) throws Exception {
		Port port = new Port();

		port.setDescription("");

		String extension = DOMHelper.valueOf(portnode, "@format");
		port.addMimeType(new MIMEtype(extension));

		String portname = DOMHelper.valueOf(portnode, "@name");
		port.setName(portname);

		return port;
	}
}
