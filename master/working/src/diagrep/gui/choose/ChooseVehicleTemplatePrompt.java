package diagrep.gui.choose;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import vehicles.logic.VehicleTemplate;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public final class ChooseVehicleTemplatePrompt extends GenericPrompt {

    private final ViewVehicleTemplateTableModel tableModel = new ViewVehicleTemplateTableModel();
    private int selectedRowIdx;

    public void show() {
        // Prepare table model
        tableModel.setEditable(false); // disable editing
        tableModel.loadTemplates();

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        TableHelper.leftAlignColumnValues(table, 0);
        TableHelper.leftAlignColumnValues(table, 3);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "Choose Vehicle Template", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a vehicle template or cancel this interaction.",
                        "Choose Vehicle Template - Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public VehicleTemplate parseSelectedVehicleTemplate() {
        if (!successful || selectedRowIdx == -1)
            throw new IllegalStateException();
        return tableModel.rowToVehicleTemplate(selectedRowIdx);
    }

    class ViewVehicleTemplateTableModel extends DefaultTableModelEx {

        private ArrayList<VehicleTemplate> templates;

        ViewVehicleTemplateTableModel() {
            super(new String[]{"id", "Make", "Model", "Engine Size", "Fuel Type"},
                    new Class[]{Integer.class, String.class, String.class, Double.class, String.class});
            setEditable(false);
        }

        public void addRow(VehicleTemplate template) {
            addRow(new Object[]{
                    template.getId(), template.getMake(), template.getModel(), template.getEngineSize(),
                    template.getFuelType()
            });
        }

        VehicleTemplate rowToVehicleTemplate(int rowIdx) {
            if (rowIdx < 0 || rowIdx >= getRowCount()) {
                throw new IllegalArgumentException();
            }
            return templates.get(rowIdx);
        }

        void loadTemplates() {
            try {
                // Load all vehicle templates
                this.templates = Database.getInstance().getAllTemplate();

                // Iterate over the vehicles and add them to the table row by row
                for (VehicleTemplate vt : templates) {
                    addRow(vt);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
