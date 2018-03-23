/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Lists;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import parts.logic.InstalledParts;

/**
 *
 * @author cawaala
 */
public class ListOfPartsTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {
        "Customer Name","Part Name", "Description"
        
    };
    private static final Class[] COLUMN_TYPES = {
        String.class, String.class, String.class
    };

    public ListOfPartsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    private void addRow(InstalledParts ip) {
        addRow(new Object[]{
            ip.getCustName(),
            ip.getName(),
            ip.getDescription()
        });
    }

    public void loadInstalled() throws ParseException {
        try {
            Collection<InstalledParts> installparts = Database.getInstance().getAllPartsInstalled();
            for (InstalledParts ip : installparts) {
                addRow(ip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}