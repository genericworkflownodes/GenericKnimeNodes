package org.ballproject.knime.nodegeneration.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.templates.TemplateResources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class PluginXmlTemplate {
	/**
	 * Prepares a new copy of a template plugin.xml and returns its
	 * {@link Document} representation.
	 * 
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static Document getFromTemplate() throws DocumentException,
			IOException {
		File temp = File.createTempFile("plugin", "xml");
		temp.deleteOnExit();
		Helper.copyStream(TemplateResources.class
				.getResourceAsStream("plugin.xml.template"), temp);

		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(new DOMDocumentFactory());

		return reader.read(new FileInputStream(temp));
	}

	/**
	 * Write a given plugin.xml representation to a file.
	 * 
	 * @param pluginXml
	 * @param dest
	 * @throws IOException
	 */
	public static void saveTo(Document pluginXml, File dest) throws IOException {
		XMLWriter writer = new XMLWriter(new FileWriter(dest),
				OutputFormat.createPrettyPrint());
		writer.write(pluginXml);
		writer.close();
	}
}
