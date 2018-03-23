/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

/*
 * SOURCE CODE TAKEN FROM DIAGREP.CHOOSE DATE PROMPT
 */
import specialist.logic.DateHelperSPC;
import common.gui.util.JPanelBuilder;
import diagrep.gui.date.GenericDatePrompt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * A JOptionPane wrapper that receives user input to create a {@link Calendar}
 * from.
 */
public final class ChooseDatePromptSPC extends GenericDatePrompt {

    /**
     * Adds an {@link ActionListener} to the provided button, such that on click
     * it shows a {@link ChooseDatePrompt} and if the prompt is successful it
     * will set the parsed date into the provided textfield with the format
     * defined by {@link DateHelper#toString(Calendar)}.
     */
    public static void setAsChooseDateComponents(JButton btn, final JTextField tf) {
        setAsChooseDateComponents(btn, tf, false);
    }

    public static void setAsChooseDateComponents(JButton btn, final JTextField tf, final boolean omitSundays) {
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseDatePromptSPC cdp = new ChooseDatePromptSPC();
                cdp.setOmitSundays(omitSundays);
                cdp.show();

                if (cdp.isSuccessful()) {
                    tf.setText(DateHelperSPC.toString(cdp.parseDate()));
                }
            }
        });
    }

    private boolean omitTime = true;

    /**
     * @param omitTime If time boxes should be omitted.
     */
    public void show(boolean omitTime) {
        this.omitTime = omitTime;

        // Design prompt
        JPanelBuilder panelBuilder = new JPanelBuilder()
                .setLayout(new GridLayout(omitTime ? 3 : 5, 2, 5, 5))
                .addLabel("Day: ")
                .add(dayComboBox)
                .addLabel("Month: ")
                .add(monthComboBox)
                .addLabel("Year: ")
                .add(yearComboBox);
        JPanel panel;
        panel = panelBuilder.getPanel();

        // Month/year combo box behaviour, to update the day values for each month/year pair when modified
        initComboBoxValues();
        yearComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (ready) {
                    initDayComboBoxValues();
                }
            }
        });
        monthComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (ready) {
                    initDayComboBoxValues();
                }
            }
        });

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Choose Date...", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        successful = result == JOptionPane.OK_OPTION;
    }

    public void show() {
        show(false);
    }

    public Calendar parseDate() {
        if (!successful) {
            throw new IllegalStateException();
        }
        Calendar c = Calendar.getInstance();
        c.set(getSelectedYearNo(), getSelectedMonthNo(), getSelectedDayNo(),
                omitTime ? 0 : getSelectedHoursNo(), omitTime ? 0 : getSelectedMinuteNo(), 0);
        return c;
    }
}
