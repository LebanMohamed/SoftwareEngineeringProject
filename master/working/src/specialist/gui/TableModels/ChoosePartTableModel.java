/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import parts.logic.Parts;
import parts.logic.StockParts;

public class ChoosePartTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "Part Name", "Part description"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, String.class, String.class
    };

    public ChoosePartTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
    }

    public void addRow(Parts part) {
        addRow(new Object[]{
            part.getID(), part.getName(), part.getDescription()
        });
    }

    public Parts rowToPart(int rowIdx) throws ParseException {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int partId = (int) getValueAt(rowIdx, 0);
        String name = (String) getValueAt(rowIdx, 1);
        String description = (String) getValueAt(rowIdx, 2);

        return new Parts(partId, name, description);
    }

    public void loadParts() {
        try {
            Collection<StockParts> allStockParts = Database.getInstance().getAllStockParts();
            // Iterate over the parts and add them to the table row by row
            Collection<Parts> allParts = new ArrayList<Parts>();
            for (StockParts part : allStockParts) {
                Parts addPart = (Parts) part;
                addRow(addPart);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChoosePartTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //disable editing of all cells
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
