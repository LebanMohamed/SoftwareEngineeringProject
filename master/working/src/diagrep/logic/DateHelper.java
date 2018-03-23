package diagrep.logic;

import common.Database;
import common.util.InvalidDateException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * A set of misc. helper methods related to {@link Date}.
 */
public class DateHelper {

    // XXX refactor month info into an enum?
    public static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"
    };
    public static final String[] DAY_NAMES = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    /**
     * The amount of days in each month, this does not take into account leap years.
     *
     * @see {@link DateHelper#daysInMonth(int, int)}
     */
    private static final int[] MONTH_DAYS = {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
    private static final Pattern HOUR_PATTERN = Pattern.compile("^[\\d]{2}:[\\d]{2}:[\\d]{2}$");

    /**
     * Checks if the given date is a public holiday. This only takes into account the 8 consistent holidays
     * observed in England and Wales.
     * Source: https://en.wikipedia.org/wiki/Public_holidays_in_the_United_Kingdom
     * Source: https://stackoverflow.com/questions/2510383/how-can-i-calculate-what-date-good-friday-falls-on-given-a-year
     */
    public static boolean isPublicHoliday(Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int yr = date.get(Calendar.YEAR);

        // No public holidays on feb,jun,jul,sept,oct,nov ever
        if (month == 1 || month == 5 || month == 6 || month == 8 || month == 9 || month == 10)
            return false;

        // New Year's Day
        if (month == Calendar.JANUARY) {
            Calendar c = Calendar.getInstance();
            c.set(yr, Calendar.JANUARY, 1);

            if (c.get(Calendar.DAY_OF_MONTH) == Calendar.SUNDAY && day == 2) {
                return true;
            } else if (c.get(Calendar.DAY_OF_MONTH) == Calendar.SATURDAY && day == 3) {
                return true;
            } else if (day == 1) {
                return true;
            }
        }

        // Easter date for this year
        Calendar easterDate = easterDate(yr);

        // Easter monday
        int easterMondayMonth = easterDate.get(Calendar.MONTH);
        int easterMondayDay = easterDate.get(Calendar.DAY_OF_MONTH) + 1;

        if (easterMondayDay > MONTH_DAYS[easterMondayMonth]) {
            easterMondayMonth++;
            easterMondayDay = 1;
        }

        if (month == easterMondayMonth && day == easterMondayDay) {
            return true;
        }

        // Good friday
        int easterFridayMonth = easterDate.get(Calendar.MONTH);
        int easterFridayDay = easterDate.get(Calendar.DAY_OF_MONTH) - 2;

        if (easterFridayDay == 0) {
            easterFridayMonth--;
            easterFridayDay = MONTH_DAYS[easterFridayMonth];
        } else if (easterFridayDay == -1) {
            easterFridayMonth--;
            easterFridayDay = MONTH_DAYS[easterFridayMonth] - 1;
        }

        if (month == easterFridayMonth && day == easterFridayDay) {
            return true;
        }

        // May holidays
        if (month == Calendar.MAY) {
            // First Monday
            Calendar c = Calendar.getInstance();
            c.set(yr, Calendar.MAY, 1);

            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (c.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                return true;
            }

            // Last Monday
            c = Calendar.getInstance();
            c.set(yr, Calendar.MAY, MONTH_DAYS[Calendar.MAY]);

            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }

            if (c.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                return true;
            }
        }

        // August holidays (last monday)
        if (month == Calendar.AUGUST) {
            Calendar c = Calendar.getInstance();
            c.set(yr, Calendar.AUGUST, MONTH_DAYS[Calendar.AUGUST]);

            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }

            if (c.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                return true;
            }
        }

        // Christmas / Boxing Day
        if (month == Calendar.DECEMBER) {
            // Christmas
            Calendar c = Calendar.getInstance();
            c.set(yr, Calendar.DECEMBER, 25);
            boolean onWeekend = onWeekend(c);

            if (onWeekend && (day == 27 || day == 28)) {
                return true; // substitute day
            } else if (!onWeekend && day == 25) {
                return true;
            }

            // Boxing Day
            c = Calendar.getInstance();
            c.set(yr, Calendar.DECEMBER, 26);
            onWeekend = onWeekend(c);

            if (!onWeekend && day == 26) {
                return true;
            } else if (onWeekend && day == 28) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the date of easter for the given year.
     * Source: http://aa.usno.navy.mil/faq/docs/easter.php
     */
    public static Calendar easterDate(int year) {
        // Calculate date
        int c = year / 100;
        int n = year - 19 * (year / 19);
        int k = (c - 17) / 25;
        int i = c - c / 4 - (c - k) / 3 + 19 * n + 15;
        i = i - 30 * (i / 30);
        i = i - (i / 28) * (1 - (i / 28) * (29 / (i + 1)) * ((21 - n) / 11));
        int j = year + year / 4 + i + 2 - c + c / 4;
        j = j - 7 * (j / 7);
        int l = i - j;
        int m = 3 + (l + 40) / 44;
        int d = l + 28 - 31 * (m / 4);

        // Create and return calendar
        Calendar date = Calendar.getInstance();
        date.set(year, m - 1, d, 0, 0, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    public static boolean isWithinOpeningHours(Calendar c) {
        int day = c.get(Calendar.DAY_OF_WEEK);
        int hrs = c.get(Calendar.HOUR_OF_DAY); // Source: https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html#HOUR_OF_DAY
        int mins = c.get(Calendar.MINUTE);
        int secs = c.get(Calendar.SECOND);

        boolean isSunday = day == Calendar.SUNDAY;
        boolean openOnWeekday = day >= Calendar.MONDAY && day <= Calendar.FRIDAY
                && (hrs >= 9 && hrs <= 17) // 9am-5.30pm
                && (hrs != 17 || mins <= 30) // ensure strict ending at 5.30pm
                && (hrs != 17 && mins != 30 || secs == 0); // ensure strict ending at 0s
        boolean openOnWeekend = day == Calendar.SATURDAY
                && (hrs >= 9 && hrs <= 12) // 9pm-12 noon
                && (hrs != 12 || mins == 0 && secs == 0); // ensure strict ending at 12 noon
        return !isSunday && (openOnWeekday || openOnWeekend);
    }

    /**
     * @return If the given dates are on the same day, in the same month of the same year.
     */
    private static boolean onSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    private static boolean overlapsWithOtherBookings(Calendar bookingStartDate, Calendar bookingEndDate)
            throws SQLException {
        return Database.getInstance().overlapsWithOtherBooking(bookingStartDate, bookingEndDate);
    }

    private static boolean overlapsWithOtherBookings(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate)
            throws SQLException {
        return Database.getInstance().overlapsWithOtherBooking(bookingId, bookingStartDate, bookingEndDate);
    }

    /**
     * Checks if the given booking dates are valid, with respect to public holidays and garage opening/closing times.
     * If it returns no exceptions, then the date pair is valid.
     */
    public static void validateBookingDates(Calendar bookingStartDate, Calendar bookingEndDate)
            throws SQLException, InvalidDateException {
        validateBookingDates(-1, bookingStartDate, bookingEndDate);
    }

    /**
     * Checks if the given booking dates are valid, with respect to public holidays and garage opening/closing times.
     * If it returns no exceptions, then the date pair is valid. If a booking id is provided and non-negative, that
     * booking's dates are ignored for the overlapping booking date check.
     * This does not check if the booking date is in the past.
     */
    public static void validateBookingDatesForUpdate(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate)
            throws SQLException, InvalidDateException {
        if (!onSameDay(bookingStartDate, bookingEndDate))
            throw new InvalidDateException("Your booking start and end date must be on the same day.");
        if (!isWithinOpeningHours(bookingStartDate))
            throw new InvalidDateException("Your booking start date is not within opening hours.\n"
                    + "Refer to the user manual for the opening hours.");
        if (!isWithinOpeningHours(bookingEndDate))
            throw new InvalidDateException("Your booking end date is not within opening hours.\n"
                    + "Refer to the user manual for the opening hours.");
        if (isPublicHoliday(bookingStartDate))
            throw new InvalidDateException("Your booking date must not be on a public holiday.");
        if (bookingStartDate.compareTo(bookingEndDate) > 0)
            throw new InvalidDateException("The booking start date is after the end date.");

        // Check overlapping
        if (bookingId != -1) {
            if (overlapsWithOtherBookings(bookingId, bookingStartDate, bookingEndDate))
                throw new InvalidDateException("Your booking dates must not overlap with another.");
        } else {
            if (overlapsWithOtherBookings(bookingStartDate, bookingEndDate))
                throw new InvalidDateException("Your booking dates must not overlap with another.");
        }
    }

    /**
     * Checks if the given booking dates are valid, with respect to public holidays and garage opening/closing times.
     * If it returns no exceptions, then the date pair is valid. If a booking id is provided and non-negative, that
     * booking's dates are ignored for the overlapping booking date check.
     */
    private static void validateBookingDates(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate)
            throws SQLException, InvalidDateException {
        if (Calendar.getInstance().compareTo(bookingStartDate) > 0)
            throw new InvalidDateException("The booking starting date has already passed.");

        // Check other date constraints
        validateBookingDatesForUpdate(bookingId, bookingStartDate, bookingEndDate);
    }

    /**
     * Returns the number of days in the given month.
     */
    public static int daysInMonth(int month, int year) {
        if (month < 0 || month > 11)
            throw new IllegalArgumentException("month is invalid - must be between 0 and 11 (inclusive)");
        if (month == 1 && year % 4 == 0) // leap year, exceptional case
            return 29;
        return MONTH_DAYS[month];
    }

    /**
     * This will convert a {@link Calendar} instance to a format 'YYYY-MM-DD HH:MM:SS'.
     */
    public static String toString(Calendar c) {
        int yr = c.get(Calendar.YEAR);

        if (yr == 1) { // hour only object
            return String.format("%s %02d:%02d:%02d", "0000-00-00", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                    c.get(Calendar.SECOND));
        } else {
            return String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                    c.get(Calendar.SECOND));
        }
    }

    /**
     * This will convert a quantity in the format: 'HH:MM:SS' to its equivalent {@link Calendar}
     * representation.
     * Use this with {@link #toString(Calendar)} for optimal results.
     */
    public static Calendar toCalendarHour(String s) throws IllegalArgumentException {
        // Test string format
        if (!validHourFormat(s)) {
            throw new IllegalArgumentException("Invalid date format specified.");
        }

        // Parse date
        String[] time = s.split(":");

        Calendar c = Calendar.getInstance();
        c.set(0, 0, 1, Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
        c.set(Calendar.MILLISECOND, 0); // XXX this is not always 0, so two calendars with varying millis would not be equal
        return c;
    }

    /**
     * This will convert a quantity in the format: 'YYYY-MM-DD HH:MM:SS' to its equivalent {@link Calendar}
     * representation.
     */
    public static Calendar toCalendar(String s) throws IllegalArgumentException {
        // Test string format
        if (!DATE_TIME_PATTERN.matcher(s).matches()) {
            throw new IllegalArgumentException("Invalid date format specified.");
        }

        // Parse date
        String[] s1 = s.split(" ");
        String[] date = s1[0].split("-");
        String[] time = s1[1].split(":");

        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]),
                Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
        c.set(Calendar.MILLISECOND, 0); // XXX this is not always 0, so two calendars with varying millis would not be equal
        return c;
    }

    public static boolean validHourFormat(String s) {
        return HOUR_PATTERN.matcher(s).matches();
    }

    public static int seconds(Calendar time) {
        int hrs = time.get(Calendar.HOUR_OF_DAY);
        int mins = time.get(Calendar.MINUTE);
        int sec = time.get(Calendar.SECOND);
        return hrs * 60 * 60 + mins * 60 + sec;
    }

    private static boolean onWeekend(Calendar c) {
        return c.get(Calendar.DAY_OF_MONTH) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static double toFractionalHours(Calendar time) {
        double hrs = time.get(Calendar.HOUR_OF_DAY);
        double mins = time.get(Calendar.MINUTE);
        double sec = time.get(Calendar.SECOND);
        return hrs + (mins / 60) + (sec / 60);
    }
}
