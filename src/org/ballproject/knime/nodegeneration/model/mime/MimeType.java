package org.ballproject.knime.nodegeneration.model.mime;

import org.dom4j.Element;

public class MimeType {
	private final String name;
	private final String ext;
	private final String descr;
	private final String demangler;
	private final String binary;

	public MimeType(Element element) {
		name = element.valueOf("@name");
		ext = element.valueOf("@ext");
		descr = element.valueOf("@description");
		demangler = element.valueOf("@demangler");
		binary = (element.valueOf("@binary").equals("") ? "false" : element
				.valueOf("@binary"));
	}

	public String getName() {
		return name;
	}

	public String getExt() {
		return ext;
	}

	public String getDescr() {
		return descr;
	}

	public String getDemangler() {
		return demangler;
	}

	public String getBinary() {
		return binary;
	}
}
