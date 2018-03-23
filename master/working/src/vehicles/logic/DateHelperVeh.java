package vehicles.logic;

import java.util.Calendar;

/**
 *
 * @author Akhil
 */
public class DateHelperVeh extends diagrep.logic.DateHelper {

    public static String toString(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }
}
