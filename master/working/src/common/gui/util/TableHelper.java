package common.gui.util;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * A set of helper methods for {@link javax.swing.JTable}.
 */
public final class TableHelper {

    /**
     * If a column's value type is not a String (e.g. integer), it is right-aligned by default.
     * This sets it to left alignment.
     * Source: https://stackoverflow.com/questions/2408541/align-the-values-of-the-cells-in-jtable
     */
    public static void leftAlignColumnValues(JTable table, int columnIdx) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(columnIdx).setCellRenderer(renderer);
    }

    /**
     * If a column's value type is not a String (e.g. integer), it is right-aligned by default when being editted.
     * This sets it to left alignment.
     * Source: https://stackoverflow.com/questions/33506312/align-values-in-jtable-cells-to-right-to-left-while-editing
     */
    public static void leftAlignEditingColumnValues(JTable table, int columnIdx) {
        JTextField editorTextField = new JTextField();
        editorTextField.setBorder(new LineBorder(Color.BLACK));
        editorTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        DefaultCellEditor cellEditor = new DefaultCellEditor(editorTextField);
        table.getColumnModel().getColumn(columnIdx).setCellEditor(cellEditor);
    }

    /**
     * Sets the width for a column in a table.
     * Source: https://stackoverflow.com/questions/953972/java-jtable-setting-column-width
     */
    public static void setColumnPrefWidth(JTable table, int colIdx, int width) {
        table.getColumnModel().getColumn(colIdx).setPreferredWidth(width);
    }
}
