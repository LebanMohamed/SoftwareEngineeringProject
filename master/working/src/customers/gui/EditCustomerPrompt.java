package customers.gui;

import common.Database;
import common.gui.util.GenericPrompt;
import customers.logic.Customer;
import customers.logic.Customer.CustomerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 *
 * @author cawaala
 */
public final class EditCustomerPrompt extends GenericPrompt {

    //private final JTextField cusIdTextField = new JTextField(20);
    private final JTextField cusFullNameTextField = new JTextField(20);
    private final JTextField cusPhoneTextField = new JTextField(20);
    private final JTextField cusEmailTextField = new JTextField(20);
    private final JTextField cusCityTextField = new JTextField(20);
    private final JTextField cusStreetAddressTextField = new JTextField(20);
    private final JTextField cusPostcodeTextField = new JTextField(20); 
    private final JComboBox<String> checkType = new JComboBox<>(); 
    private final int cusId;
    public CustomerType type;

    public EditCustomerPrompt(int cusID) {
        cusId = cusID;

    }

    @Override
    public void show() {
        // Design prompt
        Customer cus;
        try {
            cus = Database.getInstance().getCustomer(cusId);
            cusFullNameTextField.setText(cus.getFullName());
            cusPhoneTextField.setText(cus.getPhone());
            cusEmailTextField.setText(cus.getEmail());
            cusCityTextField.setText(cus.getCity());
            cusStreetAddressTextField.setText(cus.getStreetAddress());
            cusPostcodeTextField.setText(cus.getPostcode());
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to interact with the database.\r\nError: " + e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            successful = false;
            return;
        }
        cusPhoneTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (!Character.isDigit(c)) {
                    ke.consume();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }
        });
        // Design prompt
        GridLayout layout = new GridLayout(8, 2);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(new JLabel("ID: "));
        panel.add(new JLabel("" + cusId));
        panel.add(new JLabel("FullName: "));
        panel.add(cusFullNameTextField);
        panel.add(new JLabel("Phone: "));
        panel.add(cusPhoneTextField);
        panel.add(new JLabel("Email: "));
        panel.add(cusEmailTextField);
        panel.add(new JLabel("City: "));
        panel.add(cusCityTextField);
        panel.add(new JLabel("Street address: "));
        panel.add(cusStreetAddressTextField);
        panel.add(new JLabel("Postcode: "));
        panel.add(cusPostcodeTextField);
        panel.add(new JLabel("Type: "));
        checkType.addItem("Private");
        checkType.addItem("Business");
        panel.add(checkType);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Edit Customer", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values

            if (cusFullNameTextField.getText().matches("^\\s*$")
                    || cusStreetAddressTextField.getText().matches("^\\s*$")
                    || cusPhoneTextField.getText().matches("^\\s*$")
                    || cusEmailTextField.getText().matches("^\\s*$")
                    || cusPostcodeTextField.getText().matches("^\\s*$")
                    || cusCityTextField.getText().matches("^\\s*$")) {
                showErrorDialog("Invalid all Customer details should not be blank.");
                return;
            }

            // Fall-back
            if (checkType.getSelectedItem().toString().equals("Private")) {
                type = CustomerType.Private;
            } else {
                type = CustomerType.Business;
            }

            successful = true;
        }
    }

    public Customer parseCustomer() {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Customer(cusId, cusFullNameTextField.getText(), cusPhoneTextField.getText(), cusEmailTextField.getText(),
                cusCityTextField.getText(), cusStreetAddressTextField.getText(), cusPostcodeTextField.getText(), type);
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Editing Customer", JOptionPane.ERROR_MESSAGE);
    }

}
