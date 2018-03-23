package customers.gui.Search;

 
import common.gui.util.GenericPrompt; 
import customers.logic.Filter;
import javax.swing.*;
import java.awt.*; 

/**
 *
 * @author cawaala
 */
public final class SearchCustomerPrompt extends GenericPrompt {

    private final ButtonGroup group = new ButtonGroup();

    private final JTextField fullname = new JTextField();
    private final JTextField firstname = new JTextField();
    private final JTextField Surname = new JTextField();
    private final JTextField vehicle = new JTextField();

    private final JRadioButton fullnameRadioButton = new JRadioButton("Full Name");
    private final JRadioButton firstnameRadioButton = new JRadioButton("First Name");
    private final JRadioButton surnameRadioButton = new JRadioButton("Surname");
    private final JRadioButton VehicleRadioButton = new JRadioButton("Registration Number");

    Filter filter;

    @Override
    public void show() {

        GridLayout layout = new GridLayout(4, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(fullnameRadioButton);
        panel.add(fullname);
        panel.add(firstnameRadioButton);
        panel.add(firstname);
        panel.add(surnameRadioButton);
        panel.add(Surname);
        panel.add(VehicleRadioButton);
        panel.add(vehicle);
        initRadioButtons();

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Search Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
      

        if (result == JOptionPane.OK_OPTION) {
            
              if (!fullnameRadioButton.isSelected()
                && !firstnameRadioButton.isSelected()
                && !surnameRadioButton.isSelected()
                && !VehicleRadioButton.isSelected()) {
            showErrorDialog("Radio buttons must be selected.");
            return;
        }
              
            if (fullnameRadioButton.isSelected()) {
                if (fullname.getText().isEmpty()) {
                    showErrorDialog("Customer's full name cannot be empty.");
                    return;
                }
                filter = new Filter("FullName", fullname.getText());
            }

            if (firstnameRadioButton.isSelected()) {
                if (firstname.getText().isEmpty()) {
                    showErrorDialog("Customer's firstname cannot be empty.");
                    return;
                }
                filter = new Filter("FirstName", firstname.getText());
            }
            if (surnameRadioButton.isSelected()) {
                if (Surname.getText().isEmpty()) {
                    showErrorDialog("Customer's surname cannot be empty.");
                    return;
                }
                filter = new Filter("SurName", Surname.getText());
            }
            if (VehicleRadioButton.isSelected()) {
                if (vehicle.getText().isEmpty()) {
                    showErrorDialog("Customer's Registration Number cannot be empty.");
                    return;
                }
                filter = new Filter("Vehicle", vehicle.getText());
            }
            // Fall-back
            successful = true;
        }

    }

    private void initRadioButtons() {
        //ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
        group.add(fullnameRadioButton);
        group.add(firstnameRadioButton);
        group.add(surnameRadioButton);
        group.add(VehicleRadioButton);

    }

    public boolean isSuccessful() {
        return successful;
    }

    public Filter getFilter() {
        return filter;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Searching Customers", JOptionPane.ERROR_MESSAGE);
    }
}
