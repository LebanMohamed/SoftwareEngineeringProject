/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import diagrep.gui.choose.VehicleDetailsTableModel;
import vehicles.logic.Vehicle;

import javax.swing.*;
import java.awt.*;


public final class ChooseVehiclePrompt extends GenericPrompt {

    private final VehicleDetailsTableModel tableModel = new VehicleDetailsTableModel();
    private int selectedRowIdx;

    public void show() {
        // Prepare table model
        tableModel.loadAllVehicles();

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        TableHelper.leftAlignColumnValues(table, 4);
        TableHelper.leftAlignColumnValues(table, 7);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS SPC booking / Choose Vehicle",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a vehicle.", "Error Choosing Vehicle",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    Vehicle getSelectedVehicle() {
        if (!successful || selectedRowIdx == -1) {
            throw new IllegalStateException();
        }
        return tableModel.rowToVehicle(selectedRowIdx);
    }
}
