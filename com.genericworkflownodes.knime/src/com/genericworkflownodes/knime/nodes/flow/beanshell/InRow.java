package com.genericworkflownodes.knime.nodes.flow.beanshell;

import org.knime.core.data.DataCell;

public class InRow {
    private DataCell[] row;

    public InRow(DataCell[] row) {
        this.row = new DataCell[row.length];
        System.arraycopy(row, 0, this.row, 0, row.length);
    }

    public int getNumCols() {
        if (row != null) {
            return row.length;
        }
        return 0;
    }

    public Class<?> getColumnClass(int idx) {
        if (row != null && idx >= 0 && idx < row.length) {
            return row[idx].getClass();
        }
        return null;
    }

    public DataCell getCell(int idx) {
        if (row != null && idx >= 0 && idx < row.length) {
            return row[idx];
        }
        return null;
    }
}