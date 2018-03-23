/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui;

import common.Database;
import common.logic.Invoice;
import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import parts.logic.Parts;
import specialist.gui.TableModels.PartsTableModel;
import specialist.logic.SPC;
import specialist.logic.SPCBooking;
import specialist.logic.SPCPart;
import vehicles.logic.Vehicle;

public class PartsAtSPC extends JDialog {

    private final PartsTableModel partsTableModel = new PartsTableModel();
    private final Collection<SPCPart> parts;
    private final Vehicle vehicle;
    private int selectedRowIdx;
    private final SPC spc;
    private Calendar deliveryDate;
    private Calendar returnDate;
    private boolean changed = false;
    private JButton AddPartButton;
    private JButton EditPartButton;
    private JButton DeletePartButton;
    private JScrollPane partsScrollPane;
    private JTable partsTable;

    /**
     * Creates new form PartsAtSPC
     */
    public PartsAtSPC(Collection<SPCPart> parts, Vehicle vehicle, SPC spc) {
        this.vehicle = vehicle;
        setModal(true);
        setTitle("GMSIS SPC booking / Parts at selected SPC");
        initComponents();
        partsTableModel.loadParts(parts);
        this.parts = parts;
        this.spc = spc;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        partsScrollPane = new JScrollPane();
        partsTable = new JTable();
        AddPartButton = new JButton();
        EditPartButton = new JButton();
        DeletePartButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        partsTable.setModel(partsTableModel);
        partsScrollPane.setViewportView(partsTable);
        partsScrollPane.setPreferredSize(new Dimension(1200, 500));

        AddPartButton.setText("Add part");
        AddPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPartButtonActionPerformed(evt);
            }
        });

        EditPartButton.setText("Edit part");
        EditPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPartButtonActionPerformed(evt);
            }
        });

        DeletePartButton.setText("Delete part");
        DeletePartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletePartButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(partsScrollPane)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(AddPartButton)
                                .addGap(29, 29, 29)
                                .addComponent(EditPartButton)
                                .addGap(35, 35, 35)
                                .addComponent(DeletePartButton)
                                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(partsScrollPane)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(AddPartButton)
                                        .addComponent(EditPartButton)
                                        .addComponent(DeletePartButton)
                                        .addGap(35, 35, 35)))
        );

        pack();
        this.setLocationRelativeTo(null);
        for (int i = 0; i < 4; i++) {
            if (i == 2) {
                partsTable.getColumnModel().getColumn(i).setMinWidth(0);
                partsTable.getColumnModel().getColumn(i).setMaxWidth(0);
                partsTable.getColumnModel().getColumn(i).setWidth(0);
            }
        }
    }// </editor-fold>                        

    private void AddPartButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {

            SPCBooking booking = Database.getInstance().getCurrentSPCBookingForVehicle(vehicle);
            if (booking == null) {
                JOptionPane.showMessageDialog(null, "You can not add a part as there is no current SPC bookings for this vehicle.");
                return;
            }
            AddSPCPartPrompt addPart = new AddSPCPartPrompt(booking, spc, null, null);
            deliveryDate = booking.getStartDate();
            returnDate = booking.getEndDate();
            addPart.show();
            if (addPart.isSuccessful()) {
                SPCPart addedPart = addPart.parseSPCPart();
                try {
                    Database.getInstance().addSPCPart(addedPart);
                    addedPart.getSPCBooking().setTotalCost(addedPart.getSPCBooking().getSPCBookingTotal() + addedPart.getRepairCost());
                    partsTableModel.addRow(addedPart);
                    if (deliveryDate.compareTo(addedPart.getDeliveryDate()) > 0) {
                        deliveryDate = addedPart.getDeliveryDate();
                    }
                    if (returnDate.compareTo(addedPart.getReturnDate()) < 0) {
                        returnDate = addedPart.getReturnDate();
                    }
                    booking.setEndDate(returnDate);
                    booking.setStartDate(deliveryDate);
                    Database.getInstance().updateSPCBooking(booking);

                    Invoice invoice = addedPart.getSPCBooking().getBooking().getInvoice();
                    invoice.setSpcTotalCost(addedPart.getSPCBooking().getSPCBookingTotal());

                    if (invoice.isVehicleWarrantyActive()) {//if vehicle is under warranty, check if the warranty is still valid
                        if (booking.getBooking().getVehicle().getWarranty().getExpiryDate().compareTo(deliveryDate) <= 0) {    //if warranty has ended, make sure that the parts cost is added to the invoice
                            invoice.deactivateVehicleWarranty();//deactivate warranty if its end date is past
                            invoice.setSettled(false);
                            invoice.setSpcAmountDue(addedPart.getRepairCost());
                        }
                    } else {
                        invoice.setSpcAmountDue(invoice.getSpcAmountDue() + addedPart.getRepairCost());
                    }

                    Database.getInstance().updateInvoice(invoice);

                    changed = true;
                } catch (SQLException ex) {
                    Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(null, "Part has beed succesfully added to the booking");
            }
            // }
            changed = true;
        } catch (SQLException ex) {
            Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void DeletePartButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        selectedRowIdx = partsTable.getSelectedRow();
        if (selectedRowIdx == -1) {
            JOptionPane.showMessageDialog(null, "You need to select a part.", "Part is not chosen",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int partId = (int) partsTable.getModel().getValueAt(selectedRowIdx, 0);

        // Ask to confirm action
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the SPC with id: '" + partId + "'",
                "Confirm deletion of spc", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            SPCPart part = partsTableModel.rowToSPCPart(selectedRowIdx);
            partsTableModel.removeRow(selectedRowIdx);
            setBookingEndDate(part.getSPCBooking());
           
            //updating the invoice
            Invoice invoice = part.getSPCBooking().getBooking().getInvoice();
            invoice.setSpcTotalCost(invoice.getSpcTotalCost() - part.getRepairCost());
            deliveryDate =part.getSPCBooking().getStartDate();
            if (part.getSPCBooking().getBooking().getVehicle().getWarranty().getExpiryDate().compareTo(deliveryDate) <= 0) //if warranty has ended, make sure that the parts cost is added to the invoice
            {
                invoice.deactivateVehicleWarranty();//deactivate warranty if its end date is past
            }
            if (!invoice.isVehicleWarrantyActive())//if vehicle is under warranty, dont do anything, otherwise change the amount due
            {
                invoice.setSpcAmountDue(invoice.getSpcAmountDue() - part.getRepairCost());
            }
            Database.getInstance().deleteSPCPart(part);
            Database.getInstance().updateInvoice(invoice);
            

        } catch (ParseException | SQLException ex) {
            Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
        }
        changed = true;
        JOptionPane.showMessageDialog(null, "Part has beed succesfully delited");

    }

    private void EditPartButtonActionPerformed(java.awt.event.ActionEvent evt) {
        selectedRowIdx = partsTable.getSelectedRow();
        if (selectedRowIdx == -1) {
            JOptionPane.showMessageDialog(null, "You need to select a part.", "Part is not chosen",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int partId = (int) partsTable.getModel().getValueAt(selectedRowIdx, 0);

        try {
            SPCPart spcPart = partsTableModel.rowToSPCPart(selectedRowIdx);
            Parts part = spcPart.getPart();
            if (Calendar.getInstance().compareTo(spcPart.getDeliveryDate()) > 0) {
                JOptionPane.showMessageDialog(null, "You can not edit details of parts that have already been delivered.", "Delivery date has past",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            AddSPCPartPrompt addPart = new AddSPCPartPrompt(spcPart.getSPCBooking(), spc, part, spcPart);
            deliveryDate = spcPart.getSPCBooking().getStartDate();
            returnDate = spcPart.getSPCBooking().getEndDate();
            addPart.show();
            if (addPart.isSuccessful()) {
                SPCPart addedPart = addPart.parseSPCPart();

                Database.getInstance().editSPCPart(addedPart);
                spcPart.getSPCBooking().setTotalCost(spcPart.getSPCBooking().getSPCBookingTotal() - spcPart.getRepairCost() + addedPart.getRepairCost());
                partsTableModel.removeRow(selectedRowIdx);
                partsTableModel.addRow(addedPart);
                if (deliveryDate.compareTo(addedPart.getDeliveryDate()) > 0) {
                    deliveryDate = addedPart.getDeliveryDate();
                }
                if (returnDate.compareTo(addedPart.getReturnDate()) < 0) {
                    returnDate = addedPart.getReturnDate();
                }
                spcPart.getSPCBooking().setEndDate(returnDate);
                spcPart.getSPCBooking().setStartDate(deliveryDate);
                Database.getInstance().updateSPCBooking(spcPart.getSPCBooking());

                Invoice invoice = addedPart.getSPCBooking().getBooking().getInvoice();
                invoice.setSpcTotalCost(spcPart.getSPCBooking().getSPCBookingTotal());
                if (invoice.isVehicleWarrantyActive()) {//if vehicle is under warranty, check if the warranty is still valid
                    if (spcPart.getSPCBooking().getBooking().getVehicle().getWarranty().getExpiryDate().compareTo(deliveryDate) <= 0) { //if warranty has ended, make sure that the parts cost is added to the invoice
                        invoice.deactivateVehicleWarranty();//deactivate warranty if its end date is past
                        invoice.setSettled(false);
                        invoice.setSpcAmountDue(addedPart.getRepairCost());
                    }
                } else {
                    invoice.setSpcAmountDue(invoice.getSpcAmountDue() - spcPart.getRepairCost() + addedPart.getRepairCost());
                }
                Database.getInstance().updateInvoice(invoice);

                changed = true;

                JOptionPane.showMessageDialog(null, "Part has beed succesfully edited");
            }
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
        }
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setBookingEndDate(SPCBooking booking) {
        try {
            Collection<SPCPart> allParts = Database.getInstance().getAllPartsForSPCBooking(booking);
            Calendar bookingEndDate = Database.getInstance().getSPCVehicleForSPCBooking(booking).getExpReturnDate();
            for (SPCPart part : allParts) {
                if (part.getReturnDate().compareTo(bookingEndDate) > 1) {
                    bookingEndDate = part.getReturnDate();
                }
            }
            booking.setEndDate(bookingEndDate);
            Database.getInstance().updateSPCBooking(booking);
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartsAtSPC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}