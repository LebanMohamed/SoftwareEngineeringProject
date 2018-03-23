


/**
 *
 * @author ec  Nikita Mirolyubov SOURCE OF SAMPLE CODE: DIAGREP.DATEHELPER
 */
package specialist.logic;
import common.Database;
import common.util.InvalidDateException;
import diagrep.logic.DateHelper;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * A set of misc. helper methods related to {@link Date}.
 */
public final class DateHelperSPC extends DateHelper {

  

    public static boolean isWithinOpeningHours(Calendar c) {
        int day = c.get(Calendar.DAY_OF_WEEK);

        boolean isSunday = day == Calendar.SUNDAY;
        boolean openOnWeekday = day >= Calendar.MONDAY && day <= Calendar.FRIDAY;
        boolean openOnWeekend = day == Calendar.SATURDAY;
        return !isSunday && (openOnWeekday || openOnWeekend);
    }

    /**
     * Checks if the given booking dates are valid, with respect to public
     * holidays and garage opening/closing times. If it returns no exceptions,
     * then the date pair is valid.
     */
    /**
     * Checks if the given booking dates are valid, with respect to public
     * holidays and garage opening/closing times. If it returns no exceptions,
     * then the date pair is valid. If there is an overlapping with other bookings of any kind, 
     * which are made for the same vehicle,exception is thrown.
     */
    public static void validateBookingDates(Calendar bookingStartDate, Calendar bookingEndDate, String regNo)
            throws InvalidDateException, SQLException {
        if (isPublicHoliday(bookingStartDate)) {
            throw new InvalidDateException("Your booking date must not be on a public holiday.");
        }
        if (Calendar.getInstance().compareTo(bookingStartDate) > 0) {
            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == bookingStartDate.get(Calendar.DAY_OF_MONTH)) {
                throw new InvalidDateException("The garage is already closed, you can not set booking start date as today.");
            } else {
                throw new InvalidDateException("The booking start date has already passed .");
            }
        }
        if (!isWithinOpeningHours(bookingStartDate)) {
            throw new InvalidDateException("Your booking start date is not within opening hours.\n"
                    + "Refer to the user manual for the opening hours.");
        }
        if (!isWithinOpeningHours(bookingEndDate)) {
            throw new InvalidDateException("Your booking end date is not within opening hours.\n"
                    + "Refer to the user manual for the opening hours.");
        }
        if (bookingStartDate.compareTo(bookingEndDate) > 0) {
            throw new InvalidDateException("The booking start date can not be after the end date .");
        }
        if(regNo!=null){
        if (overlapsWithOtherBookings(bookingStartDate, bookingEndDate, regNo))
                throw new InvalidDateException("Your SPC booking dates must not overlap with the corresponding Diagnostic&Repair/Scheduled Maintenance.");
        if (overlapsWithOtherSPCBookings(bookingStartDate, bookingEndDate, regNo))
                throw new InvalidDateException("Your SPC booking dates must not overlap with other SPC bookings for this car.");
        }
        
        
    }

    /**
     * Returns the number of days in the given month.
     */


    public static String toString(Calendar c) {
        return String.format("%02d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar toCalendar(String s) throws IllegalArgumentException {
        try {
            if (s == null || s.isEmpty() || !s.contains("-")) {
                throw new IllegalArgumentException();
            }
            String[] date = s.split("-");
            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                c.set(Calendar.HOUR_OF_DAY, 11);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                c.set(Calendar.MILLISECOND, 0);
            } else if (c.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && c.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY) { //if working day, set the time to the 1s before closing time. this would allow us to reject bookings for today IF the garage is supposed to be closed at the time the booking is made.
                c.set(Calendar.HOUR_OF_DAY, 17);
                c.set(Calendar.MINUTE, 29);
                c.set(Calendar.SECOND, 59);
                c.set(Calendar.MILLISECOND, 0);
            } else { //if sunday, we dont really care, we cant deliver of get returned vehicle
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
            }
            return c;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
     private static boolean overlapsWithOtherBookings(Calendar bookingStartDate, Calendar bookingEndDate, String regNo)
            throws SQLException {
        return Database.getInstance().overlapsWithOtherBookingSPC(bookingStartDate, bookingEndDate, regNo);
    }
      private static boolean overlapsWithOtherSPCBookings(Calendar bookingStartDate, Calendar bookingEndDate, String regNo)
            throws SQLException {
        return Database.getInstance().overlapsWithOtherSPCBookingSPC(bookingStartDate, bookingEndDate, regNo);
    }
}
