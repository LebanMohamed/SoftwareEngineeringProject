package common.gui.date;

import common.gui.util.GenericPrompt;
import common.gui.util.JPanelBuilder;
import diagrep.logic.DateHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * A {@link JOptionPane#showConfirmDialog(Component, Object, String, int, int)} call wrapper that receives user input
 * to create a {@link Calendar} from.
 */
public final class SelectDatePrompt extends GenericPrompt {

    private static final String WINDOW_TITLE = "GMSIS / Select Date...";
    private final JComboBox<String> yearComboBox = new JComboBox<>();
    private final JComboBox<String> monthComboBox = new JComboBox<>();
    private final JComboBox<String> dayComboBox = new JComboBox<>();
    private final JComboBox<String> hoursComboBox = new JComboBox<>();
    private final JComboBox<String> minutesComboBox = new JComboBox<>();
    private final JComboBox<String> secondsComboBox = new JComboBox<>();
    private final Config config;
    /**
     * If the JComboBox values are finished being initialized and further changes should be processed with day JComboBox
     * modifying behaviour.
     */
    private boolean ready = false;
    private JPanel panel;

    public SelectDatePrompt(Config config) {
        this.config = config;
        init();
    }

    /**
     * This will set behaviour for the provided arguments to make them behaviour as choose date components.
     */
    public static void setAsChooseDateComponents(JButton btn, final JTextField tf, final boolean omitSundays) {
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Try to get date from tf, if possible
                Calendar cal = null;

                try {
                    cal = DateHelper.toCalendar(tf.getText());
                } catch(IllegalArgumentException ex) {
                }

                // Create prompt
                SelectDatePrompt prompt = new SelectDatePrompt(
                        new Config(Config.SHOW_HOURS_MINUTES_MODE, !omitSundays, cal == null, 2000, 2030, 9, 18)
                );

                if (cal != null) {
                    prompt.setDate(cal);
                }

                // Show prompt and handle response
                prompt.show();

                if (prompt.isSuccessful()) {
                    tf.setText(DateHelper.toString(prompt.parseDate()));
                }
            }
        });
    }

    private void initComboBoxValues() {
        // Add values to combo boxes
        for (int year = config.startYear; year <= config.endYear; year++) {
            yearComboBox.addItem(year + "");
        }

        for (String month : DateHelper.MONTH_NAMES) {
            monthComboBox.addItem(month);
        }

        for (int i = config.startHour; i <= config.endHour; i++) {
            hoursComboBox.addItem(String.format("%02d:00", i));
        }

        for (int i = 0; i < 60; i++) {
            secondsComboBox.addItem(String.format("%02d", i));
            minutesComboBox.addItem(String.format("%02d", i));
        }

        // Set defaults, if necessary
        if (config.setDateToNow) {
            setDate(Calendar.getInstance());
        } else {
            initDayComboBoxValues(); // after year and month is set (since they will have default values here)
        }
        ready = true;
    }

    public void setDate(Calendar date) {
        yearComboBox.setSelectedItem(date.get(Calendar.YEAR) + "");
        monthComboBox.setSelectedItem(DateHelper.MONTH_NAMES[date.get(Calendar.MONTH)]);
        String hoursTxt = String.format("%02d:00", date.get(Calendar.HOUR_OF_DAY));
        hoursComboBox.setSelectedItem(hoursTxt);

        if (hoursComboBox.getSelectedItem() != null
                && hoursComboBox.getSelectedItem().equals(hoursTxt)) { // only set minute,sec to current one, if hr was valid
            minutesComboBox.setSelectedItem(date.get(Calendar.MINUTE) + "");
            secondsComboBox.setSelectedItem(date.get(Calendar.SECOND) + "");
        } else {
            minutesComboBox.setSelectedItem("00");
            secondsComboBox.setSelectedItem("00");
        }
        initDayComboBoxValues(); // after year and month is set
        dayComboBox.setSelectedItem(date.get(Calendar.DAY_OF_MONTH)
                + " " + DateHelper.DAY_NAMES[date.get(Calendar.DAY_OF_WEEK) - 1]);
    }

    private void initDayComboBoxValues() {
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

            if (config.showSundays || !dayTxt.endsWith("Sunday"))
                dayComboBox.addItem(dayTxt);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void init() {
        // Design prompt
        JPanelBuilder panelBuilder = new JPanelBuilder()
                .setLayout(new GridLayout(config.amountVisible(), 2, 5, 5))
                .addLabel("Day: ")
                .add(dayComboBox)
                .addLabel("Month: ")
                .add(monthComboBox)
                .addLabel("Year: ")
                .add(yearComboBox);

        if (config.showMode != Config.SHOW_ONLY_DATE_MODE) {
            panelBuilder.addLabel("Hours: ")
                    .add(hoursComboBox)
                    .addLabel("Minutes: ")
                    .add(minutesComboBox);
        }

        if (config.showMode == Config.SHOW_HOURS_MINUTES_SECONDS_MODE)
            panelBuilder.addLabel("Seconds: ").add(secondsComboBox);

        panel = panelBuilder.getPanel();

        // Month/year combo box behaviour, to update the day values for each month/year pair when modified
        initComboBoxValues();
        yearComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (ready)
                    initDayComboBoxValues();
            }
        });
        monthComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (ready)
                    initDayComboBoxValues();
            }
        });
    }

    @Override
    public void show() {
        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, WINDOW_TITLE, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        successful = result == JOptionPane.OK_OPTION;
    }

    private int getSelectedSecondsNo() {
        return comboBoxValueAsInt(secondsComboBox);
    }

    private int getSelectedMinuteNo() {
        return comboBoxValueAsInt(minutesComboBox);
    }

    private int getSelectedHoursNo() {
        String hourNo = ((String) hoursComboBox.getSelectedItem()).split(":")[0];
        return Integer.parseInt(hourNo);
    }

    private int getSelectedYearNo() {
        return comboBoxValueAsInt(yearComboBox);
    }

    private int getSelectedMonthNo() {
        String monthName = (String) monthComboBox.getSelectedItem();

        for (int i = 0; i < DateHelper.MONTH_NAMES.length; i++)
            if (DateHelper.MONTH_NAMES[i].equalsIgnoreCase(monthName))
                return i;
        return -1;
    }

    private int getSelectedDayNo() {
        String dayNo = ((String) dayComboBox.getSelectedItem()).split(" ")[0];
        return Integer.parseInt(dayNo);
    }

    private int comboBoxValueAsInt(JComboBox comboBox) {
        return Integer.parseInt((String) comboBox.getSelectedItem());
    }

    public Calendar parseDate() {
        if (!successful)
            throw new IllegalStateException();
        Calendar c = Calendar.getInstance();
        c.set(getSelectedYearNo(), getSelectedMonthNo(), getSelectedDayNo(),
                config.showMode == Config.SHOW_ONLY_DATE_MODE ? 0 : getSelectedHoursNo(),
                config.showMode == Config.SHOW_ONLY_DATE_MODE ? 0 : getSelectedMinuteNo(),
                config.showMode != Config.SHOW_HOURS_MINUTES_SECONDS_MODE ? 0 : getSelectedSecondsNo());
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * Configuration for a {@link SelectDatePrompt}.
     */
    public static final class Config {

        public static final int SHOW_ONLY_DATE_MODE = 0;
        public static final int SHOW_HOURS_MINUTES_SECONDS_MODE = 1;
        public static final int SHOW_HOURS_MINUTES_MODE = 2;
        private final int showMode;
        private final boolean showSundays;
        private final boolean setDateToNow;
        private final int startYear;
        private final int endYear;
        private final int startHour;
        private final int endHour;

        /**
         * The year and hour fields are inclusive.
         */
        public Config(int showMode, boolean showSundays, boolean setDateToNow, int startYear, int endYear, int startHour,
                      int endHour) {
            this.showMode = showMode;
            this.showSundays = showSundays;
            this.setDateToNow = setDateToNow;
            this.startYear = startYear;
            this.endYear = endYear;
            this.startHour = startHour;
            this.endHour = endHour;
        }

        private int amountVisible() {
            return 3 + (showMode == SHOW_HOURS_MINUTES_SECONDS_MODE ? 3 : 0) + (showMode == SHOW_HOURS_MINUTES_MODE ? 2 : 0);
        }
    }
}
