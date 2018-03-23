package diagrep.gui.date;

import common.gui.util.JPanelBuilder;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link JOptionPane} to return a formatted string, representing a date including a ? wildcard identifier
 * where necessary.
 */
public final class SearchDatePrompt extends GenericDatePrompt {

    private final JCheckBox searchDayCheckBox = new JCheckBox("Search By Day");
    private final JCheckBox searchMonthCheckBox = new JCheckBox("Search By Month");
    private final JCheckBox searchYearCheckBox = new JCheckBox("Search By Year");
    private final JCheckBox searchHoursCheckBox = new JCheckBox("Search By Hours");
    private final JCheckBox searchMinuteCheckBox = new JCheckBox("Search By Minute");

    public void show() {
        // Design prompt
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(5, 3, 5, 5))
                .addLabel("Day: ")
                .add(dayComboBox)
                .add(searchDayCheckBox)
                .addLabel("Month: ")
                .add(monthComboBox)
                .add(searchMonthCheckBox)
                .addLabel("Year: ")
                .add(yearComboBox)
                .add(searchYearCheckBox)
                .addLabel("Hours: ")
                .add(hoursComboBox)
                .add(searchHoursCheckBox)
                .addLabel("Minutes: ")
                .add(minutesComboBox)
                .add(searchMinuteCheckBox)
                .getPanel();

        // Do not show sundays
        setOmitSundays(true);

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

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "Search By Date", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        successful = result == JOptionPane.OK_OPTION;
    }

    public String parseSearchDate() {
        if (!successful)
            throw new IllegalStateException();
        return checkedValueOrDefault(searchYearCheckBox, getSelectedYearNo(), "????") + "-"
                + checkedValueOrDefault(searchMonthCheckBox, getSelectedMonthNo() + 1) + "-"
                + checkedValueOrDefault(searchDayCheckBox, getSelectedDayNo()) + " "
                + checkedValueOrDefault(searchHoursCheckBox, getSelectedHoursNo()) + ":"
                + checkedValueOrDefault(searchMinuteCheckBox, getSelectedMinuteNo()) + ":00";
    }

    private static String checkedValueOrDefault(JCheckBox checkBox, int value) {
        return checkedValueOrDefault(checkBox, value, "??");
    }

    private static String checkedValueOrDefault(JCheckBox checkBox, int value, String def) {
        return checkBox.isSelected() ? String.format("%02d", value) : def;
    }
}

