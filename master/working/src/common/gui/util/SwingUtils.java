package common.gui.util;

import common.util.StringHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public final class SwingUtils {

    /**
     * Closes the window ancestor of the given component. This is particularly useful in {@link JOptionPane} related
     * windows.
     */
    public static void closeWindowAncestorOf(Component component) {
        // Close window
        Window w = SwingUtilities.getWindowAncestor(component);

        if (w != null) {
            w.setVisible(false);
            w.dispose();
        }
    }

    public static boolean validateNonEmptyString(String val) {
        if (val == null || val.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The new value cannot be empty for this column.",
                    "Erroneous Edit Details", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateNonNegativeDouble(double val) {
        if (val < 0) {
            JOptionPane.showMessageDialog(null, "The new value cannot be negative for this column.",
                    "Erroneous Edit Details", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateName(String value) {
        if (!StringHelper.validName(value)) {
            JOptionPane.showMessageDialog(null, "The following value must not contain any digits, consecutive spaces,"
                            + " ending with a space or be empty: " + value, "Erroneous Edit Details",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateMagnitude(double d, double limit) {
        if (d > limit) {
            JOptionPane.showMessageDialog(null, "Invalid value, it should be a float value (number with decimal"
                            + " points) and smaller than " + limit, "Erroneous Edit Details",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void showDatabaseErrorDialog(String moduleName, SQLException ex) {
        JOptionPane.showMessageDialog(null, "An error occurred while attempting to interact with the database.\r\n"
                        + "Error: " + ex.getMessage(), moduleName + " - Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
