

package parts.gui;

import common.gui.util.DefaultTableModelEx;
import parts.logic.InstalledParts;
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
public class PartsInstalledTableModel extends DefaultTableModelEx{
    List<String> CustomerNames = new ArrayList<>();
    List<String> VehicleReg = new ArrayList<>();
    
    private static final String[] COLUMN_NAMES = {
            "ID", "Booking ID", "Date Booked", "Vehicle Registration", "Customer Name", "Part Name", "Quantity", "Date Installed", "Waranty Expiry Date"
    };
    private static final Class[] COLUMN_TYPES = {
            Integer.class, Integer.class, String.class, String.class, Integer.class, String.class,Integer.class,String.class,String.class
    };

    public PartsInstalledTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        for(int i =0;i<getColumnCount();i++){
            setColumnEditable(i,false);
        }
    }
        
    public void addRow(InstalledParts ip){
        addRow(new Object[] {
                ip.getinstallpartID(),
                ip.getBookID(),
                ip.getBookDate(),
                ip.getVehicleReg(),
                ip.getCustName(),
                ip.getName(),
                ip.getInstallQuantity(),
                ip.getInstallDate(),
                ip.getExpiryDate()
        });
        CustomerNames.add(ip.getCustName());
        VehicleReg.add(ip.getVehicleReg());
    }
    
    public List<String> getAllCustomerNames(){
        return CustomerNames;
    }
    public List<String> getAllVehicleRegs(){
        return VehicleReg;
    }
    
    public void loadInstalled() throws ParseException{
        try{
            Collection<InstalledParts> installparts = Database.getInstance().getAllPartsInstalled();
            for (InstalledParts ip : installparts) {
                addRow(ip);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}