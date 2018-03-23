package diagrep.gui.choose;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import diagrep.logic.DateHelper;
import vehicles.logic.Vehicle;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * A table model for displaying vehicle and their corresponding vehicle template details.
 */
public final class VehicleDetailsTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"Registration No.", "Colour", "MOT Renewal Date", "Warranty?",
            "Current Mileage", "Make", "Model", "Engine Size", "Fuel Type"};
    private static final Class[] COLUMN_TYPES = {
            String.class, String.class, String.class, Boolean.class, Integer.class, String.class,
            String.class, Double.class, String.class
    };
    private ArrayList<Vehicle> vehicles;

    public VehicleDetailsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setEditable(false);
    }

    public void addRow(Vehicle vehicle) {
        addRow(new Object[]{
            vehicle.getRegistrationNo(), vehicle.getColour(), DateHelper.toString(vehicle.getMOTRenewalDate()),
                vehicle.isWarrantyActive(), vehicle.getCurrentMileage(), vehicle.getTemplate().getMake(),
                vehicle.getTemplate().getModel(), vehicle.getTemplate().getEngineSize(), vehicle.getTemplate().getFuelType()
        });
    }

    public Vehicle rowToVehicle(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        return vehicles.get(rowIdx);
    }

    void loadVehicles(int customerId) {
        try {
            // Load all vehicles in the database for the given vehicle
            this.vehicles = Database.getInstance().getVehiclesForCustomer(customerId);
            refreshRows();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }
    
    public void loadAllVehicles () {
        try {
            // Load all vehicles in the database for the given vehicle
            this.vehicles = Database.getInstance().getAllVehicles();
            refreshRows();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void refreshRows() {
        clearRows();

        for (Vehicle vehicle : vehicles) {
            addRow(vehicle);
        }
    }
}