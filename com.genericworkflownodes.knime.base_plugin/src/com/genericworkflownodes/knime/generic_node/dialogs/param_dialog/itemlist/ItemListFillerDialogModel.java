/*
 * Copyright (c) 2011, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;

public class ItemListFillerDialogModel extends AbstractListModel {
	private static final long serialVersionUID = 3187917698500696806L;
	protected List<String> data = new ArrayList<String>();
	protected Validator validator;
	protected boolean setLike = false;
	protected String[] values;

	public ItemListFillerDialogModel() {
	}

	public ItemListFillerDialogModel(List<String> items) {
		for (String item : items) {
			data.add(item);
		}
	}

	public ItemListFillerDialogModel(String[] items) {
		for (String item : items) {
			data.add(item);
		}
	}

	public void setSetLike(boolean flag) {
		setLike = true;
	}

	public boolean isSetLike() {
		return setLike;
	}

	public boolean hasRestrictedValues() {
		return values != null;
	}

	public String[] getRestrictedValues() {
		return values;
	}

	public void restrictValues(String... values) {
		this.values = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i];
		}
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public String getValidatorName() {
		if (validator == null) {
			return "Null";
		}
		return validator.getName();
	}

	public String getValidatorReason() {
		if (validator == null) {
			return "N/A";
		}
		return validator.getReason();
	}

	@Override
	public Object getElementAt(int index) {
		return data.get(index);
	}

	@Override
	public int getSize() {
		return data.size();
	}

	public String[] getSelectedItems() {
		String[] ret = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	private boolean isValid(String text) {
		if (validator == null) {
			return true;
		}
		return validator.validate(text);
	}

	public boolean addItem(String item) {
		boolean valid = isValid(item);
		if (valid) {
			if (setLike) {
				if (data.contains(item)) {
					return false;
				}
			}

			data.add(item);
			int index = data.size() - 1;
			this.fireContentsChanged(this, index, index);
		}
		return valid;
	}

	public void removeItems(int[] idx) {
		Arrays.sort(idx);
		for (int i = idx.length; i > 0; i--) {
			removeItem(idx[i - 1]);
		}
	}

	public void removeItem(int idx) {
		data.remove(idx);
		this.fireContentsChanged(this, idx, idx);
	}
}