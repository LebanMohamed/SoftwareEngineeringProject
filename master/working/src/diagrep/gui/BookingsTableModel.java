package diagrep.gui;

import common.gui.util.DefaultTableModelEx;
import diagrep.logic.Booking;
import diagrep.logic.BookingFilter;
import diagrep.logic.DateHelper;
import diagrep.logic.DiagnosisRepairBooking;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * The table model used in {@link ManageBookingsDialog}.
 */
public class BookingsTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {
            "ID", "Start Date", "End Date", "Type", "Base Cost / £", "Mechanic", "Customer", "Vehicle Reg No",
            "Vehicle Make", "Vehicle Model"
    };
    private static final Class[] COLUMN_TYPES = {
            Integer.class, String.class, String.class, String.class, Double.class, String.class, String.class,
            String.class, String.class, String.class
    };
    private List<Booking> bookings;

    public BookingsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setEditable(false);
    }

    /**
     * Adds a row representing the argument {@link Booking} object. This performs no validation of the object.
     */
    private void addRow(Booking booking) {
        addRow(new Object[] {
                booking.getId(), DateHelper.toString(booking.getBookingStartDate()),
                DateHelper.toString(booking.getBookingEndDate()),
                booking instanceof DiagnosisRepairBooking ? "Diagnosis & Repair" : "Scheduled Maintenance",
                booking.getInvoice().getBookingBaseCost(),
                booking.getMechanic().getFullName(), booking.getCustomer().getFullName(),
                booking.getVehicle().getRegistrationNo(), booking.getVehicle().getTemplate().getMake(),
                booking.getVehicle().getTemplate().getModel()
        });
    }

    public Booking rowToBooking(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int id = (int)getValueAt(rowIdx, 0);
        return bookingForId(id);
    }

    /**
     * Adds bookings to the table from the database, provided by the argument's {@link BookingFilter#results()} call.
     */
    public void loadBookings(BookingFilter filter) {
        try {
            // Load all bookings in the database
            bookings = filter.results();

            // Iterate over the bookings and add them to the table row by row
            for (Booking booking : bookings) {
                addRow(booking);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Booking bookingForId(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getId() == bookingId)
                return booking;
        }
        throw new RuntimeException("Invalid booking id.");
    }

    /**
     * Removes the first row with the given booking id from this model.
     * @return If the row was found and removed.
     */
    boolean removeRowByBookingId(int bookingId) {
        return removeRowByColumnValue(0, bookingId);
    }
}
