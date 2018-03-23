/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import java.awt.Dimension;
import java.text.ParseException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import parts.logic.Parts;
import specialist.gui.TableModels.ChoosePartTableModel;

public class ChoosePartPrompt extends GenericPrompt {

    private ChoosePartTableModel model = new ChoosePartTableModel();
    private int selectedRowIdx;

    public void show() {
        model.loadParts();
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(model);
        
        TableHelper.leftAlignColumnValues(table, 0);
        TableHelper.setColumnPrefWidth(table, 0, 6);
        TableHelper.setColumnPrefWidth(table, 1, 40);
        TableHelper.setColumnPrefWidth(table, 2, 40);
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        TableHelper.leftAlignColumnValues(table, 0);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS SPC booking / Choose part",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedRowIdx = table.getSelectedRow();

            if (selectedRowIdx == -1) {
                JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                        "GMSIS / Error Choosing part", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public Parts getSelectedPart() throws ParseException {
        if (!successful || selectedRowIdx == -1) {
            throw new IllegalStateException();
        }
        return model.rowToPart(selectedRowIdx);
    }

}
