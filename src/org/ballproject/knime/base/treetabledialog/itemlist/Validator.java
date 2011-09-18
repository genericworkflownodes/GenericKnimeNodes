package org.ballproject.knime.base.treetabledialog.itemlist;

public interface Validator
{
	boolean validate(String s);
	String getName();
	String getReason();
}