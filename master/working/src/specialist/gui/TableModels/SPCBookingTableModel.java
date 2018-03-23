/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import diagrep.logic.Booking;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import specialist.logic.DateHelperSPC;
import specialist.logic.SPCBooking;
import vehicles.logic.Vehicle;

public class SPCBookingTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "Booking ID", "Customer", "Vehicle Reg No",
        "Vehicle Make", "Vehicle Model", "Start date", "End date", "Total cost"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, Integer.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class
    };

    public SPCBookingTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(SPCBooking spcBooking) {
        addRow(new Object[]{
            spcBooking.getSpcBookingId(), spcBooking.getBooking().getId(), spcBooking.getBooking().getCustomer().getFullName(), spcBooking.getBooking().getVehicle().getRegistrationNo(), spcBooking.getBooking().getVehicle().getTemplate().getMake(), spcBooking.getBooking().getVehicle().getTemplate().getModel(), DateHelperSPC.toString(spcBooking.getStartDate()), DateHelperSPC.toString(spcBooking.getEndDate()), spcBooking.getSPCBookingTotal()
        });
    }

    public SPCBooking rowToSPCBooking(int rowIdx) throws ParseException {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int spcBookingId = (int) getValueAt(rowIdx, 0);
        int bookingId = (int) getValueAt(rowIdx, 1);
        Calendar startDate = DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 6));
        Calendar endDate = DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 7));
        Double totalCost = (Double) getValueAt(rowIdx, 8);
        Booking booking = null;

        try {
            booking = Database.getInstance().getBooking(bookingId);
        } catch (SQLException ex) {
            Logger.getLogger(PartsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        SPCBooking spcBooking = new SPCBooking(spcBookingId, booking);
        spcBooking.setEndDate(endDate);
        spcBooking.setStartDate(startDate);
        spcBooking.setSPCBookingTotal(totalCost);
        return spcBooking;

    }

    public void loadSPCBookings() throws SQLException, ParseException {
        Collection<SPCBooking> allBookings = Database.getInstance().getAllSPCBookings();
        // Iterate over the parts and add them to the table row by row
        for (SPCBooking spcBooking : allBookings) {
            if (spcBooking.getEndDate().compareTo(Calendar.getInstance()) > 1 && spcBooking.getStartDate().compareTo(Calendar.getInstance()) < 1) {
                addRow(spcBooking);
            }
            addRow(spcBooking);
        }

    }

    public void loadPastSPCBookings() throws SQLException, ParseException {
        Collection<SPCBooking> allBookings = Database.getInstance().getAllSPCBookings();
        // Iterate over the parts and add them to the table row by row
        for (SPCBooking spcBooking : allBookings) {
            Calendar eDt =spcBooking.getEndDate();
            eDt.set(Calendar.HOUR, 23);
            eDt.set(Calendar.MINUTE, 59);
            eDt.set(Calendar.SECOND, 59);
            eDt.set(Calendar.MILLISECOND, 59);
            if (eDt.compareTo(Calendar.getInstance()) <= 0) {
                addRow(spcBooking);
            }
        }

    }

    public void loadFutureSPCBookings() throws SQLException, ParseException {
        Collection<SPCBooking> allBookings = Database.getInstance().getAllSPCBookings();
        // Iterate over the parts and add them to the table row by row
        for (SPCBooking spcBooking : allBookings) {
            Calendar stDt= spcBooking.getStartDate();
            stDt.set(Calendar.HOUR, 0);
            stDt.set(Calendar.MINUTE, 00);
            stDt.set(Calendar.SECOND, 00);
            stDt.set(Calendar.MILLISECOND, 00);
            if (stDt.compareTo(Calendar.getInstance()) >= 0) {
                addRow(spcBooking);
            }
        }

    }

    public void loadCurrentSPCBookings() throws SQLException, ParseException {
        Collection<SPCBooking> allBookings = Database.getInstance().getAllSPCBookings();
        // Iterate over the parts and add them to the table row by row
        for (SPCBooking spcBooking : allBookings) {
            Calendar stDt= spcBooking.getStartDate();
            Calendar eDt =spcBooking.getEndDate();
            stDt.set(Calendar.HOUR, 0);
            stDt.set(Calendar.MINUTE, 00);
            stDt.set(Calendar.SECOND, 00);
            stDt.set(Calendar.MILLISECOND, 00);
            eDt.set(Calendar.HOUR, 23);
            eDt.set(Calendar.MINUTE, 59);
            eDt.set(Calendar.SECOND, 59);
            eDt.set(Calendar.MILLISECOND, 59);
            if (stDt.compareTo(Calendar.getInstance()) <= 0 && eDt.compareTo(Calendar.getInstance()) >= 0) {
                addRow(spcBooking);
            }
        }

    }

    public void loadSPCBookings(Collection<SPCBooking> allBookings) throws SQLException, ParseException {
        // Iterate over the parts and add them to the table row by row
        for (SPCBooking spcBooking : allBookings) {
            addRow(spcBooking);
        }

    }

    public void loadSPCBookingsForVehicle(Vehicle vehicle) {
        Collection<SPCBooking> allBookings;
        try {
            allBookings = Database.getInstance().getAllSPCBookingsForVehicle(vehicle);
            // Iterate over the parts and add them to the table row by row
            for (SPCBooking spcBooking : allBookings) {
                addRow(spcBooking);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(SPCBookingTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //disable editing of all cells

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
