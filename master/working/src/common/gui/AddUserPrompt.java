package common.gui;

import common.Database;
import common.gui.util.JPanelBuilder;
import common.gui.util.SwingUtils;
import common.gui.util.TextFieldHelper;
import common.util.StringHelper;
import common.logic.User;
import common.gui.util.GenericPrompt;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * A JOptionPane wrapper that receives user input to create a {@link common.logic.User} from.
 */
public final class AddUserPrompt extends GenericPrompt {

    private final JTextField userIdTextField = new JTextField(20);
    private final JTextField firstNameTextField = new JTextField(20);
    private final JTextField surnameTextField = new JTextField(20);
    private final JTextField passwordTextField = new JTextField(20);
    private final JCheckBox systemAdminCheckBox = new JCheckBox();
    private User user;

    public void show() {
        // Design prompt
        // Source: https://stackoverflow.com/questions/13434888/new-line-between-swing-components/13447000#13447000
        JPanel panel = new JPanelBuilder()
                .setLayout(new GridLayout(5, 2, 5, 5)) // Source: https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
                .addLabel("User ID: ")
                .add(userIdTextField)
                .addLabel("First Name: ")
                .add(firstNameTextField)
                .addLabel("Surname: ")
                .add(surnameTextField)
                .addLabel("Password: ")
                .add(passwordTextField)
                .addLabel("Is System Admin?: ")
                .add(systemAdminCheckBox)
                .getPanel();
        TextFieldHelper.disallowNonDigitInput(userIdTextField);

        // Show prompt
        // Source: https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Add User", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values
            int userId;

            try {
                userId = Integer.parseInt(userIdTextField.getText());

                if (userId < 9_999 || userId > 99_999)
                    throw new NumberFormatException("userId out of bounds");
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid user ID, it should be a 5 digit unique integer (whole number).");
                return;
            }

            // Check if first name, surname or password are valid
            String firstName = firstNameTextField.getText();
            String surname = surnameTextField.getText();

            if (!StringHelper.validName(firstName) || !StringHelper.validName(surname)) {
                showErrorDialog("Invalid first name and surname, neither is allowed to contain numbers (digits),"
                        + " consecutive spaces or ending with a space.");
                return;
            }

            if (passwordTextField.getText().isEmpty()) {
                showErrorDialog("Invalid password, it should not be blank and unique.");
                return;
            }

            // System administrator check box
            boolean systemAdmin = systemAdminCheckBox.isSelected();

            // Data integrity checking
            try {
                if (Database.getInstance().userExists(userId)) {
                    showErrorDialog("Invalid user ID, it is already taken.");
                    return;
                }

                boolean nameTaken = Database.getInstance().userExists(firstName, surname);

                if (nameTaken) {
                    showErrorDialog("Invalid first name and surname, this pair is already taken.");
                    return;
                }
            } catch (SQLException ex) {
                SwingUtils.showDatabaseErrorDialog("Add User", ex);
                return;
            }

            // Fall-back
            user = new User(userId, firstName, surname, passwordTextField.getText(), systemAdmin);
            successful = true;
        }
    }

    User parseUser() {
        if (!successful)
            throw new IllegalStateException();
        return user;
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Add User - Error", JOptionPane.ERROR_MESSAGE);
    }
}
