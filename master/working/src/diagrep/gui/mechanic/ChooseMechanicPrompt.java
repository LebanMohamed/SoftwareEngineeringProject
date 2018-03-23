package diagrep.gui.mechanic;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import diagrep.logic.Mechanic;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link javax.swing.JOptionPane} for creating a {@link Mechanic} instance from the selected row if successful.
 */
public final class ChooseMechanicPrompt extends GenericPrompt {

    private final ManageMechanicsTableModel model = new ManageMechanicsTableModel();
    private Mechanic selectedMechanic = null;

    public void show() {
        // Prepare table model
        model.loadMechanics();
        model.setEditable(false); // disable editing of all columns

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        TableHelper.leftAlignColumnValues(table, 0);
        TableHelper.leftAlignColumnValues(table, 3);
        TableHelper.setColumnPrefWidth(table, 0, 8);
        TableHelper.setColumnPrefWidth(table, 3, 8);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "Choose Mechanic",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) { // Invalid row selected
                JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                        "Choose Mechanic - Error", JOptionPane.ERROR_MESSAGE);
            } else { // Valid row selected
                selectedMechanic = model.rowToMechanic(selectedRowIdx);
                successful = true;
            }
        }
    }

    public Mechanic getSelectedMechanic() {
        if (!successful)
            throw new IllegalStateException();
        return selectedMechanic;
    }
}
