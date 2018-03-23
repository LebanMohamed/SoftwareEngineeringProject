/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import parts.logic.Parts;
import parts.logic.StockParts;
import specialist.logic.DateHelperSPC;
import specialist.logic.SPC;
import specialist.logic.SPCPart;
import specialist.logic.SPCBooking;

public class PartsTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "Part id", "SPC id", "SPC Booking id", "SPC Name", "Part Name", "Part Description", "Fault description", "Repair cost",
        "Exp. Delivery date", "Exp. Return date"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class
    };

    public PartsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(SPCPart part) {
        addRow(new Object[]{
            part.getId(), part.getPart().getID(), part.getSPC().getId(), part.getSPCBooking().getSpcBookingId(), part.getSPC().getName(), part.getPart().getName(), part.getPart().getDescription(), part.getFaultDescription(), part.getRepairCost(), DateHelperSPC.toString(part.getDeliveryDate()), DateHelperSPC.toString(part.getReturnDate())
        });
    }

    public SPCPart rowToSPCPart(int rowIdx) throws ParseException {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int spcPartId = (int) getValueAt(rowIdx, 0);
        int partId = (int) getValueAt(rowIdx, 1);
        int spcId = (int) getValueAt(rowIdx, 2);
        String faultDescription = (String) getValueAt(rowIdx, 7);
        int spcBookingId = (Integer) getValueAt(rowIdx, 3);
        Double repairCost = (Double) getValueAt(rowIdx, 8);
        Calendar deliveryDate = DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 9));
        Calendar returnDate = DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 10));
        SPC spc = null;
        SPCBooking spcBooking = null;
        StockParts stockpart = null;
        Parts part = null;
        try {
            spc = Database.getInstance().getSPC(spcId);
            spcBooking = Database.getInstance().getSPCBooking(spcBookingId);
            stockpart = Database.getInstance().getStockPartForId(partId);
        } catch (SQLException ex) {
            Logger.getLogger(PartsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        part = (Parts) stockpart;
        return new SPCPart(spcPartId, part, repairCost, deliveryDate, returnDate, spc, spcBooking, faultDescription, 0);
    }

    public void loadParts(Collection<SPCPart> allParts) {

        // Iterate over the parts and add them to the table row by row
        for (SPCPart part : allParts) {
            addRow(part);
        }

    }

    public void loadParts() {
        try {
            Collection<SPCPart> allParts = Database.getInstance().getAllOutstandingPartsAtSPCs();
            // Iterate over the parts and add them to the table row by row
            for (SPCPart part : allParts) {
                addRow(part);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     public void loadReturnedParts() {
        try {
            Collection<SPCPart> allParts = Database.getInstance().getAllReturnedPartsAtSPCs();
            // Iterate over the parts and add them to the table row by row
            for (SPCPart part : allParts) {
                addRow(part);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    //disable editing of all cells

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
