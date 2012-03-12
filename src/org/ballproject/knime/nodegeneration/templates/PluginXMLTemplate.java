package org.ballproject.knime.nodegeneration.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.util.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

public class PluginXMLTemplate {

	private static final Logger LOGGER = Logger
			.getLogger(PluginXMLTemplate.class.getCanonicalName());

	private Document doc;
	private Set<String> registeredPrefixed = new HashSet<String>();

	/**
	 * Constructs a new copy of a template plugin.xml and returns its
	 * {@link Document} representation.
	 * 
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public PluginXMLTemplate() throws DocumentException, IOException {
		File temp = File.createTempFile("plugin", "xml");
		temp.deleteOnExit();
		Helper.copyStream(TemplateResources.class
				.getResourceAsStream("plugin.xml.template"), temp);

		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(new DOMDocumentFactory());

		this.doc = reader.read(new FileInputStream(temp));
	}

	/**
	 * Write a given plugin.xml representation to a file.
	 * 
	 * @param pluginXml
	 * @param dest
	 * @throws IOException
	 */
	public void saveTo(File dest) throws IOException {
		Utils.writeDocumentTo(this.doc, dest);
	}

	// TODO: documentation
	public void registerPath(String path) {
		List<String> prefixes = Utils.getPathPrefixes(path);
		for (String prefix : prefixes) {
			this.registerPathPrefix(prefix);
		}
	}

	// TODO: documentation
	private void registerPathPrefix(String path) {
		// do not register any top level or root path
		// TODO: why?
		if (path.equals("/") || new File(path).getParent().equals("/"))
			return;

		if (this.registeredPrefixed.contains(path))
			return;

		LOGGER.info("Registering path prefix: " + path);

		this.registeredPrefixed.add(path);

		String categoryName = Utils.getPathSuffix(path);
		String categoryPath = Utils.getPathPrefix(path);

		Node node = this.doc
				.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.categories']");

		Element elem = (Element) node;
		LOGGER.info("name=" + categoryName);

		elem.addElement("category").addAttribute("description", path)
				.addAttribute("icon", "icons/category.png")
				.addAttribute("path", categoryPath)
				.addAttribute("name", categoryName)
				.addAttribute("level-id", categoryName);
	}

	// TODO: documentation
	public void registerNode(String clazz, String path) {
		LOGGER.info("registering Node " + clazz);
		this.registerPath(path);

		Node node = this.doc
				.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
		Element elem = (Element) node;

		elem.addElement("node").addAttribute("factory-class", clazz)
				.addAttribute("id", clazz).addAttribute("category-path", path);
	}
}
