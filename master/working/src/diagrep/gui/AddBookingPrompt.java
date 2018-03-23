package diagrep.gui;

import common.Database;
import common.gui.date.SelectDatePrompt;
import common.gui.util.GenericPrompt;
import common.gui.util.JPanelBuilder;
import common.gui.util.SwingUtils;
import common.gui.util.TextFieldHelper;
import common.logic.Invoice;
import common.util.InvalidDateException;
import common.util.MathHelper;
import customers.logic.Customer;
import diagrep.gui.choose.ChooseCustomerPrompt;
import diagrep.gui.choose.ChooseVehiclePrompt;
import diagrep.gui.mechanic.ChooseMechanicPrompt;
import diagrep.logic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import specialist.gui.AddSPCBookingPrompt;
import vehicles.logic.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * A JOptionPane wrapper that receives user input to create a {@link diagrep.logic.Booking} from.
 */
public final class AddBookingPrompt extends GenericPrompt {

    private static final Logger logger = LoggerFactory.getLogger(AddBookingPrompt.class);
    private final JComboBox<String> typeComboBox = new JComboBox<>();
    private final JTextField vehicleMileageTextField = new JTextField(20);
    private final JTextField startDateTextField = new JTextField(20);
    private final JTextField endDateTextField = new JTextField(20);
    private final JTextField baseCostTextField = new JTextField(20);
    private final JTextField mechanicTextField = new JTextField(20);
    private final JTextField mechanicTimeSpentTextField = new JTextField(20);
    private final JTextField customerTextField = new JTextField(20);
    private final JTextField vehicleTextField = new JTextField(20);
    private final JButton chooseBookingStartDateButton = new JButton("Choose");
    private final JButton chooseMechanicButton = new JButton("Choose");
    private final JButton chooseBookingEndDateButton = new JButton("Choose");
    private final JButton chooseCustomerButton = new JButton("Choose");
    private final JButton chooseVehicleButton = new JButton("Choose");
    private final JButton delegateToSpcButton = new JButton("Delegate to SPC");
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    private Customer customer = null;
    private Vehicle vehicle = null;
    private Mechanic mechanic;
    private Calendar startDate;
    private Calendar endDate;
    private double baseCost;
    private int vehicleMileage;
    private Calendar mechanicTimeSpent;

    public void show() {
        // Design prompt
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(10, 3, 5, 5))
                .addLabel("Type:")
                .add(typeComboBox)
                .addEmptyLabel()
                .addLabel("Vehicle Mileage:")
                .add(vehicleMileageTextField)
                .addEmptyLabel()
                .addLabel("Base Cost:")
                .add(baseCostTextField)
                .addEmptyLabel()
                .addLabel("Start Date:")
                .add(startDateTextField)
                .add(chooseBookingStartDateButton)
                .addLabel("End Date:")
                .add(endDateTextField)
                .add(chooseBookingEndDateButton)
                .addLabel("Mechanic:")
                .add(mechanicTextField)
                .add(chooseMechanicButton)
                .addLabel("Mechanic Hours Spent:")
                .add(mechanicTimeSpentTextField)
                .addEmptyLabel()
                .addLabel("Customer:")
                .add(customerTextField)
                .add(chooseCustomerButton)
                .addLabel("Vehicle:")
                .add(vehicleTextField)
                .add(chooseVehicleButton)
                .add(delegateToSpcButton)
                .add(okButton)
                .add(cancelButton)
                .getPanel();

        // Set component states
        TextFieldHelper.enableDefaultValue(baseCostTextField, "0");
        TextFieldHelper.enableDefaultValue(vehicleMileageTextField, "0");
        TextFieldHelper.enableDefaultValue(mechanicTimeSpentTextField, "00:00:00");
        customerTextField.setEditable(false);
        vehicleTextField.setEditable(false);
        mechanicTextField.setEditable(false);
        chooseVehicleButton.setEnabled(false);

        // Tailor text field possible inputs
        TextFieldHelper.disallowNonDigitInput(vehicleMileageTextField);
        TextFieldHelper.disallowNonNumericInput(baseCostTextField);
        TextFieldHelper.disallowNonDateInput(mechanicTimeSpentTextField);

        // Add type combobox behaviour
        typeComboBox.addItem("Diagnosis & Repair");
        typeComboBox.addItem("Scheduled Maintenance");
        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = typeComboBox.getSelectedIndex();
                vehicleMileageTextField.setEnabled(idx == 0); // toggling vehicle mileage enabled
                delegateToSpcButton.setEnabled(idx == 0); // toggling spc delegation enabled
            }
        });

        // Behaviour for the buttons
        SelectDatePrompt.setAsChooseDateComponents(chooseBookingStartDateButton, startDateTextField, true);
        SelectDatePrompt.setAsChooseDateComponents(chooseBookingEndDateButton, endDateTextField, true);
        chooseCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseCustomerPrompt prompt = new ChooseCustomerPrompt();
                prompt.show();

                if (prompt.isSuccessful()) {
                    customer = prompt.getSelectedCustomer();
                    customerTextField.setText(customer.getFullName());

                    chooseVehicleButton.setEnabled(true);
                    vehicle = null;
                    vehicleTextField.setText("");
                }
            }
        });
        chooseVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseVehiclePrompt prompt = new ChooseVehiclePrompt(customer.getId());
                prompt.show();

                if (prompt.isSuccessful()) {
                    vehicle = prompt.getSelectedVehicle();
                    vehicleTextField.setText(vehicle.getRegistrationNo());
                }
            }
        });
        chooseMechanicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseMechanicPrompt prompt = new ChooseMechanicPrompt();
                prompt.show();

                if (prompt.isSuccessful()) {
                    mechanic = prompt.getSelectedMechanic();
                    mechanicTextField.setText(mechanic.getFullName());
                }
            }
        });
        delegateToSpcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateValues())
                    return;

                // Attempt to add booking to db
                Booking booking = attemptAddToDatabase();

                // Close window
                SwingUtils.closeWindowAncestorOf(delegateToSpcButton);

                // Show spc booking prompt
                if (successful) {
                    AddSPCBookingPrompt prompt = new AddSPCBookingPrompt(booking);
                    prompt.show();
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateValues())
                    return;

                // Attempt to add booking to db
                attemptAddToDatabase();

                // Close window
                SwingUtils.closeWindowAncestorOf(okButton);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close window
                SwingUtils.closeWindowAncestorOf(cancelButton);
            }
        });

        // Show prompt
        JOptionPane.showOptionDialog(null, panel, "Add Booking", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, new Object[] {}, null);
    }

    private Booking attemptAddToDatabase() {
        Booking booking;

        try {
            booking = parseBooking();
            Database.getInstance().addBooking(booking);
            JOptionPane.showMessageDialog(null, "You have successfully added the booking.\nIt may not show in the"
                            + " bookings table if a search filter is active, until you clear it.", "Add Booking",
                    JOptionPane.INFORMATION_MESSAGE);
            successful = true;
            logger.info("Successfully added booking to database.");
            return booking;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred while adding the booking.\nMessage: "
                    + ex.getMessage(), "Add Booking - Error", JOptionPane.ERROR_MESSAGE);
            successful = false;
            logger.info("Failed to add booking to database.");
            return null;
        }
    }

    private Booking parseBooking() {
        Booking booking;

        if (typeComboBox.getSelectedItem().equals("Diagnosis & Repair")) {
            booking = new DiagnosisRepairBooking(-1, startDate, endDate);
            ((DiagnosisRepairBooking)booking).setVehicleMileage(vehicleMileage);
        } else {
            booking = new ScheduledMaintenanceBooking(-1, startDate, endDate);
        }
        booking.setMechanic(mechanic);
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setMechanicTimeSpent(mechanicTimeSpent);

        // Set invoice
        Invoice invoice = new Invoice();
        booking.setInvoice(invoice);
        invoice.setBookingBaseCost(MathHelper.round(baseCost, 2));

        // Calculate total booking cost for invoice, formula: base_cost + mechanic_cost
        double totalCost = invoice.getBookingBaseCost() + booking.mechanicCost();
        totalCost = MathHelper.round(totalCost);
        invoice.setBookingTotalCost(totalCost);
        invoice.setBookingAmountDue(totalCost);

        // Check vehicle warranty status
        if (vehicle.isWarrantyActive() && vehicle.getWarranty().getExpiryDate().compareTo(endDate) >= 0) {
            logger.debug("Vehicle warranty is active for this booking.");
            invoice.activateVehicleWarranty();
        }
        return booking;
    }
    
    private boolean validateValues() {
        // Ensure customer, vehicle and mechanic is set
        if (!validateNonEmpty(customerTextField, "customer")) return false;
        if (!validateNonEmpty(vehicleTextField, "vehicle")) return false;
        if (!validateNonEmpty(mechanicTextField, "mechanic")) return false;

        // Check mechanic hrs spent format
        try {
            mechanicTimeSpent = DateHelper.toCalendarHour(mechanicTimeSpentTextField.getText());
        } catch (IllegalArgumentException ex) {
            showErrorDialog("Invalid mechanic time spent, enter a time like: 03:15:00 for 3 hours 15 minutes.");
            return false;
        }

        // Check vehicle mileage format
        if (typeComboBox.getSelectedIndex() == 1) {
            try {
                vehicleMileage = Integer.parseInt(vehicleMileageTextField.getText());

                if (vehicleMileage > 1_000_000) {
                    showErrorDialog("Invalid vehicle mileage value, it cannot be more than 1 million.");
                    return false;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid vehicle mileage format, enter a whole number, e.g. 35000, 100, 10, 0.");
                return false;
            }
        }

        // Validate base cost format
        try {
            baseCost = Double.parseDouble(baseCostTextField.getText());

            if (baseCost > 100_000) {
                showErrorDialog("Invalid base cost value, it cannot be more than £100,000.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid base cost format, enter a fractional or whole number, e.g. 30.0 or 30");
            return false;
        }

        // Check date formats
        try {
            startDate = DateHelper.toCalendar(startDateTextField.getText());
            endDate = DateHelper.toCalendar(endDateTextField.getText());
        } catch (IllegalArgumentException ex) {
            showErrorDialog("Invalid date format, use the choose button to set it.");
            return false;
        }

        // Check date suitabilities
        try {
            DateHelper.validateBookingDates(startDate, endDate);
        } catch (InvalidDateException e) {
            showErrorDialog("Invalid dates selected: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            showErrorDialog("Error connecting to database, cannot check date availabilities.\nError Message: "
                    + e.getMessage());
            return false;
        }

        // Compare mechanic time spent to date range
        int length1 = DateHelper.seconds(endDate) - DateHelper.seconds(startDate);
        int length2 = DateHelper.seconds(mechanicTimeSpent);

        if (length2 > length1) {
            showErrorDialog("Invalid mechanic time spent, it cannot be longer than the booking itself.");
            return false;
        }

        // Fall-back
        return true;
    }

    private boolean validateNonEmpty(JTextField tf, String name) {
        if (tf.getText().isEmpty()) {
            showErrorDialog("You must select a " + name + " to assign a booking to.");
            return false;
        }
        return true;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Add Booking - Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
