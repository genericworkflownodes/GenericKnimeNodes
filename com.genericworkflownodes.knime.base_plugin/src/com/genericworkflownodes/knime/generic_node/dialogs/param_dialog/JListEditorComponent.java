package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import javax.swing.JLabel;

import com.genericworkflownodes.knime.generic_node.dialogs.UIHelper;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist.ItemListFillerDialog;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist.ListParameterModel;

/**
 * This component allows to edit lists of values in a separate window.
 * 
 * @author Björn Kahlert
 * 
 */
public class JListEditorComponent extends JLabel {

	private static final long serialVersionUID = 1L;
	private final ListParameterModel listParameterModel;

	public JListEditorComponent(ListParameterModel listParameterModel) {
		super("Editing...");

		this.listParameterModel = listParameterModel;

		ItemListFillerDialog sd = new ItemListFillerDialog(
				this.listParameterModel);
		sd.setVisible(true);

		UIHelper.simulateEnterKeyPressed(this, 50);
	}

	public ListParameterModel getModel() {
		return this.listParameterModel;
	}

}
