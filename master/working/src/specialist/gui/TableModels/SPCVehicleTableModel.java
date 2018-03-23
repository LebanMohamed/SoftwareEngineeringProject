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
import specialist.logic.SPCVehicle;

public class SPCVehicleTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "SPC id", "SPC Booking id", "SPC Name", "Vehicle reg.number", "Make", "Model", "Repair cost",
        "Exp. Delivery date", "Exp. Return date"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, Integer.class, String.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class
    };

    public SPCVehicleTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(SPCVehicle vehicle) {
        addRow(new Object[]{
            vehicle.getId(), vehicle.getSPC().getId(), vehicle.getSPCBooking().getSpcBookingId(), vehicle.getSPC().getName(), vehicle.getSPCBooking().getBooking().getVehicle().getRegistrationNo(), vehicle.getSPCBooking().getBooking().getVehicle().getTemplate().getMake(), vehicle.getSPCBooking().getBooking().getVehicle().getTemplate().getModel(), vehicle.getRepairCost(), DateHelperSPC.toString(vehicle.getExpDeliveryDate()), DateHelperSPC.toString(vehicle.getExpReturnDate())
        
        });
    }

    public void loadVehicles() {
        try {
            Collection<SPCVehicle> allVehicles = Database.getInstance().getAllOutstandingVehiclesAtSPCs();
        
            for (SPCVehicle vehicle : allVehicles) {
                addRow(vehicle);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(SPCVehicleTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
      public void loadReturnedVehicles() {
        try {
            Collection<SPCVehicle> allVehicles = Database.getInstance().getAllReturnedVehiclesAtSPCs();
          
            for (SPCVehicle vehicle : allVehicles) {
                addRow(vehicle);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(SPCVehicleTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //disable editing of all cells

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
