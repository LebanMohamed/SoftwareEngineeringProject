package diagrep.gui.choose;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import customers.gui.ManageCustomerFrame;
import customers.gui.ManageCustomerTableModel;
import customers.logic.Customer;

import javax.swing.*;
import java.awt.*;

/**
 * A specialized variant of {@link ManageCustomerFrame} for creating a {@link Customer} instance from the selected row
 * if successful.
 */
public final class ChooseCustomerPrompt extends GenericPrompt {

    private final ManageCustomerTableModel tableModel = new ManageCustomerTableModel();
    private int selectedRowIdx;

    public void show() {
        // Prepare table model
        tableModel.setEditable(false); // disable editing
        tableModel.loadInitialCustomerList();

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        TableHelper.leftAlignColumnValues(table, 0);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "Choose Customer", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a customer or cancel this interaction.",
                        "Choose Customer - Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public Customer getSelectedCustomer() {
        if (!successful || selectedRowIdx == -1)
            throw new IllegalStateException();
        return tableModel.rowToCustomer(selectedRowIdx);
    }
}
