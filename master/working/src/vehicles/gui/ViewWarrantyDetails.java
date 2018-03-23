package vehicles.gui;

import common.Database;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import vehicles.logic.Warranty;

/**
 * @author Akhil
 */
public class ViewWarrantyDetails {

    private final JTextField companyNameInput = new javax.swing.JTextField();
    private final JTextField AddressLine1Input = new javax.swing.JTextField();
    private final JTextField TownInput = new javax.swing.JTextField();
    private final JTextField CityInput = new javax.swing.JTextField();
    private final JTextField PostcodeInput = new javax.swing.JTextField();
    private final JTextField expiryDateInput = new javax.swing.JTextField();
    private final int WarrantyID;
    private boolean successful = false;

    public ViewWarrantyDetails(int warrantyid) {
        WarrantyID = warrantyid;
    }

    public void show() {
        Warranty w;
        try {
            w = Database.getInstance().getWarrantyforVehicle(WarrantyID);
            companyNameInput.setText(w.getCompanyName());
            AddressLine1Input.setText(w.getAddressLine1());
            TownInput.setText(w.getTown());
            CityInput.setText(w.getCity());
            PostcodeInput.setText(w.getPostcode());
            if (WarrantyID == 0) {
                expiryDateInput.setText(" ");
            } else {
                expiryDateInput.setText(toString(w.getExpiryDate()));
            }

        } catch (SQLException | ParseException ex) {
        }
        GridLayout layout = new GridLayout(7, 5);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("WarrantyID: "));
        panel.add(new JLabel("" + WarrantyID));
        panel.add(new JLabel("Name:"));
        panel.add(companyNameInput);
        panel.add(new JLabel("Address Line 1: "));
        panel.add(AddressLine1Input);
        panel.add(new JLabel("Town: "));
        panel.add(TownInput);
        panel.add(new JLabel("City: "));
        panel.add(CityInput);
        panel.add(new JLabel("Postcode: "));
        panel.add(PostcodeInput);
        panel.add(new JLabel("Expiry date: "));
        panel.add(expiryDateInput);
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ View and Edit Warranty Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (companyNameInput.getText().isEmpty() || AddressLine1Input.getText().isEmpty() || TownInput.getText().isEmpty() || CityInput.getText().isEmpty() || PostcodeInput.getText().isEmpty() || expiryDateInput.getText().isEmpty()) {
                showErrorDialog("Invalid Warranty Details, none of the fields should be empty.");
                return;
            }
            successful = true;
        }
    }

    public static String toString(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Warranty Company", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSuccessful() {
        return successful;
    }

    private Calendar toDate(String text) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(text));
        return cal;
    }

    public Warranty parseWarranty() throws ParseException {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Warranty(WarrantyID, companyNameInput.getText(), AddressLine1Input.getText(), TownInput.getText(), CityInput.getText(), PostcodeInput.getText(), toDate(expiryDateInput.getText()));
    }

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
