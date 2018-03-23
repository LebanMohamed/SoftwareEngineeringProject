package diagrep.gui;

import common.gui.util.GenericPrompt;
import common.gui.util.JPanelBuilder;
import diagrep.gui.choose.ChooseVehicleTemplatePrompt;
import diagrep.gui.date.SearchDatePrompt;
import diagrep.logic.BookingFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JOptionPane wrapper that receives user input to create a {@link BookingFilter} instance from.
 */
final class SearchBookingsPrompt extends GenericPrompt {

    private final JComboBox<String> searchTypeComboBox = new JComboBox<>();
    private final JTextField valueTextField = new JTextField();
    private final JCheckBox partialMatchCheckBox = new JCheckBox();
    private final JButton chooseBookingDateButton = new JButton("Choose");
    private BookingFilter bookingFilter;

    public void show() {
        // Design prompt
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(3, 3, 5, 5))
                .addLabel("Search Type")
                .add(searchTypeComboBox)
                .addEmptyLabel()
                .addLabel("Value")
                .add(valueTextField)
                .add(chooseBookingDateButton)
                .addLabel("Allow Partial Match")
                .add(partialMatchCheckBox)
                .addEmptyLabel()
                .getPanel();

        // Initialise fields
        searchTypeComboBox.addItem("Vehicle Registration Number");
        searchTypeComboBox.addItem("Vehicle Make");
        searchTypeComboBox.addItem("Customer Full Name");
        searchTypeComboBox.addItem("Customer First Name");
        searchTypeComboBox.addItem("Customer Surname");
        searchTypeComboBox.addItem("Booking Date");
        searchTypeComboBox.addItem("Vehicle Template");
        searchTypeComboBox.addItem("All Bookings");
        searchTypeComboBox.addItem("Future Bookings");
        searchTypeComboBox.addItem("Past Bookings");
        searchTypeComboBox.addItem("Next Booking Per Vehicle");
        searchTypeComboBox.setSelectedItem(0);
        searchTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = searchTypeComboBox.getSelectedIndex();
                valueTextField.setEnabled(idx <= 6);
                chooseBookingDateButton.setEnabled(idx == 5 || idx == 6);
                partialMatchCheckBox.setEnabled(idx < 5);
            }
        });
        searchTypeComboBox.setEditable(false);
        chooseBookingDateButton.setEnabled(false);

        // Behaviour for the choose button
        chooseBookingDateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = searchTypeComboBox.getSelectedIndex();

                if (idx == 5) {
                    SearchDatePrompt prompt = new SearchDatePrompt();
                    prompt.show();

                    if (prompt.isSuccessful()) {
                        valueTextField.setText(prompt.parseSearchDate());
                    }
                } else if (idx == 6) {
                    ChooseVehicleTemplatePrompt prompt = new ChooseVehicleTemplatePrompt();
                    prompt.show();

                    if (prompt.isSuccessful()) {
                        valueTextField.setText(prompt.parseSelectedVehicleTemplate().toString());
                    }
                }
            }
        });

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "Search Booking", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String value = valueTextField.getText();
            boolean partialMatch = partialMatchCheckBox.isSelected();

            switch ((String)searchTypeComboBox.getSelectedItem()) {
                case "Vehicle Registration Number":
                    if (value.isEmpty()) {
                        showErrorDialog("Vehicle registration number field cannot be empty.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.VEHICLE_REG_NUM, value, partialMatch);
                    break;
                case "Vehicle Make":
                    if (value.isEmpty()) {
                        showErrorDialog("Vehicle make field cannot be empty.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.VEHICLE_MAKE, value, partialMatch);
                    break;
                case "Customer Full Name":
                    if (value.isEmpty()) {
                        showErrorDialog("Customer full name field cannot be empty.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.CUSTOMER_FULL_NAME, value, partialMatch);
                    break;
                case "Customer First Name":
                    if (value.isEmpty()) {
                        showErrorDialog("Customer first name field cannot be empty.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.CUSTOMER_FIRST_NAME, value, partialMatch);
                    break;
                case "Customer Surname":
                    if (value.isEmpty()) {
                        showErrorDialog("Customer surname field cannot be empty.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.CUSTOMER_SURNAME, value, partialMatch);
                    break;
                case "Booking Date":
                    if (value.isEmpty()) {
                        showErrorDialog("Booking date number field cannot be empty, click the choose button next to it.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.DATE, value);
                    break;
                case "Vehicle Template":
                    if (value.isEmpty()) {
                        showErrorDialog("Vehicle template cannot be empty, click the choose button next to it.");
                        return;
                    }
                    bookingFilter = new BookingFilter(BookingFilter.Type.VEHICLE_TEMPLATE, value);
                    break;
                case "All Bookings":
                    bookingFilter = BookingFilter.NO_FILTER;
                    break;
                case "Future Bookings":
                    bookingFilter = BookingFilter.FUTURE_BOOKINGS_FILTER;
                    break;
                case "Past Bookings":
                    bookingFilter = BookingFilter.PAST_BOOKINGS_FILTER;
                    break;
                case "Next Booking Per Vehicle":
                    bookingFilter = BookingFilter.NEXT_BOOKING_PER_VEHICLE_FILTER;
                    break;
                default:
                    showErrorDialog("Unknown filter selected.");
                    break;
            }

            // Fall-back
            successful = true;
        }
    }

    BookingFilter parseBookingFilter() {
        if (!successful)
            throw new IllegalStateException();
        return bookingFilter;
    }

    public boolean isSuccessful() {
        return successful;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Search Booking - Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
