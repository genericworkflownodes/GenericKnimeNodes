package com.genericworkflownodes.knime.nodes.flow.beanshell;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.genericworkflownodes.knime.generic_node.dialogs.UIHelper;

public class EditorPanel extends JPanel {
	private static final long serialVersionUID = -366002958038558740L;
	private final JEditorPane edit;
	private final JEditorPane edit2;
	private final JEditorPane edit3;

	public EditorPanel() {

		setLayout(new GridBagLayout());

		edit = new JEditorPane();
		edit2 = new JEditorPane();
		edit3 = new JEditorPane();

		Font font = edit.getFont();
		Font newFont = new Font(Font.MONOSPACED, Font.PLAIN, (font == null ? 12
				: font.getSize()));

		UIHelper.addComponent(this, new JLabel("Init:"), 0, 0, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0.0f);
		UIHelper.addComponent(this, new JScrollPane(edit), 0, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2.0f);
		UIHelper.addComponent(this, new JLabel("FirstPass:"), 0, 2, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0.0f);
		UIHelper.addComponent(this, new JScrollPane(edit2), 0, 3, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2.0f);
		UIHelper.addComponent(this, new JLabel("SecondPass:"), 0, 4, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0.0f);
		UIHelper.addComponent(this, new JScrollPane(edit3), 0, 5, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2.0f);

		edit.setFont(newFont);
		edit2.setFont(newFont);
		edit3.setFont(newFont);
	}

	public String getInitScript() {
		return edit.getText();
	}

	public void setInitScript(String txt) {
		edit.setText(txt);
	}

	public String getFirstPassScript() {
		return edit2.getText();
	}

	public void setFirstPassScript(String txt) {
		edit2.setText(txt);
	}

	public String getSecondPassScript() {
		return edit3.getText();
	}

	public void setSecondPassScript(String txt) {
		edit3.setText(txt);
	}
}
