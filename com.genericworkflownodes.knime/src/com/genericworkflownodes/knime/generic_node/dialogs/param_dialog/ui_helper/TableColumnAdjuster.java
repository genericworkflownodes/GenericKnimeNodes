package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ui_helper;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Class to manage the widths of colunmns in a table.
 * 
 * Various properties control how the width of the column is calculated. Another
 * property controls whether column width calculation should be dynamic.
 * Finally, various Actions will be added to the table to allow the user to
 * customize the functionality.
 * 
 * This class was designed to be used with tables that use an auto resize mode
 * of AUTO_RESIZE_OFF. With all other modes you are constrained as the width of
 * the columns must fit inside the table. So if you increase one column, one or
 * more of the other columns must decrease. Because of this the resize mode of
 * RESIZE_ALL_COLUMNS will work the best.
 * 
 * see: http://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
 */
public class TableColumnAdjuster implements PropertyChangeListener,
		TableModelListener, TreeModelListener {
	/**
	 * The table
	 */
	private JTable m_table;

	/**
	 * The spacing.
	 */
	private int m_spacing;

	/**
	 * Indicator if column header is included.
	 */
	private boolean m_isColumnHeaderIncluded;

	/**
	 * Indicator if column data is included.
	 */
	private boolean m_isColumnDataIncluded;

	/**
	 * Indicator if isOnlyAdjustLarger.
	 */
	private boolean m_isOnlyAdjustLarger;

	/**
	 * Indicator if is dynamic adjustment.
	 */
	private boolean m_isDynamicAdjustment;

	/**
	 * Mapping of column sizes.
	 */
	private Map<TableColumn, Integer> m_columnSizes;

	/**
	 * Specify the table and use default m_spacing
	 */
	public TableColumnAdjuster(JTable table) {
		this(table, 6);
	}

	/**
	 * Specify the table and spacing
	 */
	public TableColumnAdjuster(final JTable table, final int spacing) {
		m_table = table;
		m_spacing = spacing;
		m_columnSizes = new HashMap<TableColumn, Integer>();
		setColumnHeaderIncluded(true);
		setColumnDataIncluded(true);
		setOnlyAdjustLarger(true);
		setDynamicAdjustment(false);
		installActions();
	}

	/**
	 * Adjust the widths of all the columns in the table
	 */
	public void adjustColumns() {
		TableColumnModel tcm = m_table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			adjustColumn(i);
		}
	}

	/**
	 * Adjust the width of the specified column in the table
	 */
	public void adjustColumn(final int column) {
		TableColumn tableColumn = m_table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
			return;

		int columnHeaderWidth = getColumnHeaderWidth(column);
		int columnDataWidth = getColumnDataWidth(column);
		int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

		updateTableColumn(column, preferredWidth);
	}

	/**
	 * Calculated the width based on the column name
	 */
	private int getColumnHeaderWidth(int column) {
		if (!m_isColumnHeaderIncluded)
			return 0;

		TableColumn tableColumn = m_table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			renderer = m_table.getTableHeader().getDefaultRenderer();
		}

		Component c = renderer.getTableCellRendererComponent(m_table, value,
				false, false, -1, column);
		return c.getPreferredSize().width;
	}

	/**
	 * Calculate the width based on the widest cell renderer for the given
	 * column.
	 */
	private int getColumnDataWidth(final int column) {
		if (!m_isColumnDataIncluded)
			return 0;

		int preferredWidth = 0;
		int maxWidth = m_table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < m_table.getRowCount(); row++) {
			preferredWidth = Math.max(preferredWidth,
					getCellDataWidth(row, column));

			// We've exceeded the maximum width, no need to check other rows

			if (preferredWidth >= maxWidth)
				break;
		}

		return preferredWidth;
	}

	/**
	 * Get the preferred width for the specified cell
	 */
	private int getCellDataWidth(final int row, final int column) {
		// Inovke the renderer for the cell to calculate the preferred width

		TableCellRenderer cellRenderer = m_table.getCellRenderer(row, column);
		Component c = m_table.prepareRenderer(cellRenderer, row, column);
		int width = c.getPreferredSize().width
				+ m_table.getIntercellSpacing().width;

		return width;
	}

	/**
	 * Update the TableColumn with the newly calculated width
	 */
	private void updateTableColumn(final int column, int width) {
		final TableColumn tableColumn = m_table.getColumnModel().getColumn(
				column);

		if (!tableColumn.getResizable())
			return;

		width += m_spacing;

		// Don't shrink the column width

		if (m_isOnlyAdjustLarger) {
			width = Math.max(width, tableColumn.getPreferredWidth());
		}

		m_columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));
		m_table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}

	/**
	 * Restore the widths of the columns in the table to its previous width
	 */
	public void restoreColumns() {
		TableColumnModel tcm = m_table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			restoreColumn(i);
		}
	}

	/**
	 * Restore the width of the specified column to its previous width
	 */
	private void restoreColumn(int column) {
		TableColumn tableColumn = m_table.getColumnModel().getColumn(column);
		Integer width = m_columnSizes.get(tableColumn);

		if (width != null) {
			m_table.getTableHeader().setResizingColumn(tableColumn);
			tableColumn.setWidth(width.intValue());
		}
	}

	/**
	 * Indicates whether to include the header in the width calculation
	 */
	public void setColumnHeaderIncluded(boolean isColumnHeaderIncluded) {
		m_isColumnHeaderIncluded = isColumnHeaderIncluded;
	}

	/**
	 * Indicates whether to include the model data in the width calculation
	 */
	public void setColumnDataIncluded(boolean isColumnDataIncluded) {
		m_isColumnDataIncluded = isColumnDataIncluded;
	}

	/**
	 * Indicates whether columns can only be increased in size
	 */
	public void setOnlyAdjustLarger(boolean isOnlyAdjustLarger) {
		m_isOnlyAdjustLarger = isOnlyAdjustLarger;
	}

	/**
	 * Indicate whether changes to the model should cause the width to be
	 * dynamically recalculated.
	 */
	public void setDynamicAdjustment(boolean isDynamicAdjustment) {
		// May need to add or remove the TableModelListener when changed

		if (m_isDynamicAdjustment != isDynamicAdjustment) {
			if (isDynamicAdjustment) {
				m_table.addPropertyChangeListener(this);
				m_table.getModel().addTableModelListener(this);
			} else {
				m_table.removePropertyChangeListener(this);
				m_table.getModel().removeTableModelListener(this);
			}
		}

		m_isDynamicAdjustment = isDynamicAdjustment;
	}

	//
	// Implement the PropertyChangeListener
	//
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// When the TableModel changes we need to update the listeners
		// and column widths

		if ("model".equals(e.getPropertyName())) {
			TableModel model = (TableModel) e.getOldValue();
			model.removeTableModelListener(this);

			model = (TableModel) e.getNewValue();
			model.addTableModelListener(this);
			adjustColumns();
		}
	}

	//
	// Implement the TableModelListener
	//
	@Override
	public void tableChanged(TableModelEvent e) {
		if (!m_isColumnDataIncluded)
			return;

		// A cell has been updated

		if (e.getType() == TableModelEvent.UPDATE) {
			int column = m_table.convertColumnIndexToView(e.getColumn());

			// Only need to worry about an increase in width for this cell

			if (m_isOnlyAdjustLarger) {
				int row = e.getFirstRow();
				TableColumn tableColumn = m_table.getColumnModel().getColumn(
						column);

				if (tableColumn.getResizable()) {
					int width = getCellDataWidth(row, column);
					updateTableColumn(column, width);
				}
			}

			// Could be an increase of decrease so check all rows

			else {
				adjustColumn(column);
			}
		}

		// The update affected more than one column so adjust all columns

		else {
			adjustColumns();
		}
	}

	/**
	 * Install Actions to give user control of certain functionality.
	 */
	private void installActions() {
		installColumnAction(true, true, "adjustColumn", "control ADD");
		installColumnAction(false, true, "adjustColumns", "control shift ADD");
		installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
		installColumnAction(false, false, "restoreColumns",
				"control shift SUBTRACT");

		installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
		installToggleAction(false, true, "toggleLarger", "control DIVIDE");
	}

	/**
	 * Update the input and action maps with a new ColumnAction
	 */
	private void installColumnAction(boolean isSelectedColumn,
			boolean isAdjust, String key, String keyStroke) {
		Action action = new ColumnAction(isSelectedColumn, isAdjust);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		m_table.getInputMap().put(ks, key);
		m_table.getActionMap().put(key, action);
	}

	/**
	 * Update the input and action maps with new ToggleAction
	 */
	private void installToggleAction(boolean isToggleDynamic,
			boolean isToggleLarger, String key, String keyStroke) {
		Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		m_table.getInputMap().put(ks, key);
		m_table.getActionMap().put(key, action);
	}

	/**
	 * Action to adjust or restore the width of a single column or all columns
	 */
	class ColumnAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7137066217479472611L;
		private boolean isSelectedColumn;
		private boolean isAdjust;

		public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
			this.isSelectedColumn = isSelectedColumn;
			this.isAdjust = isAdjust;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Handle selected column(s) width change actions

			if (isSelectedColumn) {
				int[] columns = m_table.getSelectedColumns();

				for (int i = 0; i < columns.length; i++) {
					if (isAdjust)
						adjustColumn(columns[i]);
					else
						restoreColumn(columns[i]);
				}
			} else {
				if (isAdjust)
					adjustColumns();
				else
					restoreColumns();
			}
		}
	}

	/**
	 * Toggle properties of the TableColumnAdjuster so the user can customize
	 * the functionality to their preferences
	 */
	class ToggleAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1333283198492490659L;
		private boolean isToggleDynamic;
		private boolean isToggleLarger;

		public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
			this.isToggleDynamic = isToggleDynamic;
			this.isToggleLarger = isToggleLarger;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isToggleDynamic) {
				setDynamicAdjustment(!m_isDynamicAdjustment);
				return;
			}

			if (isToggleLarger) {
				setOnlyAdjustLarger(!m_isOnlyAdjustLarger);
				return;
			}
		}
	}

	// TreeModelListener

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		adjustColumns();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		adjustColumns();
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		adjustColumns();
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		adjustColumns();
	}
}