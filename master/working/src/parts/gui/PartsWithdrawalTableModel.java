
package parts.gui;

import common.gui.util.DefaultTableModelEx;
import parts.logic.InstalledParts;
import parts.logic.PartWithdrawals;
import common.Database;
import java.util.Collection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author: Taiybah Hussain EC15205
 * Reference List
 * 1. ManageUsersTableModel : Used as an example on how to create a table model
 */
public class PartsWithdrawalTableModel extends DefaultTableModelEx{
    
    private static final String[] COLUMN_NAMES = {
            "Booking ID", "Booking Date", "Vehicle Reg", "Customer Name", "Part Name", "Part Cost","Quantity", "Type"
    };
    private static final Class[] COLUMN_TYPES = {
            Integer.class, String.class, String.class, String.class,String.class, String.class, Double.class, Integer.class, String.class
    };

    public PartsWithdrawalTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        for(int i =0;i<getColumnCount();i++){
            setColumnEditable(i,false);
        }
    }
        
    public void addRow(PartWithdrawals pw){
        addRow(new Object[] {
                pw.getBookingID(),
                pw.getBookStartDate(),
                pw.getVehicleReg(),
                pw.getCustName(),
                pw.getPartName(),
                pw.getPartCost(),
                pw.getQuantity(),
                pw.getTypeforTable(),
                
        });
    }
    
    public void loadWithdrawals() throws ParseException{
        try{
        
            List<PartWithdrawals> partwithdraw = Database.getInstance().getAllPartWithdrawals();
            for (PartWithdrawals pw : partwithdraw) {
                addRow(pw);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
