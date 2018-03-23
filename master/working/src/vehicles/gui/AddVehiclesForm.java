package vehicles.gui;

import common.Database;
import customers.logic.Customer;
import vehicles.logic.Vehicle;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import vehicles.logic.VehicleTemplate;
import vehicles.logic.Warranty;

/**
 *
 * @author Akhil
 */
public final class AddVehiclesForm extends javax.swing.JFrame {

    private DateFormat format = new SimpleDateFormat("dd/MM/yyyy");//Source:http://www.java2s.com/Tutorial/Java/0240__Swing/JFormattedTextFieldwithSimpleDateFormat.htm
    private final JTextField RegistrationNoInput = new javax.swing.JTextField();
    private final JComboBox<VehicleTemplate> CarDetailsComboBox = new javax.swing.JComboBox<>();
    private final JTextField ColourInput = new javax.swing.JTextField();
    private final JTextField MOTRenewalDateInput = new javax.swing.JFormattedTextField(format);
    private final JRadioButton WarrantyBoolean = new javax.swing.JRadioButton();
    private final JButton ManageVehicleTemplate = new javax.swing.JButton("Manage Vehicle Templates");
    private final JTextField CurrentMileageInput = new javax.swing.JTextField();
    private final JComboBox<Customer> CustomerID = new javax.swing.JComboBox<>();
    private final JTextField LastServiceDateInput = new javax.swing.JFormattedTextField(format);
    private final JComboBox<Warranty> WarrantyID = new javax.swing.JComboBox<>();

    private String RegistrationNo;
    private boolean Warranty = false;
    private boolean successful = false;

    @Override
    public void show() {
        //Display all compnnts of Panel in GUI
        GridLayout layout = new GridLayout(10, 5);
        layout.setHgap(5);
        layout.setVgap(5);
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("RegistrationNo: "));
        panel.add(RegistrationNoInput);
        panel.add(new JLabel("Vehicle Details "));
        panel.add(CarDetailsComboBox);
        loadVehicleTemplates();// call to loadVehicleTemplates method that adds all the menu items in the combobox
        panel.add(new JLabel("Manage Vehicle Details "));
        panel.add(ManageVehicleTemplate);
        panel.add(new JLabel("Colour: "));
        panel.add(ColourInput);
        panel.add(new JLabel("MOTRenewalDate: "));
        panel.add(MOTRenewalDateInput);
        panel.add(new JLabel("CurrentMileage: "));
        panel.add(CurrentMileageInput);
        panel.add(new JLabel("Customer ID:"));
        panel.add(CustomerID);
        loadCustomers();// call to loadcustomers method that adds all the menu items in the combobox
        panel.add(new JLabel("LastServiceDate"));
        panel.add(LastServiceDateInput);
        panel.add(new JLabel("hasWarranty: "));
        panel.add(WarrantyBoolean);
        panel.add(new JLabel("Warranty ID"));
        panel.add(WarrantyID);
        try {
            loadWarrantyDetails();//in try catch statement due to source code
        } catch (ParseException ex) {
            Logger.getLogger(AddVehiclesForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        MOTRenewalDateInput.setText("dd/MM/yyyy");
        LastServiceDateInput.setText("dd/MM/yyyy");
        WarrantyID.setEnabled(WarrantyBoolean.isSelected());
        WarrantyBoolean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BooleanButtonActionPerformed(evt);
            }
        });
        ManageVehicleTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageVehicleTemplateButtonActionPerformed(evt);
            }
        });
        RegistrationNoInput.getDocument().addDocumentListener(new DocumentListener() {//http://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
            public void changedUpdate(DocumentEvent e) {
                try {
                    warn();
                } catch (SQLException ex) {
                }
            }

            public void removeUpdate(DocumentEvent e) {
                try {
                    warn();
                } catch (SQLException ex) {
                }
            }

            public void insertUpdate(DocumentEvent e) {
                try {
                    warn();
                } catch (SQLException ex) {
                }
            }

            public void warn() throws SQLException {
                if (Database.getInstance().VehicleExists(RegistrationNoInput.getText().toUpperCase())) {
                    showErrorDialog("Registration No already exists.Please type in another Reg No");
                    return;
                }
            }
        });
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ Add Vehicle Record", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (Database.getInstance().VehicleExists(RegistrationNo)) {
                    showErrorDialog("Invalid RegistrationNo, it is already taken.");
                    return;
                }
                if (ColourInput.getText().trim().length() == 0||RegistrationNoInput.getText().trim().length() == 0) {
                    showErrorDialog("empty spaces cannot be inputted");
                    return;
                }
                try {
                    if (Double.parseDouble(CurrentMileageInput.getText()) > 100000) {
                        showErrorDialog("The current mileage cannot be greater than 100000 or less than 0");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid input, please type in a number");
                }
                if (CarDetailsComboBox.getSelectedIndex() == -1 || RegistrationNoInput.getText().isEmpty() || ColourInput.getText().isEmpty() || MOTRenewalDateInput.getText().isEmpty() || CurrentMileageInput.getText().isEmpty()) {//checks if any of the textfields are empty
                    showErrorDialog("Invalid Vehicle Details, none of the fields should be empty or unselected.");
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Unable to interact with the database.\r\nError: " + ex.getMessage(),
                        "Database error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Warranty = WarrantyBoolean.isSelected();
            successful = true;
        }
    }

    private void ManageVehicleTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ManageVehicleTemplatePrompt mwd = new ManageVehicleTemplatePrompt();
        mwd.setVisible(true);
        this.setVisible(false);
    }

    private void BooleanButtonActionPerformed(java.awt.event.ActionEvent evt) {
        WarrantyID.setEnabled(WarrantyBoolean.isSelected());
    }

    public boolean isSuccessful() {
        return successful;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Vehicle", JOptionPane.ERROR_MESSAGE);
    }

    public Vehicle parseVehicle() throws ParseException {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Vehicle(RegistrationNoInput.getText(), (VehicleTemplate) CarDetailsComboBox.getSelectedItem(), ColourInput.getText(), toDate(MOTRenewalDateInput.getText()), WarrantyBoolean.isSelected(), toDate(LastServiceDateInput.getText()), Double.parseDouble(CurrentMileageInput.getText()), (Customer) CustomerID.getSelectedItem(), (Warranty) WarrantyID.getSelectedItem());
    }

    private Calendar toDate(String text) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(text));
        return cal;//http://stackoverflow.com/questions/11791513/converting-string-to-calendar-what-is-the-easiest-way
    }

    private void loadVehicleTemplates() {
        try {
            ArrayList<VehicleTemplate> vehicletemplates = Database.getInstance().getAllTemplate();
            for (VehicleTemplate v : vehicletemplates) {
                CarDetailsComboBox.addItem(v);
            }
        } catch (SQLException ex) {

        }
    }

    private void loadCustomers() {
        try {
            ArrayList<Database.ExtCustomer> customers = Database.getInstance().getAllCustomerIDsforVehicles();
            for (Database.ExtCustomer c : customers) {
                CustomerID.addItem(c);
            }
        } catch (SQLException ex) {

        }
    }

    private void loadWarrantyDetails() throws ParseException {
        try {
            ArrayList<Warranty> warranty = Database.getInstance().getAllWarrantyDetailsforVehicle();
            for (Warranty w : warranty) {
                WarrantyID.addItem(w);
            }
        } catch (SQLException ex) {

        }
    }
}
