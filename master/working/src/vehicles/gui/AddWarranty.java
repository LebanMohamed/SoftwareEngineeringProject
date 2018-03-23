package vehicles.gui;

import java.awt.GridLayout;
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
 *
 * @author Akhil
 */
public class AddWarranty {

    private final JTextField WarrantyIDInput = new javax.swing.JTextField();
    private final JTextField NameInput = new javax.swing.JTextField();
    private final JTextField AddressLineInput = new javax.swing.JTextField();
    private final JTextField TownInput = new javax.swing.JTextField();
    private final JTextField CityInput = new javax.swing.JTextField();
    private final JTextField PostcodeInput = new javax.swing.JTextField();
    private final JTextField ExpiryDateInput = new javax.swing.JTextField();

    private boolean successful = false;

    public void show() {
        //Display all compnnts of Panel in GUI
        GridLayout layout = new GridLayout(7, 5);
        layout.setHgap(5);
        layout.setVgap(5);
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("WarrantyID: "));
        panel.add(WarrantyIDInput);
        panel.add(new JLabel("Name: "));
        panel.add(NameInput);
        panel.add(new JLabel("Address Line 1: "));
        panel.add(AddressLineInput);
        panel.add(new JLabel("Town: "));
        panel.add(TownInput);
        panel.add(new JLabel("City: "));
        panel.add(CityInput);
        panel.add(new JLabel("Postcode: "));
        panel.add(PostcodeInput);
        panel.add(new JLabel("Expiry Date: "));
        panel.add(ExpiryDateInput);

        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ Add Warranty Company", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
             if (WarrantyIDInput.getText().isEmpty() || NameInput.getText().isEmpty() || AddressLineInput.getText().isEmpty() || TownInput.getText().isEmpty() || CityInput.getText().isEmpty()|| PostcodeInput.getText().isEmpty()|| ExpiryDateInput.getText().isEmpty()) {//checks if any of the textfields are empty
                    showErrorDialog("Invalid Warranty Company Details, none of the fields should be empty.");
                    return;
                }
            successful = true;
        }
        if (WarrantyIDInput.getText().trim().length() == 0 || NameInput.getText().trim().length() == 0 || AddressLineInput.getText().trim().length() == 0 || TownInput.getText().trim().length() == 0 || CityInput.getText().trim().length() == 0 || PostcodeInput.getText().trim().length() == 0 || ExpiryDateInput.getText().trim().length() == 0) {
            showErrorDialog("empty spaces cannot be inputted");
            return;
        }
    }

    public boolean isSuccessful() {
        return successful;
    }
    
    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Warranty Company", JOptionPane.ERROR_MESSAGE);
    }
    
    public Warranty parseWarranty() throws ParseException {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Warranty(Integer.parseInt(WarrantyIDInput.getText()), NameInput.getText(), AddressLineInput.getText(), TownInput.getText(), CityInput.getText(), PostcodeInput.getText(), toDate(ExpiryDateInput.getText()));
    }

    private Calendar toDate(String text) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(text));
        return cal;
    }
}
