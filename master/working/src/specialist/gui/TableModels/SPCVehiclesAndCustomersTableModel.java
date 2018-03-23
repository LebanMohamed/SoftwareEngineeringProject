/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import specialist.logic.DateHelperSPC;
import specialist.logic.SPC;
import specialist.logic.SPCVehicle;
import vehicles.logic.Vehicle;

public class SPCVehiclesAndCustomersTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"Vehicle reg.number", "Make", "Model", "Engine Size", "Fuel Type", "Colour",
        "Last Service date", "Warranty", "Current Milleage", "Customer name", "Customer address", "Customer phone", "Customer email"};
    private static final Class[] COLUMN_TYPES = {
        String.class, String.class, String.class, Double.class, String.class, String.class,
        String.class, Boolean.class, Double.class, String.class, String.class, String.class, String.class
    };

    public SPCVehiclesAndCustomersTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(Vehicle vehicle) {
        addRow(new Object[]{
            vehicle.getRegistrationNo(), vehicle.getTemplate().getMake(), vehicle.getTemplate().getModel(), vehicle.getTemplate().getEngineSize(), vehicle.getTemplate().getFuelType(), vehicle.getColour(),
            DateHelperSPC.toString(vehicle.getLastServiceDate()), vehicle.isWarrantyActive(), vehicle.getCurrentMileage(), vehicle.getCustomer().getFullName(), vehicle.getCustomer().getStreetAddress() + " ," + vehicle.getCustomer().getPostcode(), vehicle.getCustomer().getPhone(), vehicle.getCustomer().getEmail()
        });
    }

    public void loadVehicles(SPC spc, String regNo) {
        try {

            Collection<Vehicle> allVehicles = Database.getInstance().getAllVehiclesSentToSPC(spc, regNo);
            // Iterate over the parts and add them to the table row by row
            for (Vehicle vehicle : allVehicles) {
                addRow(vehicle);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(SPCVehicleTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public SPCVehicle rowToVehicle(int rowIdx) {
        SPCVehicle vehicle = null;
        try {
            if (rowIdx < 0 || rowIdx >= getRowCount()) {
                throw new IllegalArgumentException();
            }
            int vehicleId = (int) getValueAt(rowIdx, 0);

            vehicle = Database.getInstance().getSPCVehicle(vehicleId);

        } catch (SQLException | ParseException ex) {
            Logger.getLogger(SPCVehiclesAndCustomersTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vehicle;
    }
    //disable editing of all cells

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
