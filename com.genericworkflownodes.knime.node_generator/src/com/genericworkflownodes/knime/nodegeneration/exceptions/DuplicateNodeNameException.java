package com.genericworkflownodes.knime.nodegeneration.exceptions;

public class DuplicateNodeNameException extends Exception {

	private static final long serialVersionUID = 998799239240695103L;

	public DuplicateNodeNameException(String nodeName) {
		super("Duplicate node name \"" + nodeName + "\" detected.");
	}
}
