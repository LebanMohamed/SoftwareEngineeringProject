/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Lists;
 
import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 *
 * @author cawaala
 */
public class ListOfFutureBookingsPrompt {
    private final ListOfBookingsTableModel tableModel = new ListOfBookingsTableModel();

    public void show() throws ParseException, SQLException {
        tableModel.loadFutureBookings();
        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
 
        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS / Future Booking Dates",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
