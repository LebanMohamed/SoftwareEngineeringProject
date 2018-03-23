/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Search;

import common.gui.util.GenericPrompt;
import diagrep.gui.ManageBookingsDialog;
import diagrep.logic.BookingFilter;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author cawaala
 */
public final class SearchBookingByName extends GenericPrompt {

    private final JTextField CustomerName = new JTextField(20);

    public void show() {
        GridLayout layout = new GridLayout(1, 1);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("Enter Customers Full Name: "));
        panel.add(CustomerName);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Search Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String getListOfCustomers = CustomerName.getText();

            if (getListOfCustomers.isEmpty()) {
                showErrorDialog("No name was entered.");
                return;
            } else {
                ManageBookingsDialog f = new ManageBookingsDialog();
                f.setDefaultFilter(new BookingFilter(BookingFilter.Type.CUSTOMER_FULL_NAME, getListOfCustomers)); // exact match on name
                f.setVisible(true);
            }

        }
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Searching Customers", JOptionPane.ERROR_MESSAGE);
    }
}
