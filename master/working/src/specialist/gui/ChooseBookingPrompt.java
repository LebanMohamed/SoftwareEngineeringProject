/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import common.gui.util.GenericPrompt;
import common.gui.util.TableHelper;
import diagrep.gui.BookingsTableModel;
import diagrep.logic.Booking;
import diagrep.logic.BookingFilter;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;


public class ChooseBookingPrompt extends GenericPrompt {

    private final BookingFilter filter = new BookingFilter(BookingFilter.Type.WITH_NO_SPC, null, false);
    private final BookingsTableModel model = new BookingsTableModel();
    private int selectedRowIdx;

    @Override
    public void show() {
        model.loadBookings(filter);

        JScrollPane scrollPane = new JScrollPane();
        JTable bookingsTable = new JTable(model);
        
        TableHelper.leftAlignColumnValues(bookingsTable, 0);
        TableHelper.setColumnPrefWidth(bookingsTable, 1, 100);
        TableHelper.setColumnPrefWidth(bookingsTable, 2, 100);
        
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(bookingsTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS SPC booking / Choose Diag&Rep booking",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            selectedRowIdx = bookingsTable.getSelectedRow();
            if (selectedRowIdx == -1) {
                if (model.getRowCount() == 0) {
                    return;
                }
                JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                        "GMSIS / Error Choosing booking", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }

    }

    public Booking getSelectedBooking() {
        if (!successful || selectedRowIdx == -1) {
            throw new IllegalStateException();
        }
        return model.rowToBooking(selectedRowIdx);
    }

}
