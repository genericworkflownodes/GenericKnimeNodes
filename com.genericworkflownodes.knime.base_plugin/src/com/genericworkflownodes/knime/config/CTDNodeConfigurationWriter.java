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

package com.genericworkflownodes.knime.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * NodeConfigurationWriter for CTD files.
 * 
 * @author roettig,aiche
 */
public class CTDNodeConfigurationWriter implements INodeConfigurationWriter {

	/**
	 * The xml document.
	 */
	private Document doc;

	/**
	 * Provides access the internal XML document.
	 * 
	 * @return The XML document.
	 */
	protected Document getDocument() {
		return doc;
	}

	/**
	 * Constructor using the original CTD xml as input.
	 * 
	 * @param xml
	 *            The original ctd file as single string.
	 */
	public CTDNodeConfigurationWriter(final String xml) {
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(new StringReader(xml));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		cleanItemLists();
	}

	/**
	 * Removes all default values from list items to enable addition of new
	 * ones.
	 */
	@SuppressWarnings("unchecked")
	private void cleanItemLists() {
		List<Node> itemlists = getDocument().selectNodes("//ITEMLIST");
		for (Node itemlist : itemlists) {
			List<Node> listitems = itemlist.selectNodes("LISTITEM");
			for (Node item : listitems) {
				item.detach();
			}
		}
	}

	@Override
	public void setParameterValue(String name, String value) {
		String[] toks = name.split("\\.");
		String query = "/tool/PARAMETERS/";
		for (int i = 0; i < toks.length - 1; i++) {
			query += "NODE[@name='" + toks[i] + "']/";
		}
		query += "ITEM[@name='" + toks[toks.length - 1] + "']";

		Node node = getDocument().selectSingleNode(query);
		if (node == null) {
			return;
		}
		Element elem = (Element) node;
		elem.addAttribute("value", value);
	}

	@Override
	public void setMultiParameterValue(String name, String value) {
		String[] toks = name.split("\\.");
		String query = "/tool/PARAMETERS/";
		for (int i = 0; i < toks.length - 1; i++) {
			query += "NODE[@name='" + toks[i] + "']/";
		}
		query += "ITEMLIST[@name='" + toks[toks.length - 1] + "']";

		Node node = getDocument().selectSingleNode(query);
		if (node == null) {
			return;
		}
		Element elem = (Element) node;
		Element item = elem.addElement("LISTITEM");
		item.addAttribute("value", value);
	}

	@Override
	public void write(File file) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();

		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(doc);

		writer.close();
	}

	public void init(INodeConfigurationStore store) {
		for (String key : store.getParameterKeys()) {
			List<String> values = store.getMultiParameterValue(key);
			if (values.size() == 1) {
				setParameterValue(key, values.get(0));
			} else {
				for (String value : values) {
					setMultiParameterValue(key, value);
				}
			}
		}
	}
}
