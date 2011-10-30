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

package org.ballproject.knime.base.treetabledialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
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
	//private JButton     toggle;
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
				boolean advanced = false;
				TreePath path = table.getPathForRow(adapter.row);

				if(path!=null&&path.getLastPathComponent()!=null)
				{
					//@SuppressWarnings("unchecked")
					//Node<Parameter<?>> node = (Node<Parameter<?>>) path.getLastPathComponent();
					ParameterNode node = (ParameterNode) path.getLastPathComponent();
					if(node.getPayload()!=null)
					{
						optional = node.getPayload().getIsOptional();
						advanced = node.getPayload().isAdvanced();
					}
				}
				if(!optional)
				{
					comp.setForeground(Color.blue);
					return comp;
				}
				else
				{
					if(advanced)
					{
						comp.setForeground(Color.GRAY);
					}
				}
				return comp;
			}

			@Override
			public void removeChangeListener(ChangeListener arg0)
			{	
			}
			
		});
		
		// expand full tree by default
		treeTable.expandAll();
		
		UIHelper.addComponent(this, new JScrollPane(treeTable), 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,2.0f);
		help = new JTextPane();
		UIHelper.addComponent(this, new JScrollPane(help), 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,1.0f);
		/*
		toggle = new JButton("Toggle adv. Parameters");
		toggle.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				model.toggleAdvanced();
			}}
		);
		UIHelper.addComponent(this, toggle, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,1.0f);
		*/
	}
	
	public ParameterDialogModel getModel()
	{
		return model;
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
