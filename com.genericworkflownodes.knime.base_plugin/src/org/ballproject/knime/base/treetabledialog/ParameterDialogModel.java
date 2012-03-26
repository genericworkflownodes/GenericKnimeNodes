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

import java.awt.Component;
import java.io.FileNotFoundException;
import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.ListParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.base.treetabledialog.itemlist.ItemListFillerDialog;
import org.ballproject.knime.base.treetabledialog.itemlist.ListParameterModel;
import org.jdesktop.swingx.treetable.TreeTableModel;


public class ParameterDialogModel implements org.jdesktop.swingx.treetable.TreeTableModel
{
	private INodeConfiguration config;
	private ConfigWrapper     wrapper;
	private boolean           showAdvanced = true;
	private Object root;
	
	public ParameterDialogModel(INodeConfiguration config) throws FileNotFoundException, Exception
	{
		this.config = config;
		wrapper     = new ConfigWrapper(this.config);
		this.root   = wrapper.getRoot();
	}
	
	public void refresh()
	{
		wrapper     = new ConfigWrapper(this.config);
	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0)
	{		
	}
	
	public void showAdvanced(boolean flag)
	{
		showAdvanced = flag;
	}
	
	public void toggleAdvanced()
	{
		showAdvanced = !showAdvanced;
	}

	@Override
	public Object getChild(Object parent, int idx)
	{
		ParameterNode par = (ParameterNode) parent;
		return par.getChild(idx);
	}

	@Override
	public int getChildCount(Object parent)
	{
		ParameterNode par = (ParameterNode) parent;
		return par.getNumChildren();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child_)
	{
		ParameterNode par   = (ParameterNode) parent;
		ParameterNode child = (ParameterNode) child_;
		return par.getChildIndex(child);
	}

	@Override
	public Object getRoot()
	{
		return root;
	}

	@Override
	public boolean isLeaf(Object parent)
	{
		ParameterNode par = (ParameterNode) parent;
		return par.isLeaf();
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1)
	{
		
	}

	static protected Class[] cTypes = { TreeTableModel.class, String.class, String.class };
	
	@Override
	public Class<?> getColumnClass(int column)
	{
		return cTypes[column];
	}

	@Override
	public int getColumnCount()
	{
		return cNames.length;
	}

	private static String[] cNames = {"Parameter","Value","Type"};
	
	@Override
	public String getColumnName(int idx)
	{
		return cNames[idx];
	}

	@Override
	public int getHierarchicalColumn()
	{
		return 0;
	}

	@Override
	public Object getValueAt(Object node, int column)
	{
		ParameterNode n = (ParameterNode) node;

		if(column==-1)
			return n.getName();
		if(column==0)
		{
			if(n.getPayload()==null)
				return n.getName();
			else
				return n.getPayload().getKey();
		}
		if(column==1)
		{
			if(n.getPayload()==null)
				return "";
			else
				return n.getPayload();
		}
		if(column==2)
		{
			if(n.getPayload()==null)
				return "";
			else
			{
				return n.getPayload().getMnemonic();
			}
		}	
		return null;
	}

	@Override
	public boolean isCellEditable(Object value, int column)
	{
		ParameterNode n = (ParameterNode) value;
		if(column==1)
		{
			if(n.isLeaf())
				return true;
			else
				return false;
		}
			
		return false;
	}

	@Override
	public void setValueAt(Object value, Object node, int column)
	{
		ParameterNode n = (ParameterNode) node;
		n.setPayload((Parameter<?>) value);
	}
	
	public TableCellEditor getCellEditor()
	{
		return new CE();
	}

	public class CE extends AbstractCellEditor  implements TableCellEditor
	{
		private JComboBox  box;
		private JTextField field; 
		private JLabel     label = new JLabel("");
		
		@Override
		public Object getCellEditorValue()
		{	
			if(param instanceof StringParameter||param instanceof DoubleParameter||param instanceof IntegerParameter)
			{
				try
				{
					param.fillFromString(field.getText());
				}
				catch (InvalidParameterValueException e)
				{
					e.printStackTrace();
				}
			}
			if(param instanceof StringChoiceParameter)
			{
				StringChoiceParameter scp = (StringChoiceParameter) param;
				int idx = box.getSelectedIndex();
				scp.setValue(scp.getAllowedValues().get(idx));
			}
			if(param instanceof BoolParameter)
			{
				try
				{
					param.fillFromString(box.getSelectedItem().toString());
				}
				catch (InvalidParameterValueException e)
				{
					e.printStackTrace();
				}
			}
			if(param instanceof ListParameter)
			{
				ListParameter lp = (ListParameter) param;
				try
				{
					param.fillFromString(rep);
				} 
				catch (InvalidParameterValueException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return param;
		}

		private Parameter<?> param;
		private String rep;
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			param = (Parameter<?>) value;
			
			if(value instanceof StringChoiceParameter)
			{
				StringChoiceParameter scp = (StringChoiceParameter) value;
				String[] values = new String[scp.getLabels().size()];
				int i = 0;
				for(String s: scp.getLabels())
					values[i++] = s;
				box = new JComboBox(values);
				return box;
			}
			if(value instanceof StringParameter || value instanceof DoubleParameter || value instanceof IntegerParameter)
			{
				field = new JTextField(value.toString());
				return field;	
			}
			if(value instanceof BoolParameter)
			{
				String[] values = new String[]{"true","false"};
				box = new JComboBox(values);
				return box;
			}
			if(value instanceof ListParameter)
			{
				ListParameterModel   mpm = new ListParameterModel(param);
				mpm.setSetLike(true);
				ItemListFillerDialog sd  = new ItemListFillerDialog(mpm);
            	sd.setVisible(true);
            	String[] sel = mpm.getSelectedItems();
            	ListParameter lp = (ListParameter) param;
            	lp.fillFromStrings(sel);
            	rep = param.getStringRep();
            	return label;
			}
			return null;
		}
	}
}
