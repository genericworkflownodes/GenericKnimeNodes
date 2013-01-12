package com.genericworkflownodes.knime.nodegeneration.model.mime;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MimeType other = (MimeType) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
