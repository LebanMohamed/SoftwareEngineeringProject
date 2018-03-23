package diagrep.gui.date;

import common.gui.util.GenericPrompt;
import diagrep.logic.DateHelper;

import javax.swing.*;
import java.util.Calendar;

public abstract class GenericDatePrompt extends GenericPrompt {

    protected final JComboBox<String> yearComboBox = new JComboBox<>();
    protected final JComboBox<String> monthComboBox = new JComboBox<>();
    protected final JComboBox<String> dayComboBox = new JComboBox<>();
    final JComboBox<String> hoursComboBox = new JComboBox<>();
    final JComboBox<String> minutesComboBox = new JComboBox<>();
//    private final JComboBox<String> secondsComboBox = new JComboBox<>();
    /**
     * If the JComboBox values are finished being initialized and further changes should be processed with day JComboBox
     * modifying behaviour.
     */
    protected boolean ready = false;
    private boolean omitSundays = false;

    protected void initComboBoxValues() {
        // Add values
        for (int year = 2010; year <= 2018; year++) { // XXX no records for public holidays from 2018 onwards
            yearComboBox.addItem(year + "");
        }

        for (String month : DateHelper.MONTH_NAMES) {
            monthComboBox.addItem(month);
        }

        for (int i = 9; i <= 18; i++) {
            hoursComboBox.addItem(String.format("%02d:00", i));
        }

        for (int i = 0; i < 60; i++) {
//            secondsComboBox.addItem(String.format("%02d", i));
            minutesComboBox.addItem(String.format("%02d", i));
        }

        // Set defaults
        Calendar now = Calendar.getInstance();
        yearComboBox.setSelectedItem(now.get(Calendar.YEAR) + "");
        monthComboBox.setSelectedItem(DateHelper.MONTH_NAMES[now.get(Calendar.MONTH)]);
        String hoursTxt = String.format("%02d:00", now.get(Calendar.HOUR_OF_DAY));
        hoursComboBox.setSelectedItem(hoursTxt);

        if (hoursComboBox.getSelectedItem().equals(hoursTxt)) // only set minute to current one, if hr was valid
            minutesComboBox.setSelectedItem(now.get(Calendar.MINUTE) + "");
        else
            minutesComboBox.setSelectedItem("00");
//        secondsComboBox.setSelectedItem(now.get(Calendar.SECOND) + "");

        initDayComboBoxValues(); // after year and month is set
        dayComboBox.setSelectedItem(now.get(Calendar.DAY_OF_MONTH)
                + " " + DateHelper.DAY_NAMES[now.get(Calendar.DAY_OF_WEEK) - 1]);
        ready = true;
    }

    protected void initDayComboBoxValues() {
        int monthNo = getSelectedMonthNo();
        int yearNo = getSelectedYearNo();
        int daysInMonth = DateHelper.daysInMonth(monthNo, yearNo);

        // Clear days currently stored
        dayComboBox.removeAllItems();

        // Create corresponding calendar and use this to populate values
        Calendar c = Calendar.getInstance();
        c.set(yearNo, monthNo, 1);

        for (int i = 1; i <= daysInMonth; i++) {
            String dayTxt = String.format("%s %s", c.get(Calendar.DAY_OF_MONTH),
                    DateHelper.DAY_NAMES[c.get(Calendar.DAY_OF_WEEK) - 1]);

            if (!omitSundays || !dayTxt.endsWith("Sunday"))
                dayComboBox.addItem(dayTxt);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    protected int getSelectedMonthNo() {
        String monthName = (String) monthComboBox.getSelectedItem();

        for (int i = 0; i < DateHelper.MONTH_NAMES.length; i++)
            if (DateHelper.MONTH_NAMES[i].equalsIgnoreCase(monthName))
                return i;
        return -1;
    }

//    private int getSelectedSecondsNo() {
//        return getComboBoxIntValue(secondsComboBox);
//    }

    protected int getSelectedMinuteNo() {
        return getComboBoxIntValue(minutesComboBox);
    }

    protected int getSelectedHoursNo() {
        String hourNo = ((String) hoursComboBox.getSelectedItem()).split(":")[0];
        return Integer.parseInt(hourNo);
    }

    protected int getSelectedYearNo() {
        return getComboBoxIntValue(yearComboBox);
    }

    protected int getSelectedDayNo() {
        String dayNo = ((String) dayComboBox.getSelectedItem()).split(" ")[0];
        return Integer.parseInt(dayNo);
    }

    private int getComboBoxIntValue(JComboBox comboBox) {
        return Integer.parseInt((String) comboBox.getSelectedItem());
    }

    /**
     * If sundays should be shown in the days combo box.
     */
    public void setOmitSundays(boolean omitSundays) {
        this.omitSundays = omitSundays;
    }
}
