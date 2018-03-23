/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parts.gui;

import common.gui.util.DefaultTableModelEx;
import parts.logic.Transactions;
import common.Database;
import java.util.Collection;
import java.sql.SQLException;


/**
 *
 * @author: Taiybah Hussain EC15205
 * Reference List
 * 1. ManageUsersTableModel : Used as an example on how to create a table model
 */
final class TransactionTableModel extends DefaultTableModelEx{
    
    private static final String[] COLUMN_NAMES = {
            "Part ID", "Part Name", "Description", "Quantity Added"
    };
    private static final Class[] COLUMN_TYPES = {
            String.class, String.class, Integer.class, String.class
    };

    public TransactionTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        for(int i =0;i<getColumnCount();i++){
            setColumnEditable(i,false);
        }
    }
        
    void addRow(Transactions tran){
        addRow(new Object[] {
                tran.getID(),
                tran.getName(),
                tran.getDescription(),
                tran.gettQuantity()
        });
    }
    
    public void loadTransactions(){
        try{
            Collection<Transactions> transactions = Database.getInstance().getAllPartDeliveries();
            for (Transactions tran : transactions) {
                addRow(tran);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
