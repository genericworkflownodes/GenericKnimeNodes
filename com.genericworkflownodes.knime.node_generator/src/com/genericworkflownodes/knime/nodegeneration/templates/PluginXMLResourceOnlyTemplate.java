package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;
import com.genericworkflownodes.util.Helper;

public class PluginXMLResourceOnlyTemplate {

    private static final Logger LOGGER = Logger
            .getLogger(PluginXMLResourceOnlyTemplate.class.getCanonicalName());

    private final Document doc;

    /**
     * Constructs a new copy of a template plugin.xml and returns its
     * {@link Document} representation.
     * 
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public PluginXMLResourceOnlyTemplate() throws DocumentException, IOException {
        File temp = File.createTempFile("plugin", "xml");
        temp.deleteOnExit();
        Helper.copyStream(TemplateResources.class
                .getResourceAsStream("plugin.xml.resourceonly.template"), temp);

        SAXReader reader = new SAXReader();
        reader.setDocumentFactory(new DOMDocumentFactory());

        doc = reader.read(new FileInputStream(temp));
    }

    /**
     * Write a given plugin.xml representation to a file.
     * 
     * @param pluginXml
     * @param dest
     * @throws IOException
     */
    public void saveTo(File dest) throws IOException {
        Utils.writeDocumentTo(doc, dest);
    }

  
    /**
     * Registers the given node (clazz, path) in the plugin.xml file.
     * 
     * @param clazz
     * @param path
     */
    public void registerDLLProviderClass(String clazz, String name) {
        LOGGER.info("registering resource provider " + clazz);

        Node node = doc
                .selectSingleNode("/plugin/extension[@point='com.genericworkflownodes.knime.custom.config.DLLProvider']");
        Element elem = (Element) node;

        elem.addElement("dllprovider").addAttribute("class", clazz).addAttribute("name", name);
    }

}
