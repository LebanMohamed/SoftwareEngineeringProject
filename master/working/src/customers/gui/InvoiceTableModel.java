/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection; 
import diagrep.logic.Booking;
import diagrep.logic.DateHelper;
 

/**
 *
 * @author cawaala
 */
public class InvoiceTableModel extends DefaultTableModelEx {
 
    private static final String[] COLUMN_NAMES = {
       
      "Customer",  "SPC Total Cost", "Booking Total Cost", "Amount Paid",
        "Booking Start Date","Booking End Date","Warranty", "Settled"

    };
    private static final Class[] COLUMN_TYPES = {
        String.class,  String.class, String.class, String.class,
        String.class, String.class,Boolean.class,Boolean.class
    };

    public InvoiceTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setEditable(false);
    }
    
   public void addRow(Booking bills) {
        addRow(new Object[]{ 
            
            bills.getCustomer().getFullName(), 
            bills.getInvoice().getSpcTotalCost(),
            bills.getInvoice().getBookingTotalCost(),
            bills.getInvoice().getAmtPaid(),
            DateHelper.toString(bills.getBookingStartDate()),
            DateHelper.toString(bills.getBookingEndDate()),
            bills.getVehicle().isWarrantyActive(),
            bills.getInvoice().isSettled()
          
        });
    }
   
   public void loadBill() throws ParseException, SQLException {

        Collection<Booking> bills = Database.getInstance().getAllBillsForCustomer();
        for (Booking Bill : bills) {
            addRow(Bill);
        }
    }

 

}
