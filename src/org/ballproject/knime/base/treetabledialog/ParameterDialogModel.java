package org.ballproject.knime.base.treetabledialog;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.jdesktop.swingx.treetable.TreeTableModel;


public class ParameterDialogModel implements org.jdesktop.swingx.treetable.TreeTableModel
{
	private NodeConfiguration config;
	private ConfigWrapper     wrapper;
	
	private Object root;
	
	public ParameterDialogModel(NodeConfiguration config) throws FileNotFoundException, Exception
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

	@Override
	public Object getChild(Object parent, int idx)
	{
		Node<Parameter<?>> par = (Node<Parameter<?>>) parent;
		return par.getChild(idx);
	}

	@Override
	public int getChildCount(Object parent)
	{
		Node<Parameter<?>> par = (Node<Parameter<?>>) parent;
		return par.getNumChildren();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child_)
	{
		Node<Parameter<?>> par   = (Node<Parameter<?>>) parent;
		Node<Parameter<?>> child = (Node<Parameter<?>>) child_;
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
		Node<Parameter<?>> par   = (Node<Parameter<?>>) parent;
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
		Node<Parameter<?>> n = (Node<Parameter<?>>) node;

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
		Node<Parameter<?>> n = (Node<Parameter<?>>) value;
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
		Node<Parameter<?>> n = (Node<Parameter<?>>) node;
		String val = value.toString();
		Parameter<?> p = n.getPayload();
		try
		{
			p.fillFromString(val);
		}
		catch (InvalidParameterValueException e)
		{
			e.printStackTrace();
		}
	}
	
	public TableCellEditor getCellEditor()
	{
		return new CE();
	}

	public class CE extends AbstractCellEditor  implements TableCellEditor
	{
		private JComboBox  box;
		private JTextField field; 
			
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(param instanceof StringChoiceParameter||param instanceof BoolParameter)
			{
				try
				{
					param.fillFromString(box.getSelectedItem().toString());
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
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			param = (Parameter<?>) value;
			if(value instanceof StringChoiceParameter)
			{
				StringChoiceParameter scp = (StringChoiceParameter) value;
				String[] values = new String[scp.getAllowedValues().size()];
				int i = 0;
				for(String s: scp.getAllowedValues())
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
			return null;
		}
	}
}
