package diagrep.gui.mechanic;

import common.Database;
import common.gui.util.GenericPrompt;
import common.gui.util.JPanelBuilder;
import common.gui.util.SwingUtils;
import common.gui.util.TextFieldHelper;
import common.util.MathHelper;
import common.util.StringHelper;
import diagrep.logic.Mechanic;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * A JOptionPane wrapper that receives user input to create a {@link diagrep.logic.Mechanic} from.
 */
final class AddMechanicPrompt extends GenericPrompt {

    private final JTextField mechIdTextField = new JTextField(20);
    private final JTextField firstNameTextField = new JTextField(20);
    private final JTextField surnameTextField = new JTextField(20);
    private final JTextField hourlyWageTextField = new JTextField(20);
    private Mechanic mechanic;

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Add Mechanic - Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void show() {
        // Design prompt
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(4, 2, 5, 5))
                .addLabel("Mechanic ID:")
                .add(mechIdTextField)
                .addLabel("First Name:")
                .add(firstNameTextField)
                .addLabel("Surname:")
                .add(surnameTextField)
                .addLabel("Hourly Wage:")
                .add(hourlyWageTextField)
                .getPanel();
        TextFieldHelper.disallowNonDigitInput(mechIdTextField);
        TextFieldHelper.disallowNonNumericInput(hourlyWageTextField);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Mechanic", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values
            String firstName = firstNameTextField.getText();
            String surname = surnameTextField.getText();
            int mechanicId;

            try {
                mechanicId = Integer.parseInt(mechIdTextField.getText());
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid mechanic ID, it should be a positive, unique integer (whole number).");
                return;
            }

            // Check if first name, surname or password are blank
            if (!StringHelper.validName(firstName) || !StringHelper.validName(surname)) {
                showErrorDialog("Invalid mechanic details, the first name and surname should not be empty and be "
                        + "valid names (no numbers, no symbols, accents are allowed, etc.).");
                return;
            }

            double hourlyWage;

            try {
                hourlyWage = Double.parseDouble(hourlyWageTextField.getText());
                hourlyWage = MathHelper.round(hourlyWage);

                if (hourlyWage > 100_000d) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid hourly wage, it should be a float value (number with decimal points) and smaller than £100,000.");
                return;
            }

            // Data uniqueness checking
            try {
                if (Database.getInstance().mechanicExists(mechanicId)) {
                    showErrorDialog("Invalid mechanic ID, it is already taken.");
                    return;
                }

                boolean nameTaken = Database.getInstance().mechanicExists(firstName, surname);

                if (nameTaken) {
                    showErrorDialog("Invalid first name and surname, this pair is already taken.");
                    return;
                }
            } catch (SQLException ex) {
                SwingUtils.showDatabaseErrorDialog("Add Mechanic", ex);
                return;
            }

            // Fall-back
            mechanic = new Mechanic(mechanicId, firstNameTextField.getText(), surnameTextField.getText(), hourlyWage);
            successful = true;
        }
    }

    Mechanic parseMechanic() {
        if (!successful)
            throw new IllegalStateException();
        return mechanic;
    }
}
