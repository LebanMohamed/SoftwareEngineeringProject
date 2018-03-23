/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import specialist.gui.TableModels.SPCTableModel;
import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import specialist.logic.SPC;

public class ChooseSPCPrompt extends GenericPrompt {

    private SPCTableModel model = new SPCTableModel();
    private int selectedRowIdx;

    public void show() {
        // Prepare table model
        model.loadSPCs();
      

        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        TableHelper.leftAlignColumnValues(table, 0);
        TableHelper.setColumnPrefWidth(table, 0, 4);
        TableHelper.setColumnPrefWidth(table, 2, 50);
        TableHelper.setColumnPrefWidth(table, 3, 90);
        TableHelper.setColumnPrefWidth(table, 4, 20);
        TableHelper.setColumnPrefWidth(table, 5, 100);
        TableHelper.setColumnPrefWidth(table, 6, 30);
        

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS SPC booking / Choose SPC",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                        "GMSIS / Error Choosing SPC", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public SPC getSelectedSPC() {
        if (!successful || selectedRowIdx == -1) {
            throw new IllegalStateException();
        }
        return model.rowToSPC(selectedRowIdx);
    }

  

}
