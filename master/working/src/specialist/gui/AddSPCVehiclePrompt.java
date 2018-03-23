/**
 * @author ec15143 Nikita Mirolyubov, Sample code taken from AddUsePrompt
 */
package specialist.gui;

import specialist.logic.DateHelperSPC;
import common.gui.util.GenericPrompt;
import common.util.InvalidDateException;
import specialist.logic.SPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import specialist.logic.SPCBooking;
import specialist.logic.SPCVehicle;
import vehicles.logic.Vehicle;
import vehicles.logic.VehicleTemplate;

public final class AddSPCVehiclePrompt extends GenericPrompt {

    private final JTextField vehicleDescriptionTextField = new JTextField(20);
    private final JTextField vehiclevehicleRegistrationNoTextField = new JTextField(20);
    private final JTextField repairCostTextField = new JTextField(20);
    private final JTextField deliveryDateTextField = new JTextField(20);
    private final JTextField returnDateTextField = new JTextField(20);
    private final JLabel chooseSPC = new JLabel("Select SPC");
    private final JButton chooseSPCButton = new JButton("Choose SPC");
    private final JTextField chooseSPCTextField = new JTextField(20);
    private final JButton chooseExpDelDateButton = new JButton("Choose delivery date");
    private final JButton chooseExpRetDateButton = new JButton("Choose return date");
    private final Vehicle vehicle;
    private final SPCBooking booking;
    private Calendar returnDate;
    private Calendar deliveryDate;
    private Double repairCost;
    private SPC spc;

    public AddSPCVehiclePrompt(SPCBooking booking) {
        this.booking = booking;
        this.vehicle = booking.getBooking().getVehicle();
    }

    @Override
    public void show() {
        // Design prompt
        VehicleTemplate vt;
        vt = vehicle.getTemplate();
        vehiclevehicleRegistrationNoTextField.setText(vehicle.getRegistrationNo());
        vehicleDescriptionTextField.setText(vt.getMake() + ", " + vt.getModel() + ", " + vt.getEngineSize() + ", " + vt.getFuelType() + ", " + vehicle.getColour());

        GridLayout layout = new GridLayout(6, 3, 5, 5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(chooseSPC);
        panel.add(chooseSPCTextField);
        panel.add(chooseSPCButton);
        panel.add(new JLabel("Vehicle registration number: "));
        panel.add(vehiclevehicleRegistrationNoTextField);
        panel.add(new JLabel());
        panel.add(new JLabel("Vehicle description: "));
        panel.add(vehicleDescriptionTextField);
        panel.add(new JLabel());
        panel.add(new JLabel("Cost of repair: "));
        panel.add(repairCostTextField);
        panel.add(new JLabel());
        panel.add(new JLabel("Estimated delivery date: "));
        panel.add(deliveryDateTextField);
        panel.add(chooseExpDelDateButton);
        panel.add(new JLabel("Estimated return date "));
        panel.add(returnDateTextField);
        panel.add(chooseExpRetDateButton);

        //disallow manual edits of fields that should be filled automatically 
        deliveryDateTextField.setEditable(false);
        returnDateTextField.setEditable(false);
        chooseSPCTextField.setEditable(false);
        vehicleDescriptionTextField.setEditable(false);
        vehiclevehicleRegistrationNoTextField.setEditable(false);

        chooseExpDelDateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseDatePromptSPC cdp = new ChooseDatePromptSPC();
                cdp.show();

                if (cdp.isSuccessful()) {
                    deliveryDateTextField.setText(DateHelperSPC.toString(cdp.parseDate()));
                    deliveryDate = DateHelperSPC.toCalendar(deliveryDateTextField.getText());
                }
            }
        });

        chooseExpRetDateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseDatePromptSPC cdp = new ChooseDatePromptSPC();
                cdp.show();

                if (cdp.isSuccessful()) {
                    returnDateTextField.setText(DateHelperSPC.toString(cdp.parseDate()));
                    returnDate = DateHelperSPC.toCalendar(returnDateTextField.getText());
                }
            }
        });

        chooseSPCButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ChooseSPCPrompt prompt = new ChooseSPCPrompt();
                prompt.show();
                if (prompt.isSuccessful()) {
                    chooseSPCTextField.setText(prompt.getSelectedSPC().getName());
                    spc = prompt.getSelectedSPC();
                }
            }
        });

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS SPC booking / Add vehicle", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values
            try {
                repairCost = (double)Math.round(Double.parseDouble(repairCostTextField.getText())*100)/100;

            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid repair cost");
                return;
            }

            if (repairCost < 0 || repairCost > 10000) {
                showErrorDialog("Invalid repair cost, it can not be negative or larger than 10,000.");
                return;
            }

            if (repairCostTextField.getText().trim().isEmpty() || returnDateTextField.getText().trim().isEmpty()
                    || deliveryDateTextField.getText().trim().isEmpty()) {
                showErrorDialog("Invalid boking details, all field must not be blank.");
                return;
            }
            try {
                DateHelperSPC.validateBookingDates(deliveryDate, returnDate, vehicle.getRegistrationNo());
            } catch (SQLException | InvalidDateException ex) {
                showErrorDialog(ex.getMessage());
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public SPCVehicle parseSPCVehicle() {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new SPCVehicle(deliveryDate, returnDate, repairCost, booking, spc, -1);
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding SPC", JOptionPane.ERROR_MESSAGE);
    }
}
