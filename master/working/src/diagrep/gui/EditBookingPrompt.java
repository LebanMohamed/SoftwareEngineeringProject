package diagrep.gui;

import common.Database;
import common.gui.date.SelectDatePrompt;
import common.gui.util.*;
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
import parts.gui.ManageInstallParts;
import specialist.gui.AddSPCBookingPrompt;
import specialist.logic.SPCBooking;
import vehicles.logic.Vehicle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * A JOptionPane wrapper that receives user input to modify a {@link diagrep.logic.Booking}.
 */
public final class EditBookingPrompt extends GenericPrompt {

    private static final Logger logger = LoggerFactory.getLogger(EditBookingPrompt.class);
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
    private final JTextField warrantyActiveTextField = new JTextField(20);
    private final JButton chooseBookingStartDateButton = new JButton("Choose");
    private final JButton chooseMechanicButton = new JButton("Choose");
    private final JButton chooseBookingEndDateButton = new JButton("Choose");
    private final JButton chooseCustomerButton = new JButton("Choose");
    private final JButton chooseVehicleButton = new JButton("Choose");
    private final JButton editPartsButton = new JButton("Edit Parts");
    private final JButton delegateToSpcButton = new JButton("Delegate to SPC");
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    private final Booking booking;
    private Calendar mechanicTimeSpent;
    private int vehicleMileage;
    private double baseCost, amtPaid, partsCost, rawPartsCost;
    private Calendar startDate, endDate;
    private Customer customer = null;
    private Vehicle vehicle = null;
    private Mechanic mechanic = null;
    private boolean pastBooking = false;
    private boolean spcBookingExists;

    EditBookingPrompt(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void show() {
        logger.debug("Editing booking with id: " + booking.getId());

        // Check if this booking is associated with an spc booking
        spcBookingExists = false;

        try {
            SPCBooking spcBooking = Database.getInstance().getSPCBookingForBooking(booking);

            if (spcBooking != null) {
                spcBookingExists = true;
                logger.debug("SPC booking exists with id {}", spcBooking.getSpcBookingId());
            }
        } catch (IllegalArgumentException | ParseException | SQLException ignored) {
        }

        // Design prompt
        JPanelBuilder builder = new JPanelBuilder()
                .setLayout(new GridLayout(spcBookingExists ? 14 : 15, 3, 5, 5))
                // Basic booking details
                .addLabel("Type:")
                .add(typeComboBox)
                .addEmptyLabel()
                .addLabel("Vehicle Mileage:")
                .add(vehicleMileageTextField)
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
                .addLabel("Mechanic Time Spent:")
                .add(mechanicTimeSpentTextField)
                .addEmptyLabel()
                .addLabel("Customer:")
                .add(customerTextField)
                .add(chooseCustomerButton)
                .addLabel("Vehicle:")
                .add(vehicleTextField)
                .add(chooseVehicleButton)
                // Invoice details
                .addLabel("Base Cost:")
                .add(baseCostTextField)
                .addEmptyLabel()
                .addLabel("Amount Paid:")
                .add(amountPaidTextField)
                .addEmptyLabel()
                .addLabel("Amount Due:")
                .add(amountDueTextField)
                .addEmptyLabel()
                .addLabel("Payment Complete?")
                .add(settledTextField)
                .addEmptyLabel()
                .addLabel("Vehicle Warranty Active?")
                .add(warrantyActiveTextField)
                .addEmptyLabel()
                // Buttons for further details
                .add(editPartsButton);
        if (!spcBookingExists) {
            builder.add(delegateToSpcButton)
                    .addEmptyLabel()
                    .addEmptyLabel();
        }
        builder.add(okButton).add(cancelButton);
        JPanel panel = builder.getPanel();

        // Set component states
        TextFieldHelper.enableDefaultValue(baseCostTextField, "0");
        TextFieldHelper.enableDefaultValue(amountPaidTextField, "0");
        TextFieldHelper.enableDefaultValue(vehicleMileageTextField, "0");
        TextFieldHelper.enableDefaultValue(mechanicTimeSpentTextField, "0");
        customerTextField.setEditable(false);
        vehicleTextField.setEditable(false);
        mechanicTextField.setEditable(false);
        amountDueTextField.setEditable(false);
        settledTextField.setEditable(false);
        warrantyActiveTextField.setEditable(false);
        vehicleMileageTextField.setEnabled(false);

        // Tailor text field possible inputs
        TextFieldHelper.disallowNonDigitInput(vehicleMileageTextField);
        TextFieldHelper.disallowNonDateInput(mechanicTimeSpentTextField);
        TextFieldHelper.disallowNonNumericInput(amountPaidTextField);
        TextFieldHelper.disallowNonNumericInput(baseCostTextField);

        // amount settled updating
        amountPaidTextField.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void onChange(DocumentEvent e) {
                if (amountPaidTextField.getText().isEmpty())
                    return;
                try {
                    amtPaid = MathHelper.round(Double.parseDouble(amountPaidTextField.getText()));
                    settledTextField.setText(amtPaid >= bookingAmtDue() ? "Yes" : "No");
                    updateBookingAmtDue();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid amount paid typed, try typing a numeric value with potentially a decimal point, e.g. 10 or 42.50");
                }
            }
        });
        baseCostTextField.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void onChange(DocumentEvent e) {
                if (baseCostTextField.getText().isEmpty())
                    return;
                try {
                    baseCost = MathHelper.round(Double.parseDouble(baseCostTextField.getText()));
                    updateBookingAmtDue();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid base cost typed, try typing a numeric value with potentially a decimal point, e.g. 10 or 42.50");
                }
            }
        });

        // Mechanic hrs spent updating
        mechanicTimeSpentTextField.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void onChange(DocumentEvent e) {
                try {
                    if (!DateHelper.validHourFormat(mechanicTimeSpentTextField.getText())) {
                        mechanicTimeSpent = DateHelper.toCalendarHour("00:00:00");
                    } else {
                        mechanicTimeSpent = DateHelper.toCalendarHour(mechanicTimeSpentTextField.getText());
                    }
                    updateBookingAmtDue();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid mechanic hrs spent, try typing a numeric value with potentially a decimal point, e.g. 1.2 or 3");
                }
            }
        });

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
                ChooseCustomerPrompt ccp = new ChooseCustomerPrompt();
                ccp.show();

                if (ccp.isSuccessful()) {
                    customer = ccp.getSelectedCustomer();
                    customerTextField.setText(customer.getFullName());

                    chooseVehicleButton.setEnabled(true);
                    vehicle = null;
                    vehicleTextField.setText("");
                    updateBookingAmtDue();
                }
            }
        });
        chooseVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseVehiclePrompt cvp = new ChooseVehiclePrompt(customer.getId());
                cvp.show();

                if (cvp.isSuccessful()) {
                    vehicle = cvp.getSelectedVehicle();
                    vehicleTextField.setText(vehicle.getRegistrationNo());
                    updateBookingAmtDue();
                }
            }
        });
        chooseMechanicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseMechanicPrompt cmp = new ChooseMechanicPrompt();
                cmp.show();

                if (cmp.isSuccessful()) {
                    mechanic = cmp.getSelectedMechanic();
                    mechanicTextField.setText(mechanic.getFullName());
                    updateBookingAmtDue();
                }
            }
        });
        editPartsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateValues())
                    return;

                // Attempt to edit booking in db
                attemptEditInDatabase();

                // Close window
                SwingUtils.closeWindowAncestorOf(editPartsButton);

                // Show parts window
                try {
                    ManageInstallParts installParts = new ManageInstallParts();
                    // TODO implement some kind of search?
                    installParts.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error viewing parts.");
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateValues())
                    return;

                // Attempt to add booking to db
                attemptEditInDatabase();

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

        // Delegate to spc button
        if (!spcBookingExists) {
            delegateToSpcButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!validateValues())
                        return;

                    // Attempt to edit booking in db
                    attemptEditInDatabase();

                    // Close window
                    SwingUtils.closeWindowAncestorOf(delegateToSpcButton);

                    // Show spc booking prompt
                    if (successful) {
                        AddSPCBookingPrompt prompt = new AddSPCBookingPrompt(booking);
                        prompt.show();
                    }
                }
            });
        }

        // Set default values
        initValues();

        // Show prompt
        JOptionPane.showOptionDialog(null, panel, "Edit Booking", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, new Object[] {}, null);
    }

    private void attemptEditInDatabase() {
        try {
            // Edit booking in database
            Booking newBooking = parseBooking();
            Database.getInstance().editBooking(newBooking);

            // Delete spc booking if necessary
            if (spcBookingExists
                    && booking instanceof DiagnosisRepairBooking && newBooking instanceof ScheduledMaintenanceBooking) {
                logger.debug("Removing spc booking");

                try {
                    Database.getInstance().removeSPCBookingForBooking(this.booking);
                } catch (ParseException e) {
                    logger.debug("Error removing spc booking");
                }
            }
            successful = true;
            logger.debug("Successfully edited booking in database.");

            JOptionPane.showMessageDialog(null, "You have successfully edited the booking.\nIt may not show in the"
                            + " table if a search filter is active, until you clear it.", "Edit Booking",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            logger.debug("Stack Trace from attemptEditInDatabase");

            for (StackTraceElement ele : ex.getStackTrace()) {
                logger.debug(ele.toString());
            }
            SwingUtils.showDatabaseErrorDialog("Edit Booking", ex);
            successful = false;
            logger.info("Failed to edit booking in database.");
        }
    }

    private void updateBookingAmtDue() {
        double amtDue = totalAmtDue();
        amountDueTextField.setText(amtDue + "");
        warrantyActiveTextField.setText(vehicleUnderWarranty() ? "Yes" : "No");
        settledTextField.setText((amtPaid >= amtDue || vehicleUnderWarranty()) ? "Yes" : "No");
    }

    private double totalAmtDue() {
        return MathHelper.round(bookingAmtDue() + this.booking.getInvoice().getSpcAmountDue());
    }

    private boolean vehicleUnderWarranty() {
        return pastBooking ? booking.getInvoice().isVehicleWarrantyActive()
                :  vehicle != null && vehicle.isWarrantyActive() && vehicle.getWarranty().getExpiryDate().compareTo(endDate) >= 0;
    }

    private double bookingAmtDue() {
        double k = baseCost + partsCost
                + (mechanic != null ? (DateHelper.toFractionalHours(mechanicTimeSpent) * mechanic.getHourlyWage()) : 0);
        return MathHelper.round(k);
    }

    private double bookingTotalCost() {
        double k = baseCost + rawPartsCost
                + (mechanic != null ? (DateHelper.toFractionalHours(mechanicTimeSpent) * mechanic.getHourlyWage()) : 0);
        return MathHelper.round(k);
    }

    private Booking parseBooking() {
        Booking booking;

        if (typeComboBox.getSelectedItem().equals("Diagnosis & Repair")) {
            booking = new DiagnosisRepairBooking(this.booking.getId(), startDate, endDate); // persist id
            ((DiagnosisRepairBooking)booking).setVehicleMileage(vehicleMileage);
        } else {
            booking = new ScheduledMaintenanceBooking(this.booking.getId(), startDate, endDate); // persist id
        }
        booking.setMechanic(mechanic);
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setMechanicTimeSpent(DateHelper.toCalendarHour(mechanicTimeSpentTextField.getText()));

        // Set invoice
        Invoice invoice = new Invoice();
        booking.setInvoice(invoice);

        // Persist id/spc amount/total
        invoice.setId(this.booking.getInvoice().getId());
        invoice.setSpcAmountDue(this.booking.getInvoice().getSpcAmountDue());
        invoice.setSpcTotalCost(this.booking.getInvoice().getSpcTotalCost());

        // Rest of invoice stuff
        invoice.setBookingBaseCost(MathHelper.round(baseCost));
        invoice.setBookingTotalCost(bookingTotalCost());
        invoice.setBookingAmountDue(bookingAmtDue());
        invoice.setAmtPaid(MathHelper.round(amtPaid));
        invoice.setSettled(invoice.getTotalAmountDue() == MathHelper.round(amtPaid));

        // Check vehicle warranty status
        if (vehicle.isWarrantyActive() && vehicle.getWarranty().getExpiryDate().compareTo(endDate) >= 0) {
            logger.debug("Vehicle warranty is active for this booking.");
            JOptionPane.showMessageDialog(null, "The vehicle was under warranty, so this booking is considered settled.",
                    "Edit Booking", JOptionPane.INFORMATION_MESSAGE);
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
            showErrorDialog("Invalid mechanic hrs spent, enter a time, e.g. 01:00:00 for 1hour or 00:30:00 for 30 minutes.");
            return false;
        }

        // Check vehicle mileage format
        if (typeComboBox.getSelectedItem().equals("Diagnosis & Repair")) {
            try {
                vehicleMileage = Integer.parseInt(vehicleMileageTextField.getText());

                if (vehicleMileage > 1_000_000) {
                    showErrorDialog("Invalid vehicle mileage value, it cannot be more than 1 million.");
                    return false;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid vehicle mileage value format, enter a whole number, e.g. 35000, 100, 10, 0.");
                return false;
            }
        }

        // Validate base cost format
        try {
            baseCost = MathHelper.round(Double.parseDouble(baseCostTextField.getText()));

            if (baseCost > 100_000) {
                showErrorDialog("Invalid base cost value, it cannot be more than £100,000.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid base cost value format, enter a fractional or whole number, e.g. 30.0 or 30");
            return false;
        }

        // Validate amount paid
        try {
            amtPaid = MathHelper.round(Double.parseDouble(amountPaidTextField.getText()));

            if (amtPaid > totalAmtDue()) {
                showErrorDialog("Invalid amount paid, a customer cannot pay more than they owe for a booking.");
                return false;
            } else if (amtPaid > 0 && vehicleUnderWarranty()) {
                showErrorDialog("Invalid amount paid, a customer cannot pay for a booking when the vehicle is under warranty.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid amount paid value format, enter a fractional or whole number, e.g. 30.0 or 30");
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
            DateHelper.validateBookingDatesForUpdate(booking.getId(), startDate, endDate);
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

    private void initValues() {
        logger.debug("Loading booking details.");
        typeComboBox.setSelectedIndex(booking instanceof DiagnosisRepairBooking ? 0 : 1);
        vehicleMileageTextField.setText(booking instanceof DiagnosisRepairBooking
                ? ((DiagnosisRepairBooking)booking).getVehicleMileage() + ""
                : "0");
        startDateTextField.setText(DateHelper.toString(booking.getBookingStartDate()));
        endDateTextField.setText(DateHelper.toString(booking.getBookingEndDate()));
        endDate = booking.getBookingEndDate();
        mechanicTextField.setText(booking.getMechanic().getFullName());
        customerTextField.setText(booking.getCustomer().getFullName());
        vehicleTextField.setText(booking.getVehicle().getRegistrationNo());
        mechanicTimeSpentTextField.setText(DateHelper.toString(booking.getMechanicTimeSpent()).split(" ")[1] + "");
        customer = booking.getCustomer();
        mechanic = booking.getMechanic();
        vehicle = booking.getVehicle();

        // Set invoice stuff
        baseCostTextField.setText(booking.getInvoice().getBookingBaseCost() + "");
        amtPaid = booking.getInvoice().getAmtPaid();
        amountPaidTextField.setText(amtPaid + "");

        try {
            partsCost = Database.getInstance().getTotalCostWithWarranty(booking.getId());
            rawPartsCost = Database.getInstance().getTotalCostWithoutWarranty(booking.getId());
        } catch (SQLException e) {
            logger.debug("Error loading parts costs.");
        }
        updateBookingAmtDue();

        // Disable editing of non-invoice fields if the booking has past
        if (Calendar.getInstance().compareTo(booking.getBookingEndDate()) > 0) {
            logger.debug("Past booking detected.");
            pastBooking = true;
            editPartsButton.setText("View Parts");
            typeComboBox.setEnabled(false);
            chooseMechanicButton.setEnabled(false);
            startDateTextField.setEditable(false);
            endDateTextField.setEditable(false);
            chooseBookingStartDateButton.setEnabled(false);
            chooseBookingEndDateButton.setEnabled(false);
            chooseVehicleButton.setEnabled(false);
            chooseCustomerButton.setEnabled(false);
            mechanicTimeSpentTextField.setEditable(false);
        }
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Edit Booking - Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
