/**
 * @author ec15143 Nikita Mirolyubov Sample code taken from AddUsePrompt
 */
package specialist.gui;

import specialist.logic.DateHelperSPC;
import common.Database;
import common.gui.util.GenericPrompt;
import common.util.InvalidDateException;
import specialist.logic.SPC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import parts.logic.InstalledParts;
import parts.logic.Parts;
import specialist.logic.SPCBooking;
import specialist.logic.SPCPart;

public final class AddSPCPartPrompt extends GenericPrompt {

    private final JTextField partNameTextField = new JTextField(20);
    private final JTextField partDescriptionTextField = new JTextField(20);
    private final JTextField repairCostTextField = new JTextField(20);
    private final JTextField deliveryDateTextField = new JTextField(20);
    private final JTextField returnDateTextField = new JTextField(20);
    private final JTextField chooseSPCTextField = new JTextField(20);
    private final JTextField faultDescriptionTextField = new JTextField(20);
    private final JLabel chooseSPC = new JLabel();
    private final JButton chooseInstalledPartButton = new JButton("Choose from installed parts");
    private final JButton chooseSPCButton = new JButton("Choose SPC");
    private final JButton choosePartButton = new JButton("Choose part");
    private final JButton chooseExpDelDateButton = new JButton("Choose delivery date");
    private final JButton chooseExpRetDateButton = new JButton("Choose return date");
    private int partId;
    private Double repairCost;
    private Calendar returnDate;
    private Calendar deliveryDate;
    private SPC spc;
    private Parts part;
    private boolean installed = false;
    private int installedPartId;
    private final SPCPart spcPart;
    private InstalledParts installedPart;
    private final SPCBooking booking;
    private boolean firstTime = false;
    private String promptName = "GMSIS SPC booking / Add part";

    public AddSPCPartPrompt(SPCBooking booking, SPC spc, Parts part, SPCPart spcPart) {
        this.booking = booking;
        this.spc = spc;
        this.spcPart = spcPart;
        this.part = part;
        if (this.spc == null) {
            firstTime = true;
        }
    }

    public void show() {

        // Design prompt   
        GridLayout layout = new GridLayout(7, 4, 5, 5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(chooseSPC);
        panel.add(chooseSPCTextField);
        if (spc == null) {
            panel.add(chooseSPCButton);
            panel.add(new JLabel());
            chooseSPC.setText("Select SPC");
        } else {
            chooseSPC.setText("Selected SPC");
            chooseSPCTextField.setText(spc.getName());
            panel.add(new JLabel());
            panel.add(new JLabel());
        }
        panel.add(new JLabel("Part name: "));
        panel.add(partNameTextField);
        if (part == null) {
            panel.add(choosePartButton);
            panel.add(chooseInstalledPartButton);
            panel.add(new JLabel("Part description: "));
        } else {
            partNameTextField.setText(part.getName());
            panel.add(new JLabel());
            panel.add(new JLabel());
            panel.add(new JLabel("Part description: "));
            partDescriptionTextField.setText(part.getDescription());
        }
        partNameTextField.setEditable(false);
        partDescriptionTextField.setEditable(false);
        panel.add(partDescriptionTextField);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("Fault description: "));
        panel.add(faultDescriptionTextField);
        panel.add(new JLabel());
        panel.add(new JLabel());
        if (spcPart != null) {
            faultDescriptionTextField.setText(spcPart.getFaultDescription());
            repairCostTextField.setText("" + spcPart.getRepairCost());
            deliveryDateTextField.setText(DateHelperSPC.toString(spcPart.getDeliveryDate()));
            deliveryDate = spcPart.getDeliveryDate();
            returnDateTextField.setText(DateHelperSPC.toString(spcPart.getReturnDate()));
            returnDate = spcPart.getReturnDate();
            promptName = "GMSIS SPC booking / Edit part";
        }
        panel.add(new JLabel("Cost of repair: "));
        panel.add(repairCostTextField);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("Estimated delivery date: "));
        panel.add(deliveryDateTextField);
        panel.add(chooseExpDelDateButton);
        panel.add(new JLabel());
        panel.add(new JLabel("Estimated return date "));
        panel.add(returnDateTextField);
        panel.add(chooseExpRetDateButton);
        panel.add(new JLabel());

        deliveryDateTextField.setEditable(false);
        returnDateTextField.setEditable(false);
        chooseSPCTextField.setEditable(false);

        // Behaviour for the choose exp del date button
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
        if (part == null) {
            choosePartButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ChoosePartPrompt cpp = new ChoosePartPrompt();
                    cpp.show();
                    if (cpp.isSuccessful()) {
                        try {
                            part = cpp.getSelectedPart();
                        } catch (ParseException ex) {
                            Logger.getLogger(AddSPCPartPrompt.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        partNameTextField.setText(part.getName());
                        partDescriptionTextField.setText(part.getDescription());
                    }
                }
            });

            chooseInstalledPartButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ChooseInstalledPartPrompt cpp = new ChooseInstalledPartPrompt(booking);
                    cpp.show();
                    if (cpp.isSuccessful()) {
                        try {
                            installedPart = cpp.getSelectedPart();
                            part = new Parts(installedPart.getID(), installedPart.getName(), installedPart.getDescription());
                        } catch (ParseException ex) {
                            Logger.getLogger(AddSPCPartPrompt.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!firstTime) {
                            try {
                                
                                if (Database.getInstance().checkForDuplicatingInstalledPartsInSPCBooking(installedPart.getinstallpartID())) {
                                    JOptionPane.showMessageDialog(null, "This part has already been added to the booking");
                                    return;
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(AddSPCPartPrompt.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        partNameTextField.setText(part.getName());
                        partDescriptionTextField.setText(part.getDescription());
                        installed = true;
                        installedPartId = installedPart.getinstallpartID();
                        if (Calendar.getInstance().compareTo(installedPart.getExpiryDateDatabase()) < 1) {
                            repairCostTextField.setText("0");
                            repairCostTextField.setEditable(false);
                        }
                    }
                }
            });
        }

        // Behaviour for the choose exp ret date button
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
        if (spc == null) {
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
        }

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, promptName, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values

            try {
                repairCost = (double)Math.round(Double.parseDouble(repairCostTextField.getText())*100)/100;
                

            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid repair cost, it should be a number.");
                return;

            }
            if (repairCost < 0 || repairCost > 10000) {
                showErrorDialog("Invalid repair cost, it can not be negative or larger than 10,000.");
                return;
            }

            // Check if first name, surname or password are blank
            if (partNameTextField.getText().trim().isEmpty()
                    || partDescriptionTextField.getText().trim().isEmpty() || repairCostTextField.getText().isEmpty() || deliveryDateTextField.getText().isEmpty()
                    || returnDateTextField.getText().trim().isEmpty() || part == null || faultDescriptionTextField.getText().trim().isEmpty()) {
                showErrorDialog("Invalid part details, all field must not be blank.");
                return;
            }

            try {
                DateHelperSPC.validateBookingDates(deliveryDate, returnDate, null);
            } catch (SQLException | InvalidDateException ex) {
                showErrorDialog(ex.getMessage());
                return;
            }

            successful = true;
        }
    }

    public SPCPart parseSPCPart() {
        if (!successful) {
            throw new IllegalStateException();
        }
        if (spcPart == null && !installed) //executed when booking is created at the first time
        {
            return new SPCPart(-1, part, repairCost, deliveryDate, returnDate, spc, booking, faultDescriptionTextField.getText(), 0);
        } else if (installed) //executed when booking is modified by adding an installed part
        {
            return new SPCPart(-1, part, repairCost, deliveryDate, returnDate, spc, booking, faultDescriptionTextField.getText(), installedPartId);
        } else //executed when booking is modified by adding an non-installed part
        {
            return new SPCPart(spcPart.getId(), part, repairCost, deliveryDate, returnDate, spc, booking, faultDescriptionTextField.getText(), 0);
        }

    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean isInstalledPart() {
        return installed;
    }

    public int getInstalledPartId() {
        return installedPartId;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding SPC", JOptionPane.ERROR_MESSAGE);
    }

}
