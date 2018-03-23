package diagrep.gui.mechanic;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import common.gui.util.SwingUtils;
import common.util.MathHelper;
import diagrep.logic.Mechanic;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * The table model used in {@link ManageMechanicsFrame}.
 */
public final class ManageMechanicsTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "First name", "Surname", "Hourly Wage / £"};
    private static final Class[] COLUMN_TYPES = {
            Integer.class, String.class, String.class, Double.class
    };

    /**
     * Initialises the table model with the static column names and no rows.
     */
    ManageMechanicsTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setColumnEditable(0, false);
    }

    private static boolean validateNamePair(String firstName, String surname) {
        try {
            if (Database.getInstance().mechanicExists(firstName, surname)) {
                JOptionPane.showMessageDialog(null,
                        "Invalid first name and surname, this pair is already taken.",
                        "Edit Mechanic - Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            SwingUtils.showDatabaseErrorDialog("Edit Mechanic", ex);
            return false;
        }
    }

    /**
     * Validation of table edit values, this queries the database to ensure consistency across edited data.
     */
    public void setValueAt(Object val, int rowIdx, int colIdx) {
        // Ignore if no change was made
        if (getValueAt(rowIdx, colIdx).toString().equals(val.toString())) {
            return;
        }

        // Otherwise validate and apply change
        switch (colIdx) {
            case 1: // First name column
                String firstName = (String) val;
                String surname = (String) getValueAt(rowIdx, 2);

                if (SwingUtils.validateNonEmptyString(firstName) && validateNamePair(firstName, surname)
                        && SwingUtils.validateName(firstName)) {
                    super.setValueAt(val, rowIdx, colIdx);
                }
                break;
            case 2: // Surname column
                firstName = (String) getValueAt(rowIdx, 1);
                surname = (String) val;

                if (SwingUtils.validateNonEmptyString(surname) && validateNamePair(firstName, surname)
                        && SwingUtils.validateName(surname)) {
                    super.setValueAt(val, rowIdx, colIdx);
                }
                break;
            case 3: // Hourly wage
                double hourlyWage = Double.parseDouble((String) val);
                hourlyWage = MathHelper.round(hourlyWage);

                if ((double)getValueAt(rowIdx, 3) != hourlyWage && SwingUtils.validateNonNegativeDouble(hourlyWage)
                        && SwingUtils.validateMagnitude(hourlyWage, 1_000_000d))
                    super.setValueAt(hourlyWage, rowIdx, colIdx);
                break;
            default: // All other columns
                super.setValueAt(val, rowIdx, colIdx);
        }
    }

    /**
     * Adds a row representing the argument {@link Mechanic} object. This performs no validation of the object.
     */
    void addRow(Mechanic mechanic) {
        addRow(new Object[]{
                mechanic.getId(), mechanic.getFirstName(), mechanic.getSurname(), mechanic.getHourlyWage()
        });
    }

    /**
     * Creates a {@link Mechanic} object representing the argument row number.
     *
     * @throws IllegalArgumentException if the provided rowIdx is invalid.
     */
    Mechanic rowToMechanic(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount())
            throw new IllegalArgumentException();
        int userId = (int) getValueAt(rowIdx, 0);
        String firstName = (String) getValueAt(rowIdx, 1);
        String surname = (String) getValueAt(rowIdx, 2);
        Object hourlyWageObj = getValueAt(rowIdx, 3);
        double hourlyWage = hourlyWageObj instanceof String ? Double.parseDouble((String) hourlyWageObj)
                : (double) hourlyWageObj;
        return new Mechanic(userId, firstName, surname, hourlyWage);
    }

    /**
     * Removes the first row with the given mechanicId from this model.
     *
     * @return If the row was found and removed.
     */
    boolean removeRowByMechanicId(int mechanicId) {
        return removeRowByColumnValue(0, mechanicId);
    }

    /*
     * Adds mechanics to the table from the database.
     */
     void loadMechanics() {
        try {
            // Load all mechanics in the database
            List<Mechanic> mechanics = Database.getInstance().loadMechanics();

            // Iterate over the mechanics and add them to the table row by row
            for (Mechanic mechanic : mechanics) {
                addRow(mechanic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
