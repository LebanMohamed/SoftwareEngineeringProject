/**
 * Sample code taken from AddUsePrompt ,modified by Nikita Miroyubov ec15143
 */
package specialist.gui;

import common.Database;
import common.gui.util.GenericPrompt;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import specialist.logic.SPC;

public final class EditSPCPrompt extends GenericPrompt {

    private final JTextField spcIdTextField = new JTextField(20);
    private final JTextField spcNameTextField = new JTextField(20);
    private final JTextField spcPhoneTextField = new JTextField(20);
    private final JTextField spcEmailTextField = new JTextField(20);
    private final JTextField spcCityTextField = new JTextField(20);
    private final JTextField spcAddressTextField = new JTextField(20);
    private final JTextField spcPostcodeTextField = new JTextField(20);
    private final int spcId;

    public EditSPCPrompt(int spcID) {
        spcId = spcID;

    }

    @Override
    public void show() {
        // Design prompt
        SPC spc;
        try {
            spc = Database.getInstance().getSPC(spcId);
            spcNameTextField.setText(spc.getName());
            spcPhoneTextField.setText(spc.getPhone());
            spcEmailTextField.setText(spc.getEmail());
            spcCityTextField.setText(spc.getCity());
            spcAddressTextField.setText(spc.getAddress());
            spcPostcodeTextField.setText(spc.getPostcode());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to interact with the database.\r\nError: " + e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            successful = false;
            return;
        }

        GridLayout layout = new GridLayout(7, 2);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(new JLabel("SPC ID: "));
        panel.add(new JLabel("" + spcId));
        panel.add(new JLabel("SPC Name: "));
        panel.add(spcNameTextField);
        panel.add(new JLabel("Phone: "));
        panel.add(spcPhoneTextField);
        panel.add(new JLabel("Email: "));
        panel.add(spcEmailTextField);
        panel.add(new JLabel("City: "));
        panel.add(spcCityTextField);
        panel.add(new JLabel("Street address: "));
        panel.add(spcAddressTextField);
        panel.add(new JLabel("Postcode: "));
        panel.add(spcPostcodeTextField);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Edit SPC", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values

            // Check if first name, surname or password are blank
            if (spcNameTextField.getText().isEmpty() || spcPhoneTextField.getText().isEmpty()
                    || spcEmailTextField.getText().isEmpty() || spcCityTextField.getText().isEmpty() || spcAddressTextField.getText().isEmpty()
                    || spcPostcodeTextField.getText().isEmpty()) {
                showErrorDialog("Invalid SPC details, all field must not be blank.");
                return;
            }

            // Fall-back
            successful = true;
        }
    }

    public SPC parseSPC() {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new SPC(spcId, spcNameTextField.getText(), spcPhoneTextField.getText(), spcEmailTextField.getText(),
                spcCityTextField.getText(), spcAddressTextField.getText(), spcPostcodeTextField.getText());
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Editing SPC", JOptionPane.ERROR_MESSAGE);
    }
}
