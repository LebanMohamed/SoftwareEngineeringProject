package diagrep.gui.choose;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import vehicles.logic.Vehicle;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link javax.swing.JOptionPane} for creating a {@link Vehicle} instance from the selected row if successful.
 */
public final class ChooseVehiclePrompt extends GenericPrompt {

    private final int customerId;
    private final VehicleDetailsTableModel tableModel = new VehicleDetailsTableModel();
    private int selectedRowIdx;

    public ChooseVehiclePrompt(int customerId) {
        this.customerId = customerId;
    }

    public void show() {
        // Prepare table model
        tableModel.loadVehicles(customerId);

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        TableHelper.leftAlignColumnValues(table, 4);
        TableHelper.leftAlignColumnValues(table, 7);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "Choose Vehicle",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a vehicle.", "Choose Vehicle - Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public Vehicle getSelectedVehicle() {
        if (!successful || selectedRowIdx == -1)
            throw new IllegalStateException();
        return tableModel.rowToVehicle(selectedRowIdx);
    }
}
