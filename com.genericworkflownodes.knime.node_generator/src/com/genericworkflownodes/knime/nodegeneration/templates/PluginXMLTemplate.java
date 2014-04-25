package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

import com.genericworkflownodes.knime.nodegeneration.model.files.MimeTypesFile.MIMETypeEntry;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;
import com.genericworkflownodes.util.Helper;

public class PluginXMLTemplate {

    private static final Logger LOGGER = Logger
            .getLogger(PluginXMLTemplate.class.getCanonicalName());

    private final Document doc;
    private final Set<String> registeredPrefixed = new HashSet<String>();

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
     * Registers an icon that is displayed on the KNIME splash screen when KNIME
     * starts.
     * 
     * @param meta
     * 
     * @param splashIcon
     *            project relative path to icon file, e.g. icons/logo.png
     * @throws IOException
     */
    public void registerSplashIcon(GeneratedPluginMeta meta, File splashIcon)
            throws IOException {
        if (splashIcon == null) {
            return;
        }
        Node node = doc
                .selectSingleNode("/plugin/extension[@point='org.knime.product.splashExtension']");
        Element elem = (Element) node;
        elem.addElement("splashExtension")
                .addAttribute("icon", splashIcon.getPath())
                .addAttribute("id", meta.getPackageRoot() + ".icons.splashIcon");
    }

    /**
     * Registers recursively the category path of the current node.
     * 
     * @param path
     */
    public void registerPath(String path) {
        List<String> prefixes = Utils.getPathPrefixes(path);
        for (String prefix : prefixes) {
            registerPathPrefix(prefix);
        }
    }

    /**
     * Registers the current prefix of the path.
     * 
     * @param path
     */
    private void registerPathPrefix(String path) {
        // do not register any top level or root path
        if ("/".equals(path) || new File(path).getParent().equals("/")
                || "".equals(path)) {
            return;
        }

        if (registeredPrefixed.contains(path)) {
            return;
        }

        LOGGER.info("Registering path prefix: " + path);

        registeredPrefixed.add(path);

        String categoryName = Utils.getPathSuffix(path);
        String categoryPath = Utils.getPathPrefix(path);

        Node node = doc
                .selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.categories']");

        Element elem = (Element) node;
        LOGGER.info("name=" + categoryName);

        elem.addElement("category").addAttribute("description", path)
                .addAttribute("icon", "icons/category.png")
                .addAttribute("path", categoryPath)
                .addAttribute("name", categoryName)
                .addAttribute("level-id", categoryName);
    }

    /**
     * Registers the given node (clazz, path) in the plugin.xml file.
     * 
     * @param clazz
     * @param path
     */
    public void registerNode(String clazz, String path) {
        LOGGER.info("registering Node " + clazz);
        registerPath(path);

        Node node = doc
                .selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
        Element elem = (Element) node;

        elem.addElement("node").addAttribute("factory-class", clazz)
                .addAttribute("id", clazz).addAttribute("category-path", path);
    }

    public void registerMIMETypeEntries(List<MIMETypeEntry> mimeTypes) {
        // <mimetype name="mzML">
        // <fileextension name="mzML"></fileextension>
        // </mimetype>
        // <mimetype name="mzXML">
        // <fileextension name="mzXML"></fileextension>
        // </mimetype>

        Node node = doc
                .selectSingleNode("/plugin/extension[@point='org.knime.base.filehandling.mimetypes']");
        Element mimeTypesExtensionPoint = (Element) node;

        for (MIMETypeEntry mimeTypeEntry : mimeTypes) {
            Element mimeTypeElement = mimeTypesExtensionPoint.addElement(
                    "mimetype").addAttribute("name", mimeTypeEntry.getType());

            // register fileExtensions
            for (String fileExtension : mimeTypeEntry.getExtensions()) {
                mimeTypeElement.addElement("fileextension").addAttribute(
                        "name", fileExtension);
            }
        }

    }
}
