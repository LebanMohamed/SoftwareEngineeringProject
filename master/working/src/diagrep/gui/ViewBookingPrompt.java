package diagrep.gui;

import common.gui.util.GenericPrompt;
import common.gui.util.JPanelBuilder;
import common.gui.util.SwingUtils;
import diagrep.logic.Booking;
import diagrep.logic.DateHelper;
import diagrep.logic.DiagnosisRepairBooking;
import parts.gui.ManageInstallParts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JOptionPane wrapper to display a {@link diagrep.logic.Booking}.
 */
public final class ViewBookingPrompt extends GenericPrompt {

    private final JComboBox<String> typeComboBox = new JComboBox<>();
    private final JTextField vehicleMileageTextField = new JTextField(20);
    private final JTextField startDateTextField = new JTextField(20);
    private final JTextField endDateTextField = new JTextField(20);
    private final JTextField amountPaidTextField = new JTextField(20);
    private final JTextField baseCostTextField = new JTextField(20);
    private final JTextField amountDueTextField = new JTextField(20);
    private final JTextField mechanicTextField = new JTextField(20);
    private final JTextField mechanicTimeSpentTextField = new JTextField(20);
    private final JTextField customerTextField = new JTextField(20);
    private final JTextField vehicleTextField = new JTextField(20);
    private final JTextField settledTextField = new JTextField(20);
    private final JButton viewPartsButton = new JButton("View Parts");
    private final Booking booking;

    ViewBookingPrompt(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void show() {
        // Design prompt
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(13, 2, 5, 5))
                // Basic booking details
                .addLabel("Type:")
                .add(typeComboBox)
                .addLabel("Vehicle Mileage:")
                .add(vehicleMileageTextField)
                .addLabel("Start Date:")
                .add(startDateTextField)
                .addLabel("End Date:")
                .add(endDateTextField)
                .addLabel("Mechanic:")
                .add(mechanicTextField)
                .addLabel("Mechanic Time Spent:")
                .add(mechanicTimeSpentTextField)
                .addLabel("Customer:")
                .add(customerTextField)
                .addLabel("Vehicle:")
                .add(vehicleTextField)
                // Invoice details
                .addLabel("Base Cost:")
                .add(baseCostTextField)
                .addLabel("Amount Paid:")
                .add(amountPaidTextField)
                .addLabel("Amount Due:")
                .add(amountDueTextField)
                .addLabel("Payment Complete?")
                .add(settledTextField)
                // Buttons for further details
                .add(viewPartsButton)
                .addEmptyLabel()
                .getPanel();

        // Set component states
        customerTextField.setEditable(false);
        vehicleTextField.setEditable(false);
        mechanicTextField.setEditable(false);
        amountDueTextField.setEditable(false);
        settledTextField.setEditable(false);
        vehicleMileageTextField.setEditable(false);
        startDateTextField.setEditable(false);
        endDateTextField.setEditable(false);
        baseCostTextField.setEditable(false);
        amountPaidTextField.setEditable(false);
        mechanicTimeSpentTextField.setEditable(false);

        // Add type combobox behaviour
        typeComboBox.setEnabled(false);
        typeComboBox.addItem("Diagnosis & Repair");
        typeComboBox.addItem("Scheduled Maintenance");
        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = typeComboBox.getSelectedIndex();
                vehicleMileageTextField.setEnabled(idx == 0); // toggling vehicle mileage enabled
            }
        });

        // Behaviour for the buttons
        viewPartsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ManageInstallParts installParts = new ManageInstallParts();
                    // TODO implement some kind of search?
                    SwingUtils.closeWindowAncestorOf(viewPartsButton);
                    installParts.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error viewing parts.", "View Booking - Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Set default values
        initValues();

        // Show prompt
        JOptionPane.showOptionDialog(null, panel, "View Booking", JOptionPane.NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK"}, "OK"); // Source: https://stackoverflow.com/questions/16511039/is-there-a-way-to-only-have-the-ok-button-in-a-joptionpane-showinputdialog-and/16511133#16511133
    }

    private void initValues() {
        typeComboBox.setSelectedIndex(booking instanceof DiagnosisRepairBooking ? 0 : 1);
        vehicleMileageTextField.setText(booking instanceof DiagnosisRepairBooking
                ? ((DiagnosisRepairBooking)booking).getVehicleMileage() + ""
                : "0");
        startDateTextField.setText(DateHelper.toString(booking.getBookingStartDate()));
        endDateTextField.setText(DateHelper.toString(booking.getBookingEndDate()));
        mechanicTextField.setText(booking.getMechanic().getFullName());
        customerTextField.setText(booking.getCustomer().getFullName());
        vehicleTextField.setText(booking.getVehicle().getRegistrationNo());
        mechanicTimeSpentTextField.setText(DateHelper.toString(booking.getMechanicTimeSpent()).split(" ")[1] + "");
        baseCostTextField.setText(booking.getInvoice().getBookingBaseCost() + "");
        amountPaidTextField.setText(booking.getInvoice().getAmtPaid() + "");
        amountDueTextField.setText(booking.getInvoice().getTotalCost() + "");
        settledTextField.setText(booking.getInvoice().isSettled() ? "Yes" : "No");
    }
}
