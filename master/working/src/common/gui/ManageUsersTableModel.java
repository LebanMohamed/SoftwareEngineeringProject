package common.gui;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import common.gui.util.SwingUtils;
import common.logic.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Collection;

/**
 * The table model used in {@link ManageUsersTableModel}.
 */
public final class ManageUsersTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "First name", "Surname", "Password", "System Admin?"};
    private static final Class[] COLUMN_TYPES = {
            Integer.class, String.class, String.class, String.class, Boolean.class
    };
    private final User loggedInUser;

    /**
     * Initialises the table model with the static column names and no rows.
     */
    ManageUsersTableModel(User loggedInUser) {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setColumnEditable(0, false);
        this.loggedInUser = loggedInUser;
    }

    /**
     * Validation of table edit values, this queries the database to ensure consistency across edited data.
     */
    public void setValueAt(Object val, int rowIdx, int colIdx) {
        // Ignore if no change was made
        if (getValueAt(rowIdx, colIdx).equals(val)) {
            return;
        }

        // Otherwise validate and apply change
        switch (colIdx) {
            case 1: // First name column
                String firstName = (String)val;
                String surname = (String)getValueAt(rowIdx, 2);

                if (validateNamePair(firstName, surname) && SwingUtils.validateNonEmptyString(surname)
                        && SwingUtils.validateName(firstName)) {
                    super.setValueAt(val, rowIdx, colIdx);
                }
                break;
            case 2: // Surname column
                firstName = (String)getValueAt(rowIdx, 1);
                surname = (String)val;

                if (validateNamePair(firstName, surname) && SwingUtils.validateNonEmptyString(firstName)
                        && SwingUtils.validateName(surname)) {
                    super.setValueAt(val, rowIdx, colIdx);
                }
                break;
            case 3: // Password column
                String password = (String)val;

                if (SwingUtils.validateNonEmptyString(password))
                    super.setValueAt(val, rowIdx, colIdx);
                break;
            case 4: // System admin column
                if ((int)getValueAt(rowIdx, 0) == loggedInUser.getId()) {
                    if (JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to unset yourself as admin? You will lose access to this table.",
                            "Edit User", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
                            == JOptionPane.CANCEL_OPTION)
                        return;
                }
                super.setValueAt(val, rowIdx, colIdx);
                break;
            default: // All other columns
                super.setValueAt(val, rowIdx, colIdx);
        }
    }

    /**
     * Adds a row representing the argument {@link User} object. This performs no validation of the object.
     */
    void addRow(User user) {
        addRow(new Object[] {
                user.getId(), user.getFirstName(), user.getSurname(), user.getPassword(), user.isAdmin()
        });
    }

    /**
     * Creates a {@link User} object representing the argument row number.
     * @throws IllegalArgumentException if the provided rowIdx is invalid.
     */
    User rowToUser(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount())
            throw new IllegalArgumentException();
        int userId = (int)getValueAt(rowIdx, 0);
        String firstName = (String)getValueAt(rowIdx, 1);
        String surname = (String)getValueAt(rowIdx, 2);
        String password = (String)getValueAt(rowIdx, 3);
        boolean isAdmin = (boolean)getValueAt(rowIdx, 4);
        return new User(userId, firstName, surname, password, isAdmin);
    }


    /**
     * Removes the first row with the given userId from this model.
     * @return If the row was found and removed.
     */
    boolean removeRowByUserId(int userId) {
        return removeRowByColumnValue(0, userId);
    }

    /*
     * Adds users to the table from the database.
     */
    void loadUsers() {
        try {
            // Load all users in the database
            Collection<User> users = Database.getInstance().loadUsers();

            // Iterate over the users and add them to the table row by row
            for (User user : users) {
                addRow(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean validateNamePair(String firstName, String surname) {
        try {
            if (Database.getInstance().userExists(firstName, surname)) {
                JOptionPane.showMessageDialog(null, "Invalid first name and surname, this pair is already taken.",
                        "Edit User - Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            SwingUtils.showDatabaseErrorDialog("Edit User", ex);
            return false;
        }
    }
}
