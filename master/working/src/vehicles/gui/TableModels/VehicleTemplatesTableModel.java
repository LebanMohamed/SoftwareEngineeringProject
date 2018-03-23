package vehicles.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.util.ArrayList;
import vehicles.logic.VehicleTemplate;

/**
 *
 * @author Akhil
 */
public class VehicleTemplatesTableModel extends DefaultTableModelEx {

    public VehicleTemplatesTableModel() {
        super(ColumnNames, ColumnTypes);
        for (int i = 0; i < getColumnCount(); i++) {
            setColumnEditable(i, false);
        }
    }
    private static final String[] ColumnNames = {
        "Veh Temp ID", "Make", "Model", "Engine Size", "FuelType", "VehicleType"
    };
    private static final Class[] ColumnTypes = {
        Integer.class, String.class, String.class, Double.class, String.class, String.class
    };

    public void loadWarrantyDetails() {
        try {
            ArrayList<VehicleTemplate> VehicleTemplates = Database.getInstance().getAllTemplate();
            for (VehicleTemplate vt : VehicleTemplates) {
                addRow(vt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRow(VehicleTemplate vt) {
        addRow(new Object[]{
            vt.getId(),
            vt.getMake(),
            vt.getModel(),
            vt.getEngineSize(),
            vt.getFuelType(),
            vt.getType()
        });
    }

    public boolean removeRowByRegNo(int VehTempID) {
        return removeRowByColumnValue(0, VehTempID);
    }
}
