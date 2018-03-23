package common.gui.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

/**
 * A feature-rich table model extending {@link DefaultTableModel}, loosely based on the auto-generated one by the
 * Netbeans GUI designer.
 */
public class DefaultTableModelEx extends DefaultTableModel {

    private final Map<Integer, Boolean> columnEditableMap = new HashMap<>();
    private final Class[] types;
    private boolean editable = true;

    public DefaultTableModelEx(String[] columnNames, Class[] types) {
        super(columnNames, 0);
        this.types = types;
    }

    public Class getColumnClass(int colIdx) {
        return types[colIdx];
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isCellEditable(int rowIdx, int colIdx) {
        return super.isCellEditable(rowIdx, colIdx) && editable
                && (columnEditableMap.containsKey(colIdx) ? columnEditableMap.get(colIdx) : true);
    }

    /**
     * Sets a column as editable or not.
     */
    public void setColumnEditable(int colIdx, boolean editable) {
        columnEditableMap.put(colIdx, editable);
    }

    /**
     * Clears all rows.
     */
    public void clearRows() {
        setRowCount(0);
    }

    /**
     * Removes the first row with the given value in the given column.
     * @return If the row was found and removed.
     */
    public boolean removeRowByColumnValue(int colIdx, Object val) {
        // Find row number
        for (int rowNo = 0; rowNo < getRowCount(); rowNo++) {
            Object colValue = getValueAt(rowNo, colIdx);

            if (colValue.equals(val)) {
                removeRow(rowNo);
                return true;
            }
        }
        return false;
    }
}
