package vehicles.gui;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import vehicles.logic.Vehicle;

/**
 *
 * @author Akhil
 */
public class VehiclesTableModel extends DefaultTableModelEx {

    public VehiclesTableModel() {
        super(ColumnNames, ColumnTypes);
        for (int i = 0; i < getColumnCount(); i++) {
            setColumnEditable(i, false);
        }
    }
    private static final String[] ColumnNames = {
        "Registration No", "Make", "Model", "Engine Size", "Fuel Type", "VehicleType", "Colour",
        "MOT Renewal Date", "Warranty", "Last Service Date", "CustomerID", "Current Mileage", "WarrantyID"
    };
    private static final Class[] ColumnTypes = {
        String.class, String.class, String.class, Double.class, String.class, String.class, String.class,
        Calendar.class, Boolean.class, Calendar.class, Integer.class, Double.class, Integer.class
    };

    public void loadVehicles() {
        try {
            ArrayList<Vehicle> Vehicles = Database.getInstance().getAllVehicles();
            for (Vehicle v : Vehicles) {
                addRow(v);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void addRow(Vehicle v) {
        addRow(new Object[]{
            v.getRegistrationNo(),
            v.getTemplate().getMake(),
            v.getTemplate().getModel(),
            v.getTemplate().getEngineSize(),
            v.getTemplate().getFuelType(),
            v.getTemplate().getType().toString(),
            v.getColour(),
            toString(v.getMOTRenewalDate()),
            v.isWarrantyActive(),
            toString(v.getLastServiceDate()),
            v.getCustomer().getId(),
            v.getCurrentMileage(),
            v.getWarranty().getId()
        });
    }

    protected void editRow(Vehicle v) {
        addRow(new Object[]{
            v.getTemplate().getMake(),
            v.getTemplate().getModel(),
            v.getTemplate().getEngineSize(),
            v.getTemplate().getFuelType(),
            v.getColour(),
            toString(v.getMOTRenewalDate()),
            v.isWarrantyActive(),
            toString(v.getLastServiceDate()),
            v.getCustomer(),
            v.getCurrentMileage(),
            v.getWarranty().getId()
        });
    }

    public static String toString(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    boolean removeRowByRegNo(String RegNo) {
        return removeRowByColumnValue(0, RegNo);
    }
}
