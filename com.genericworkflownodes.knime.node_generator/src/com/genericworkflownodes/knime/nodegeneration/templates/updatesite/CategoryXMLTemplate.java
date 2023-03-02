package com.genericworkflownodes.knime.nodegeneration.templates.updatesite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.UpdateSiteMeta.Category;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;
import com.genericworkflownodes.util.Helper;

public class CategoryXMLTemplate {

    //private static final Logger LOGGER = Logger
    //        .getLogger(CategoryXMLTemplate.class.getCanonicalName());

    private final Document doc;
    
    /**
     * Ids of categories that were already added.
     */
    private final Set<String> registeredCategories = new HashSet<String>();

    /**
     * Constructs a new copy of a template plugin.xml and returns its
     * {@link Document} representation.
     * 
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public CategoryXMLTemplate() throws DocumentException, IOException {
        File temp = File.createTempFile("category", "xml");
        temp.deleteOnExit();
        Helper.copyStream(CategoryXMLTemplate.class
                .getResourceAsStream("category.xml.template"), temp);

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
     * Adds the GKN feature with the current version to the update site
     */
    public void registerGKNFeature() {
        Node node = doc
                .selectSingleNode("/site");

        Element elem = (Element) node;
        
        // TODO can we somehow put the version of this plugin in here
        //  so we don't need to update?
        elem.addElement("feature")
        	.addAttribute("id", "com.genericworkflownodes.knime.feature")
            .addAttribute("version", "1.1.0");
    }
    
    /**
     * Register feature
     * 
     * @param feature meta info
     */
    public void registerFeature(FeatureMeta fmeta) {
        Node node = doc
                .selectSingleNode("/site");

        Element elem = (Element) node;
        
        String catId = fmeta.getCategory();
        String catDescription = catId;
        String catLabel = catId;
        
        // For now we add an "All" category. It is very confusing to have an "Uncategorized"
        //  category, which is I think even disabled by default in Eclipse.
        if (catId.isBlank()) {
        	catId = "all";
        	catLabel = "All (other) features";
        	catDescription = "All (other) features";
        }

        elem.addElement("feature")
        	.addAttribute("id", fmeta.getId())
            .addAttribute("version", fmeta.getVersion())
        	.addElement("category")
        	.addAttribute("name", catId);
        
        // We assume/require that all user-defined categories were added already.
        // Therefore we create new entries for "forgotten" ones. Not sure if
        // Eclipse/tycho would throw an error otherwise.
    	registerCategory(new Category(catId, catDescription, catLabel));
    }
    
    /**
     * Register category
     * 
     * @param categoryName
     * @param description
     */
    public void registerCategory(Category c) {
    	String id = categoryNameToID(c.getName());
    	if (!registeredCategories.contains(id))
    	{
            Node node = doc
                    .selectSingleNode("/site");

            Element elem = (Element) node;

            elem.addElement("category-def")
        		.addAttribute("name", id)
        		.addAttribute("label", c.getLabel())
            		.addElement("description")
                	.addText(c.getDescription());
            
            registeredCategories.add(id);
    	}
    }
    

    private String categoryNameToID(String categoryname)
    {
    	return categoryname.replaceAll("[^A-Za-z0-9\\.\\-]", ".");
    }
}
