package vehicles.gui;

import common.Database;
import common.Database.ExtCustomer;
import customers.logic.Customer;
import vehicles.logic.Vehicle;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import vehicles.logic.VehicleTemplate;
import vehicles.logic.Warranty;

/**
 *
 * @author Akhil
 */
public class EditVehiclesForm extends javax.swing.JFrame implements ActionListener {

    private DateFormat format = new SimpleDateFormat("dd/MM/yyyy");//Source:http://www.java2s.com/Tutorial/Java/0240__Swing/JFormattedTextFieldwithSimpleDateFormat.htm
    private final JTextField RegistrationNoInput = new javax.swing.JTextField();
    private final JComboBox<VehicleTemplate> CarDetailsComboBox = new javax.swing.JComboBox<>();
    private final JTextField ColourInput = new javax.swing.JTextField();
    private final JTextField MOTRenewalDateInput = new javax.swing.JFormattedTextField(format);
    private final JRadioButton WarrantyBoolean = new javax.swing.JRadioButton();
    private final JButton ManageVehicleTemplate = new javax.swing.JButton("Manage Vehicle Templates");
    private final JTextField CurrentMileageInput = new javax.swing.JTextField();
    private final JComboBox<ExtCustomer> CustomerID = new javax.swing.JComboBox<>();
    private final JTextField LastServiceDateInput = new javax.swing.JFormattedTextField(format);
    private final JComboBox<Warranty> WarrantyID = new javax.swing.JComboBox<>();
    private final String RegistrationNo;
    private boolean Warranty;
    private boolean successful = false;

    public EditVehiclesForm(String RegNo) {
        RegistrationNo = RegNo;
    }

    public void show() {
        
        Vehicle v;
        try {
            v = Database.getInstance().getVehicle(RegistrationNo);
            loadVehicleTemplates();
            loadCustomers();
            try {
                loadWarrantyDetails();//in try catch statement due to source code
            } catch (ParseException ex) {
                Logger.getLogger(AddVehiclesForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            

            CarDetailsComboBox.setSelectedIndex(v.getTemplate().getId() - 1);
            ColourInput.setText(v.getColour());
            MOTRenewalDateInput.setText(toStringtest(v.getMOTRenewalDate()));
            WarrantyBoolean.setSelected(v.isWarrantyActive());
            CurrentMileageInput.setText(String.valueOf(v.getCurrentMileage()));
            CustomerID.setSelectedIndex(v.getCustomer().getId() - 1);
            LastServiceDateInput.setText(toStringtest(v.getLastServiceDate()));
            
            int warrantyid;
            if (v.isWarrantyActive() == false) {
                warrantyid = 0;
            } else {
                warrantyid = v.getWarranty().getId();
            }
            WarrantyID.setSelectedIndex(warrantyid);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to interact with the database.\r\nError: " + e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            successful = false;
            return;
        } catch (ParseException ex) {

        }
        // Design prompt
        GridLayout layout = new GridLayout(10, 5);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("RegistrationNo: "));
        panel.add(new JLabel("" + RegistrationNo));
        panel.add(new JLabel("Vehicle Details "));
        panel.add(CarDetailsComboBox);
        panel.add(new JLabel("Manage Vehicle Templates: "));
        panel.add(ManageVehicleTemplate);
        panel.add(new JLabel("Colour: "));
        panel.add(ColourInput);
        panel.add(new JLabel("MOTRenewalDate: "));
        panel.add(MOTRenewalDateInput);
        panel.add(new JLabel("CurrentMileage: "));
        panel.add(CurrentMileageInput);
        panel.add(new JLabel("Customer "));
        panel.add(CustomerID);
        panel.add(new JLabel("LastServiceDate"));
        panel.add(LastServiceDateInput);
        panel.add(new JLabel("Warranty: "));
        panel.add(WarrantyBoolean);
        panel.add(new JLabel("Warranty "));
        panel.add(WarrantyID);
        pack();

        WarrantyID.setEnabled(WarrantyBoolean.isSelected());
        WarrantyBoolean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BooleanButtonActionPerformed(evt);
            }
        });
        ManageVehicleTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageVehTempButtonActionPerformed(evt);
            }
        });
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ Edit Vehicle Record", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (ColourInput.getText().trim().length() == 0) {
                showErrorDialog("empty spaces cannot be inputted");
            }
            try {
                if (Double.parseDouble(CurrentMileageInput.getText()) > 100000) {
                    showErrorDialog("The current mileage cannot be greater than 100000 or less than 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid input, please type in a number");
            }
            if (ColourInput.getText().isEmpty() || MOTRenewalDateInput.getText().isEmpty() || CurrentMileageInput.getText().isEmpty()) {
                showErrorDialog("Invalid Vehicle Details, none of the fields should be empty or unselected.");
                return;
            }
            successful = true;
        }
    }

    private void ManageVehTempButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ManageVehicleTemplatePrompt mwd1 = new ManageVehicleTemplatePrompt();
        mwd1.setVisible(true);
        this.setVisible(false);
    }

    private void BooleanButtonActionPerformed(java.awt.event.ActionEvent evt) {
        WarrantyID.setEnabled(WarrantyBoolean.isSelected());
    }

    public boolean isSuccessful() {
        return successful;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Editing Vehicle", JOptionPane.ERROR_MESSAGE);
    }

    public Vehicle parseVehicle() throws ParseException {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new Vehicle(RegistrationNo, (VehicleTemplate) CarDetailsComboBox.getSelectedItem(), ColourInput.getText(), toDate(MOTRenewalDateInput.getText()), WarrantyBoolean.isSelected(), toDate(LastServiceDateInput.getText()), Double.parseDouble(CurrentMileageInput.getText()), (Customer) CustomerID.getSelectedItem(), (Warranty) WarrantyID.getSelectedItem());
    }

    private Calendar toDate(String text) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(text));
        return cal;
    }
    
    public String toStringtest(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
