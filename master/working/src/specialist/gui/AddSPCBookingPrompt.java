/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import common.Database;
import common.gui.util.GenericPrompt;
import common.logic.Invoice;
import diagrep.logic.Booking;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import specialist.logic.SPCBooking;
import specialist.logic.SPCPart;
import specialist.logic.SPCVehicle;


public class AddSPCBookingPrompt extends GenericPrompt {

    private final JButton addPartButton = new JButton("Add part");
    private final JButton addVehicleButton = new JButton("Add vehicle");
    private final Booking booking;
    private SPCVehicle vehicle;
    private SPCPart part;
    private SPCBooking spcBooking;
    private boolean vehicleAdded = false;
    private boolean partAdded = false;
    private Collection<SPCPart> addedParts = new ArrayList();
    private Calendar startDate = Calendar.getInstance();//current time
    private Calendar endDate = Calendar.getInstance();//current time
    private Double totalCost = 0.0;
    private ArrayList<Integer> installedPartIds = new ArrayList<Integer>();

    public AddSPCBookingPrompt(Booking booking) {
        this.booking = booking;
        spcBooking = new SPCBooking(-1, booking);

    }

    @Override
    public void show() {
        GridLayout layout = new GridLayout(1, 2, 5, 5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(addPartButton);
        panel.add(addVehicleButton);

        addPartButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddSPCPartPrompt addSPCPartPrompt = new AddSPCPartPrompt(spcBooking, null, null, null);
                addSPCPartPrompt.show();

                if (addSPCPartPrompt.isSuccessful()) {
                    partAdded = true;
                    if (addSPCPartPrompt.isInstalledPart()) {
                        for (int i = 0; i < installedPartIds.size(); i++) {
                            if (installedPartIds.get(i) == addSPCPartPrompt.getInstalledPartId()) {
                                JOptionPane.showMessageDialog(null, "This part has already been added");
                            }
                            return;
                        }
                        installedPartIds.add(addSPCPartPrompt.getInstalledPartId());
                    }

                    part = addSPCPartPrompt.parseSPCPart();
                    addedParts.add(part);
                    totalCost += part.getRepairCost();

                }
            }
        });

        addVehicleButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!vehicleAdded) {
                    AddSPCVehiclePrompt addSPCVehiclePrompt = new AddSPCVehiclePrompt(spcBooking);
                    addSPCVehiclePrompt.show();

                    if (addSPCVehiclePrompt.isSuccessful()) {
                        //to do
                        vehicleAdded = true;
                        vehicle = addSPCVehiclePrompt.parseSPCVehicle();
                        totalCost += vehicle.getRepairCost();
                    }
                } else {
                    showErrorDialog("Vehicle has already been added to this booking.");
                }
            }

        });
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS SPC booking / Add booking", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            if (partAdded || vehicleAdded) {

                if (vehicleAdded) {
                    //checking the dates
                    
                        startDate = vehicle.getExpDeliveryDate();
                        endDate = vehicle.getExpReturnDate();
                   

                }
                if (partAdded) {
                    if (!vehicleAdded) {
                        Calendar sDate = Calendar.getInstance();
                        sDate.set(9999, 12, 1);
                        startDate = sDate;
                        Calendar eDate=Calendar.getInstance();
                        eDate.set(1000, 12, 1);
                        endDate = eDate;
                    }

                    for (SPCPart part : addedParts) {
                        //checking the dates
                        if (startDate.compareTo(part.getDeliveryDate()) > 0) {
                            startDate = part.getDeliveryDate();
                        }
                        if (endDate.compareTo(part.getReturnDate()) < 0) {
                            endDate = part.getReturnDate();
                        }

                    }
                }
                try {
                    spcBooking.setEndDate(endDate);
                    spcBooking.setStartDate(startDate);
                    Invoice invoice = booking.getInvoice();
                    if (spcBooking.getBooking().getVehicle().getWarranty().getExpiryDate().compareTo(startDate) <= 0) {
                        invoice.deactivateVehicleWarranty(); //if the warranty is not active at the start date of booking, deactivate it
                    }
                    spcBooking.setSPCBookingTotal(totalCost);
                    invoice.setSpcTotalCost(spcBooking.getSPCBookingTotal());
                    if (spcBooking.getBooking().getInvoice().isVehicleWarrantyActive()) {
                        invoice.setSpcAmountDue(0);
                    } else {
                        invoice.setSpcAmountDue(spcBooking.getSPCBookingTotal());
                        invoice.setSettled(false);//if the invoice was settled, but a bookings is added- invoice becomes unsettled
                    }

                    Database.getInstance().addSPCBooking(spcBooking);
                    if (vehicleAdded) {
                        Database.getInstance().addSPCVehicle(vehicle);
                    }
                    if (partAdded) {
                        for (SPCPart part : addedParts) {
                            Database.getInstance().addSPCPart(part);
                        }
                    }
                    Database.getInstance().updateInvoice(invoice);
                } catch (SQLException e) {

                    e.printStackTrace();
                }

            }
            successful = true;
            JOptionPane.showMessageDialog(null, "Succesfully added an SPC booking");

        }
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding SPC Booking", JOptionPane.ERROR_MESSAGE);
    }

}
