
package parts.gui;

import common.gui.util.DefaultTableModelEx;
import parts.logic.StockParts;
import common.Database;
import java.util.Collection;
import java.sql.SQLException;


/**
 *
 * @author: Taiybah Hussain EC15205
 * Reference List
 * 1. ManageUsersTableModel : Used as an example on how to create a table model
 */
final class PartsTableModel extends DefaultTableModelEx{
    
    private static final String[] COLUMN_NAMES = {
            "ID", "Name", "Description", "Quantity", "Cost /£"
    };
    private static final Class[] COLUMN_TYPES = {
            Integer.class, String.class, String.class, Integer.class, Double.class
    };

    public PartsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        for(int i =0;i<getColumnCount();i++){
            setColumnEditable(i,false);
        }
    }
        
    private void addRow(StockParts part){
        addRow(new Object[] {
                part.getID(),
                part.getName(),
                part.getDescription(),
                part.getQuantity(),
                part.getCost()
        });
    }
    
    public void loadParts(){
        try{
            Collection<StockParts> allStockParts = Database.getInstance().getAllStockParts();
            for (StockParts part : allStockParts) {
                addRow(part);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
        
    
    

