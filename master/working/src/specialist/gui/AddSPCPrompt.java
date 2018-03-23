/**
 * @author ec15143 Nikita Mirolyubov, Sample code taken from AddUsePrompt
 */
package specialist.gui;

import common.gui.util.GenericPrompt;

import javax.swing.*;
import java.awt.*;
import specialist.logic.SPC;

public final class AddSPCPrompt extends GenericPrompt {

    private final JTextField spcIdTextField = new JTextField(20);
    private final JTextField spcNameTextField = new JTextField(20);
    private final JTextField spcPhoneTextField = new JTextField(20);
    private final JTextField spcEmailTextField = new JTextField(20);
    private final JTextField spcCityTextField = new JTextField(20);
    private final JTextField spcAddressTextField = new JTextField(20);
    private final JTextField spcPostcodeTextField = new JTextField(20);

    @Override
    public void show() {
        // Design prompt
        GridLayout layout = new GridLayout(7, 2);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
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
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Add SPC", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Check field values
            try {
                int phone = Integer.parseInt(spcPhoneTextField.getText());

                if (((int) Math.log10(phone) + 1) == 11) {
                }// if phone number countains 11 digits, we are ok 
                else {
                    throw new NumberFormatException("Wrong phone number");
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid phone number. A phone number must be a number containing 11 digits only.");
                return;
            }

            // Check if first name, surname or password are blank
            if (spcNameTextField.getText().trim().isEmpty() || spcPhoneTextField.getText().isEmpty()
                    || spcEmailTextField.getText().trim().isEmpty() || spcCityTextField.getText().trim().isEmpty() || spcAddressTextField.getText().trim().isEmpty()
                    || spcPostcodeTextField.getText().trim().isEmpty()) {
                showErrorDialog("Invalid SPC details, all field must not be blank.");
                return;
            }

            successful = true;
        }
    }

    public SPC parseSPC() {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new SPC(-1, spcNameTextField.getText(), spcPhoneTextField.getText(), spcEmailTextField.getText(),
                spcCityTextField.getText(), spcAddressTextField.getText(), spcPostcodeTextField.getText());
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding SPC", JOptionPane.ERROR_MESSAGE);
    }
}
