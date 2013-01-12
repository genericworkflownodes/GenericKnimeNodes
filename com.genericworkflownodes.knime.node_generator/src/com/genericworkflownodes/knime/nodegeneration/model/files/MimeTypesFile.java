package com.genericworkflownodes.knime.nodegeneration.model.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import com.genericworkflownodes.knime.nodegeneration.model.mime.MimeType;
import com.genericworkflownodes.knime.schemas.SchemaProvider;
import com.genericworkflownodes.knime.schemas.SchemaValidator;

public class MimeTypesFile extends File {

	private static final long serialVersionUID = -1620704972604551679L;

	private static void validate(File file) throws DocumentException {
		SchemaValidator val = new SchemaValidator();
		val.addSchema(SchemaProvider.class.getResourceAsStream("mimetypes.xsd"));
		if (!val.validates(file.getPath())) {
			throw new DocumentException("Supplied \"" + file.getPath()
					+ "\" does not conform to schema " + val.getErrorReport());
		}
	}

	private static Document createDocument(File file)
			throws FileNotFoundException, DocumentException {
		DOMDocumentFactory factory = new DOMDocumentFactory();
		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(factory);
		return reader.read(new FileInputStream(file));
	}

	private static List<MimeType> readMimeTypes(Document doc)
			throws JaxenException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("bp", "http://www.ball-project.org/mimetypes"); // TODO

		Dom4jXPath xpath = new Dom4jXPath("//bp:mimetype");
		xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		@SuppressWarnings("unchecked")
		List<Node> nodes = xpath.selectNodes(doc);
		List<MimeType> mimeTypes = new ArrayList<MimeType>();
		for (Node node : nodes) {
			Element element = (Element) node;
			mimeTypes.add(new MimeType(element));
		}
		return mimeTypes;
	}

	private List<MimeType> mimeTypes;

	public MimeTypesFile(File file) throws IOException, DocumentException,
			JaxenException {
		super(file.getPath());

		if (file == null || !file.canRead()) {
			throw new IOException("Invalid MIME types file: " + file.getPath());
		}

		validate(file);

		Document doc = createDocument(file);
		this.mimeTypes = readMimeTypes(doc);
	}

	/**
	 * A list of {@link MimeType}s contained in the given {@link MimeTypesFile}.
	 * 
	 * @return A list of {@link MimeType}s contained in the given
	 *         {@link MimeTypesFile}.
	 */
	public final List<MimeType> getMimeTypes() {
		return mimeTypes;
	}

}
