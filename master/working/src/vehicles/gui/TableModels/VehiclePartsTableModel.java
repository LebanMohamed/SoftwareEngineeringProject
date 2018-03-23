/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vehicles.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import parts.logic.InstalledParts;

/**
 *
 * @author Akhil
 */
public class VehiclePartsTableModel extends DefaultTableModelEx {

    public VehiclePartsTableModel() {
        super(ColumnNames, ColumnTypes);
        for (int i = 0; i < getColumnCount(); i++) {
            setColumnEditable(i, false);
        }
    }
    private static final String[] ColumnNames = {
        "ID Number", "Name", "Description"
    };
    private static final Class[] ColumnTypes = {
        String.class, String.class, String.class
    };

    public void loadPartsforVehicles(String RegNo) throws SQLException, ParseException {
        List<InstalledParts> PartsInstalledforVehicle = Database.getInstance().getPartsInstalledWithVehicleReg(RegNo);
        for (InstalledParts ip : PartsInstalledforVehicle) {
            addRow(ip);
        }
    }

    public void addRow(InstalledParts ip) {
        addRow(new Object[]{
            ip.getID(),
            ip.getName(),
            ip.getDescription()
        });
    }
}
