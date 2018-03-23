package diagrep.logic;

import common.Database;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * A booking filter.
 */
public final class BookingFilter {

    /**
     * A {@link BookingFilter} that filters nothing.
     */
    public static final BookingFilter NO_FILTER = new BookingFilter(Type.ALL);
    /**
     * A {@link BookingFilter} that filters to find future bookings.
     */
    public static final BookingFilter FUTURE_BOOKINGS_FILTER = new BookingFilter(Type.FUTURE);
    /**
     * A {@link BookingFilter} that filters to find past bookings.
     */
    public static final BookingFilter PAST_BOOKINGS_FILTER = new BookingFilter(Type.PAST);
    /**
     * A {@link BookingFilter} that filters to find the soonest booking per vehicle.
     */
    public static final BookingFilter NEXT_BOOKING_PER_VEHICLE_FILTER = new BookingFilter(Type.NEXT_PER_VEHICLE);
    private final Type type;
    private final String value;
    private final boolean partialMatch;

    private BookingFilter(Type type) {
        this(type, null, false);
    }

    public BookingFilter(Type type, String value) {
        this(type, value, false);
    }

    public BookingFilter(Type type, String value, boolean partialMatch) {
        this.type = type;
        this.value = value;
        this.partialMatch = partialMatch;
    }

    public List<Booking> results() throws SQLException, ParseException {
        switch (type) {
            case ALL:
                return Database.getInstance().getAllBookings();
            case VEHICLE_REG_NUM:
                return Database.getInstance().getAllBookingsForVehicleRegNum(value, partialMatch);
            case VEHICLE_MAKE:
                return Database.getInstance().getAllBookingsForVehicleMake(value, partialMatch);
            case CUSTOMER_FULL_NAME:
                return Database.getInstance().getAllBookingsForCustomer(value, partialMatch);
            case CUSTOMER_FIRST_NAME:
                return Database.getInstance().getAllBookingsForCustomerFirstName(value, partialMatch);
            case CUSTOMER_SURNAME:
                return Database.getInstance().getAllBookingsForCustomerSurname(value, partialMatch);
            case VEHICLE_TEMPLATE:
                int id = Integer.parseInt(value.split(" ")[0]);
                return Database.getInstance().getAllBookingsForVehicleTemplate(id);
            case DATE:
                return Database.getInstance().getAllBookingsForBookingDate(value.replaceAll("\\?", "_"));
            case FUTURE:
                return Database.getInstance().getAllFutureBookings();
            case PAST:
                return Database.getInstance().getAllPastBookings();
            case NEXT_PER_VEHICLE:
                return Database.getInstance().getAllNextBookingsPerVehicle();
            case WITH_NO_SPC:
                return Database.getInstance().getAllDiagRepBookingsWithNoSPCBooking();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * The field to apply the {@link BookingFilter} on.
     */
    public enum Type {
        ALL,
        FUTURE,
        PAST,
        NEXT_PER_VEHICLE,
        VEHICLE_REG_NUM,
        VEHICLE_MAKE,
        VEHICLE_TEMPLATE,
        CUSTOMER_FULL_NAME,
        CUSTOMER_FIRST_NAME,
        CUSTOMER_SURNAME,
        DATE,
        WITH_NO_SPC
    }
}
