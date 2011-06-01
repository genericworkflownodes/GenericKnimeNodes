package org.ballproject.knime.base.treetabledialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.Parameter;
import org.jdesktop.swingx.JXTreeTable;


public class ParameterDialog extends JPanel implements ListSelectionListener
{
	private JXTreeTable table;
	private JTextPane   help;
	private ParameterDialogModel model;
	
	public ParameterDialog(NodeConfiguration config) throws FileNotFoundException, Exception
	{
		setLayout(new GridBagLayout());
		model = new ParameterDialogModel(config);
		
		JXTreeTable treeTable      = new JXTreeTable(model);
		table = treeTable;
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		treeTable.getColumn(1).setCellEditor(model.getCellEditor());
		JScrollPane     scrollpane     = new JScrollPane(treeTable);
		treeTable.getSelectionModel().addListSelectionListener(this);
		
		addComponent(this, new JScrollPane(treeTable), 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,2.0f);
		help = new JTextPane();
		addComponent(this, new JScrollPane(help), 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,1.0f);

	}
	
	public ParameterDialogModel getModel()
	{
		return model;
	}
	
	private static final Insets insets = new Insets(2,2,2,2);

	private static void addComponent(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, float weighty) 
	{
		GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
		gridwidth, gridheight, 1.0, weighty, anchor, fill, insets, 0, 0);
		container.add(component, gbc);
	}

	@Override
	public void valueChanged(ListSelectionEvent evt)
	{
		if(evt.getValueIsAdjusting())
			return;
		if(evt.getSource() == table.getSelectionModel())
		{
			int row = table.getSelectedRow();
			Object val = table.getModel().getValueAt(row, 1);
			if(val instanceof Parameter<?>)
			{
				Parameter<?>  param = (Parameter<?>) val;

				StyledDocument doc = (StyledDocument) help.getDocument();
				Style style = doc.addStyle("StyleName", null);
				StyleConstants.setFontFamily(style, "SansSerif");

				try
				{
					doc.remove(0, doc.getLength());
					doc.insertString(0, param.getDescription(), style);
				}
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		}
				
	}
}
