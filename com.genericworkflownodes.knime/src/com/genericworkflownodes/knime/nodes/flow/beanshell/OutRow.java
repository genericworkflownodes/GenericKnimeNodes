package com.genericworkflownodes.knime.nodes.flow.beanshell;

import java.util.ArrayList;
import java.util.List;

public final class OutRow {
	private List<Object> values = new ArrayList<Object>();
	private List<String> names = new ArrayList<String>();
	private boolean isNull = true;

	public void addCell(String name, Object value) {
		isNull = false;
		values.add(value);
		names.add(name);
	}

	public void addCell(Object value) {
		isNull = false;
		values.add(value);
		names.add(String.format("column %d", names.size()));
	}

	public void addDoubleCell(String name, Object value) {
		isNull = false;
		addCell(name, Double.parseDouble(value.toString()));
	}

	public void addIntCell(String name, Object value) {
		addCell(name, Integer.parseInt(value.toString()));
	}

	public void addStringCell(String name, Object value) {
		isNull = false;
		addCell(name, value.toString());
	}

	public void addDoubleCell(Object value) {
		addDoubleCell(String.format("column %d", names.size()), value);
	}

	public void addIntCell(Object value) {
		addIntCell(String.format("column %d", names.size()), value);
	}

	public void addStringCell(Object value) {
		addStringCell(String.format("column %d", names.size()), value);
	}

	public List<Object> getValues() {
		return values;
	}

	public List<String> getNames() {
		return names;
	}

	public boolean isNull() {
		return isNull;
	}

	public List<Class<?>> getTypes() {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for (Object o : values) {
			ret.add(o.getClass());
		}
		return ret;
	}
}