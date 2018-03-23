package vehicles.gui;

import common.Database;
import customers.logic.Customer;
import diagrep.logic.Booking;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import vehicles.logic.Vehicle;

/**
 * @author Akhil
 */
public class ViewCustomerDetails {

    private final JTextField customerNameInput = new javax.swing.JTextField();
    private final JTextField AddressLine1Input = new javax.swing.JTextField();
    private final JTextField CityInput = new javax.swing.JTextField();
    private final JTextField PostcodeInput = new javax.swing.JTextField();
    private final JTextField emailInput = new javax.swing.JTextField();
    private final JTextField phoneNumberInput = new javax.swing.JTextField();
    private final JTextField nextBookingInput = new javax.swing.JTextField();

    private final int CustomerID;
    private final String RegistrationNo;
    private boolean successful = false;

    public ViewCustomerDetails(int customerid, String Regno) {
        CustomerID = customerid;
        RegistrationNo = Regno;
    }

    public void show() throws SQLException, ParseException {
        Customer c;
        try {
            c = Database.getInstance().getCustomer(CustomerID);
            customerNameInput.setText(c.getFullName());
            customerNameInput.setEditable(false);
            AddressLine1Input.setText(c.getStreetAddress());
            AddressLine1Input.setEditable(false);
            CityInput.setText(c.getCity());
            CityInput.setEditable(false);
            PostcodeInput.setText(c.getPostcode());
            PostcodeInput.setEditable(false);
            emailInput.setText(c.getEmail());
            emailInput.setEditable(false);
            phoneNumberInput.setText(c.getPhone());
            phoneNumberInput.setEditable(false);
            nextBookingInput.setEditable(false);

            List<Booking> SoonestBookings;
            SoonestBookings = Database.getInstance().getAllNextBookingsPerVehicle();

            for (Booking b : SoonestBookings) {
                Vehicle v = b.getVehicle();
                if (v.getRegistrationNo().equalsIgnoreCase(RegistrationNo)) {
                    Calendar startDate = b.getBookingStartDate();
                    nextBookingInput.setText(toString(startDate));
                    break;
                }
            }
        } catch (SQLException | ParseException ex) {
        }
        GridLayout layout = new GridLayout(10, 5);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("CustomerID: "));
        panel.add(new JLabel("" + CustomerID));
        panel.add(new JLabel("Name:"));
        panel.add(customerNameInput);
        panel.add(new JLabel("Address Line 1: "));
        panel.add(AddressLine1Input);
        panel.add(new JLabel("City: "));
        panel.add(CityInput);
        panel.add(new JLabel("Postcode: "));
        panel.add(PostcodeInput);
        panel.add(new JLabel("Email: "));
        panel.add(emailInput);
        panel.add(new JLabel("Phone Number: "));
        panel.add(phoneNumberInput);
        panel.add(new JLabel("Next Booking Date: "));
        panel.add(nextBookingInput);
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ View Customer Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            successful = true;
        }
    }

    public static String toString(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Vehicle", JOptionPane.ERROR_MESSAGE);
    }

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
