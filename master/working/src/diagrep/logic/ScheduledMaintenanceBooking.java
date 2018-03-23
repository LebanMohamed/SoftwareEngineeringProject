package diagrep.logic;

import java.util.Calendar;

public final class ScheduledMaintenanceBooking extends Booking {

    public ScheduledMaintenanceBooking(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate) {
        super(bookingId, bookingStartDate, bookingEndDate);
    }
}
