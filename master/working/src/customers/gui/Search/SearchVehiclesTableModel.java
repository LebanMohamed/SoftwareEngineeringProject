package customers.gui.Search;

 import common.gui.util.DefaultTableModelEx; 
import diagrep.logic.DateHelper; 
import vehicles.logic.Vehicle;
import java.util.ArrayList; 

/**
 *
 * @author cawaala
 */
public final class SearchVehiclesTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"Registration No.", "Colour", "MOT Renewal Date", "Warranty",
        "Current Mileage", "Make", "Model", "Engine Size", "Fuel Type"};
    private static final Class[] COLUMN_TYPES = {
        String.class, String.class, String.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class 
    };

    public SearchVehiclesTableModel() {
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

    public void loadAllVehiclesPerCustomer(ArrayList<Vehicle> v) {
         for (Vehicle vehicle : v) {
            addRow(vehicle);
        }
    }
}
