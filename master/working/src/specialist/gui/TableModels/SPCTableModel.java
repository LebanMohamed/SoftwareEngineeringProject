/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.util.Collection;
import specialist.logic.SPC;

public class SPCTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "Name", "Phone", "Email", "City",
        "Address", "Postcode"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, String.class, String.class, String.class,
        String.class, String.class, String.class
    };

    public SPCTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(SPC spc) {
        addRow(new Object[]{
            spc.getId(), spc.getName(), spc.getPhone(), spc.getEmail(), spc.getCity(), spc.getAddress(), spc.getPostcode()
        });
    }

    public SPC rowToSPC(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int spcId = (int) getValueAt(rowIdx, 0);
        String name = (String) getValueAt(rowIdx, 1);
        String phone = (String) getValueAt(rowIdx, 2);
        String email = (String) getValueAt(rowIdx, 3);
        String city = (String) getValueAt(rowIdx, 4);
        String address = (String) getValueAt(rowIdx, 5);
        String postcode = (String) getValueAt(rowIdx, 6);

        return new SPC(spcId, name, phone, email, city, address, postcode);
    }

    public void loadSPCs() {
        try {
            // Load all spcs in the database
            Collection<SPC> allSPCs = Database.getInstance().getAllSPCs();

            // Iterate over the spcs and add them to the table row by row
            for (SPC spc : allSPCs) {
                addRow(spc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadListOfSPCs(String vehicleRegNo) {
        try {
            // Load all spcs that correspond to a vehicle in the database
            Collection<SPC> allSPCs = Database.getInstance().getAllSPCsForVehicle(vehicleRegNo);

            // Iterate over the spcs and add them to the table row by row
            for (SPC spc : allSPCs) {
                addRow(spc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //disable editing of all cells

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
