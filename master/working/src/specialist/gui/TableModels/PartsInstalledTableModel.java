/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui.TableModels;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import parts.logic.InstalledParts;
import specialist.logic.DateHelperSPC;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class PartsInstalledTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {
        "Installed part id", "id", "Name", "Description", "Waranty Expiration Date"
    };
    private static final Class[] COLUMN_TYPES = {
        Integer.class, Integer.class, String.class, String.class, String.class
    };

    public PartsInstalledTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        for (int i = 0; i < getColumnCount(); i++) {
            setColumnEditable(i, false);
        }
    }

    public void addRow(InstalledParts ip) throws ParseException {
        final String OLD_FORMAT = "dd/MM/yyyy";
        final String NEW_FORMAT = "yyyy-MM-dd";

        String oldDateString = ip.getExpiryDate();
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = sdf.parse(oldDateString);
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        addRow(new Object[]{
            ip.getinstallpartID(),
            ip.getID(),
            ip.getName(),
            ip.getDescription(),
            newDateString
        });

    }

    public void loadInstalledForVehicle(String regNo) throws ParseException {
        try {
            Collection<InstalledParts> installparts = Database.getInstance().getPartsInstalledWithVehicleReg(regNo);
            for (InstalledParts ip : installparts) {
                addRow(ip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public InstalledParts rowToPart(int rowIdx) throws ParseException {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int installedPartId = (int) getValueAt(rowIdx, 0);
        int partId = (int) getValueAt(rowIdx, 1);
        String name = (String) getValueAt(rowIdx, 2);
        String description = (String) getValueAt(rowIdx, 3);
        Calendar warrantyDueDate = DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 4));
        Calendar testc = null;

        return new InstalledParts(partId, name, description, testc, "", "", testc, warrantyDueDate, 0, 0, installedPartId, 0);
    }

    public boolean isActiveWarranty(int rowIdx) {
        return Calendar.getInstance().compareTo(DateHelperSPC.toCalendar((String) getValueAt(rowIdx, 4))) < 1 ? true : false;
    }

}
