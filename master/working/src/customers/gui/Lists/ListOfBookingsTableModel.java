/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Lists;

import common.Database;
import common.gui.util.DefaultTableModelEx;
 import diagrep.logic.Booking; 
import diagrep.logic.DateHelper; 
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection; 

/**
 *
 * @author cawaala
 */
public class ListOfBookingsTableModel extends DefaultTableModelEx {
     private static final String[] COLUMN_NAMES = {
           "Customer", "Start Date", "End Date"
    };
    private static final Class[] COLUMN_TYPES = {
            String.class, String.class, String.class 
    }; 

    public ListOfBookingsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setEditable(false);
    }

     public void addRow(Booking booking) {
        addRow(new Object[] {
                booking.getCustomer().getFullName(), 
                DateHelper.toString(booking.getBookingStartDate()),
                DateHelper.toString(booking.getBookingEndDate()) 
        });
    }

    public void loadBookings() throws SQLException, ParseException {
        Collection<Booking> allBookings = Database.getInstance().getAllBookings();
        for (Booking booking : allBookings) {
            addRow(booking); 
        }

    }
    
     public void loadPastBookings() throws SQLException, ParseException {
        Collection<Booking> allBookings = Database.getInstance().getAllPastBookings();
        for (Booking booking : allBookings) {
            addRow(booking); 
        }

    }
     
      public void loadFutureBookings() throws SQLException, ParseException {
        Collection<Booking> allBookings = Database.getInstance().getAllFutureBookings();
        for (Booking booking : allBookings) {
            addRow(booking); 
        }

    }
}
