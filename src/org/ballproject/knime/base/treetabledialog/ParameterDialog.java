package org.ballproject.knime.base.treetabledialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;


import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.Parameter;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;


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
		
		
		treeTable.setHighlighters(new Highlighter(){

			@Override
			public void addChangeListener(ChangeListener arg0)
			{				
			}

			@Override
			public ChangeListener[] getChangeListeners()
			{
				return null;
			}

			@Override
			public Component highlight(Component comp, ComponentAdapter adapter)
			{
				boolean optional = true;
				
				TreePath path = table.getPathForRow(adapter.row);

				if(path!=null&&path.getLastPathComponent()!=null)
				{
					Node<Parameter<?>> node = (Node<Parameter<?>>) path.getLastPathComponent();
					if(node.getPayload()!=null)
						optional = node.getPayload().getIsOptional();
				}
				if(!optional)
				{
					comp.setForeground(Color.red);
					return comp;
				}
				else
					return comp;
			}

			@Override
			public void removeChangeListener(ChangeListener arg0)
			{	
			}
			
		});
		
		// expand full tree by default
		treeTable.expandAll();
		
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
