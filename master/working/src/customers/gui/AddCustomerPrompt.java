package customers.gui;

import common.Database;
import common.gui.util.GenericPrompt;
import customers.logic.Customer;
import customers.logic.Customer.CustomerType;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author cawaala
 */
public final class AddCustomerPrompt extends GenericPrompt {

    private final JTextField customerIdTextField = new JTextField(20);
    private final JTextField fullnameTextField = new JTextField(20);
    private final JTextField phoneTextField = new JTextField(20);
    private final JTextField emailaddressTextField = new JTextField(20);
    private final JTextField cityaddressTextField = new JTextField(20);
    private final JTextField streetaddressTextField = new JTextField(20);
    private final JTextField postcodeTextField = new JTextField(20);
    private final JComboBox<String> checkType = new JComboBox<>();
    private boolean successful = false;
    private int customerId;

    public CustomerType type;

    public void show() {

        phoneTextField.addKeyListener(new KeyAdapter() {
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
        panel.add(new JLabel("Customer ID: "));
        panel.add(customerIdTextField);
        panel.add(new JLabel("Full Name: "));
        panel.add(fullnameTextField);
        panel.add(new JLabel("Phone: "));
        panel.add(phoneTextField);
        panel.add(new JLabel("Email Address: "));
        panel.add(emailaddressTextField);
        panel.add(new JLabel("City: "));
        panel.add(cityaddressTextField);
        panel.add(new JLabel("Street Address: "));
        panel.add(streetaddressTextField);
        panel.add(new JLabel("Postcode: "));
        panel.add(postcodeTextField);
        panel.add(new JLabel("Customer Type: "));
        checkType.addItem("Private");
        checkType.addItem("Business");
        panel.add(checkType);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Add Customer", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values
            try {
                customerId = Integer.parseInt(customerIdTextField.getText());
                if (customerId < 1 || customerId > 99_999) {
                    throw new NumberFormatException("customerId out of bounds");
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid customer ID, it should be between 1 to 5 degits long excluding the number 0.");
                return;
            }

            // Check if first name, surname or password are blank
            if (fullnameTextField.getText().matches("^\\s*$")
                    || streetaddressTextField.getText().matches("^\\s*$")
                    || phoneTextField.getText().matches("^\\s*$")
                    || emailaddressTextField.getText().matches("^\\s*$")
                    || postcodeTextField.getText().matches("^\\s*$")
                    || cityaddressTextField.getText().matches("^\\s*$")) {
                showErrorDialog("Invalid all Customer details should not be blank.");
                return;
            }

            if (checkType.getSelectedItem().toString().equals("Private")) {
                type = CustomerType.Private;
            } else {
                type = CustomerType.Business;
            }
            try {
                if (Database.getInstance().customerExists(customerId)) {
                    showErrorDialog("Invalid customer ID, it is already taken.");
                    return;
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Unable to interact with the database.\r\nError: " + ex.getMessage(),
                        "Database error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fall-back
            successful = true;
        }

    }

    public Customer parseCustomer() {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Customer(customerId, fullnameTextField.getText(), phoneTextField.getText(), emailaddressTextField.getText(),
                cityaddressTextField.getText(), streetaddressTextField.getText(), postcodeTextField.getText(), type);
    }

    public boolean isSuccessful() {
        return successful;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Customer", JOptionPane.ERROR_MESSAGE);
    }

}
