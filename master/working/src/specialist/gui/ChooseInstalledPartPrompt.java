package specialist.gui;

/**
 * @author ec15143 Nikita Mirolyubov
 */
import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import java.awt.Dimension;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import parts.logic.InstalledParts;
import specialist.gui.TableModels.PartsInstalledTableModel;
import specialist.logic.SPCBooking;

public class ChooseInstalledPartPrompt extends GenericPrompt {

    private SPCBooking booking;

    public ChooseInstalledPartPrompt(SPCBooking booking) {
        this.booking = booking;
    }
    private PartsInstalledTableModel model = new PartsInstalledTableModel();
    private int selectedRowIdx;

    public void show() {
        try {

            model.loadInstalledForVehicle(booking.getBooking().getVehicle().getRegistrationNo());
            JScrollPane scrollPane = new JScrollPane();
            JTable partsTable = new JTable(model);
            
            TableHelper.leftAlignColumnValues(partsTable, 0);
            TableHelper.setColumnPrefWidth(partsTable, 0, 6);
            TableHelper.setColumnPrefWidth(partsTable, 1, 40);
            TableHelper.setColumnPrefWidth(partsTable, 2, 40);
            
            partsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane.setViewportView(partsTable);
            scrollPane.setPreferredSize(new Dimension(600, 300));
            TableHelper.leftAlignColumnValues(partsTable, 0);

            // Show prompt
            int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS SPC booking // Choose part",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                selectedRowIdx = partsTable.getSelectedRow();

                if (selectedRowIdx == -1) {
                    if (model.getRowCount() == 0) {
                        return;
                    }
                    JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                            "GMSIS / Error Choosing part", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fall-back
                successful = true;
            }
        } catch (ParseException ex) {
            Logger.getLogger(ChooseInstalledPartPrompt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public InstalledParts getSelectedPart() throws ParseException {
        if (!successful || selectedRowIdx == -1) {
            throw new IllegalStateException();
        }
        return model.rowToPart(selectedRowIdx);
    }

}
