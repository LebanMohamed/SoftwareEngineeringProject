package vehicles.gui;

import java.awt.GridLayout;
import java.text.ParseException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import vehicles.logic.VehicleTemplate;
import vehicles.logic.VehicleTemplate.VehicleType;

/**
 *
 * @author Akhil
 */
public class AddVehTemp {

    private final JTextField VehTempIDInput = new javax.swing.JTextField();
    private final JTextField MakeInput = new javax.swing.JTextField();
    private final JTextField ModelInput = new javax.swing.JTextField();
    private final JTextField EngineSizeInput = new javax.swing.JTextField();
    private final JTextField FuelTypeInput = new javax.swing.JTextField();
    private final JComboBox<String> VehicleTypeCombo = new JComboBox<>();

    public VehicleType type;
    private boolean successful = false;

    public void show() {
        //Display all compnnts of Panel in GUI
        GridLayout layout = new GridLayout(6, 5);
        layout.setHgap(5);
        layout.setVgap(5);
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("VehTempID: "));
        panel.add(VehTempIDInput);
        panel.add(new JLabel("Make: "));
        panel.add(MakeInput);
        panel.add(new JLabel("Model: "));
        panel.add(ModelInput);
        panel.add(new JLabel("EngineSize: "));
        panel.add(EngineSizeInput);
        panel.add(new JLabel("FuelType: "));
        panel.add(FuelTypeInput);
        panel.add(new JLabel("VehicleType: "));
        VehicleTypeCombo.addItem("CAR");
        VehicleTypeCombo.addItem("VAN");
        VehicleTypeCombo.addItem("TRUCK");
        panel.add(VehicleTypeCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS/ Add Vehicle Template", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (VehicleTypeCombo.getSelectedIndex() == -1 || VehTempIDInput.getText().isEmpty() || MakeInput.getText().isEmpty() || ModelInput.getText().isEmpty() || EngineSizeInput.getText().isEmpty()|| FuelTypeInput.getText().isEmpty()) {//checks if any of the textfields are empty
                    showErrorDialog("Invalid Vehicle Template Details, none of the fields should be empty or unselected.");
                    return;
                }
            try {} catch (NumberFormatException ex) {
                    showErrorDialog("Invalid input, please type in a number");
                }
            switch (VehicleTypeCombo.getSelectedItem().toString()) {
                case "CAR":
                    type = VehicleType.CAR;
                    break;
                case "VAN":
                    type = VehicleType.VAN;
                    break;
                case "TRUCK":
                    type = VehicleType.TRUCK;
                    break;
                default:
                    break;
            }
             if (VehTempIDInput.getText().trim().length() == 0 || MakeInput.getText().trim().length() == 0 || ModelInput.getText().trim().length() == 0 || EngineSizeInput.getText().trim().length() == 0 || FuelTypeInput.getText().trim().length() == 0) {
                showErrorDialog("empty spaces cannot be inputted");
                return;
            }
        }
        else if(result == JOptionPane.CANCEL_OPTION){
             if (VehicleTypeCombo.getSelectedIndex() == -1 || VehTempIDInput.getText().isEmpty() || MakeInput.getText().isEmpty() || ModelInput.getText().isEmpty() || EngineSizeInput.getText().isEmpty()|| FuelTypeInput.getText().isEmpty()) {//checks if any of the textfields are empty
                    showErrorDialog("A Vehicle Template wasn't added");
                    return;
                }
        }
        successful = true;
    }

    public boolean isSuccessful() {
        return successful;
    }
    
    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Adding Vehicle Template", JOptionPane.ERROR_MESSAGE);
    }
    
    public VehicleTemplate parseVehicleTemplate() throws ParseException {
        if (!successful) {
            throw new IllegalStateException();
        }
        return new VehicleTemplate(Integer.parseInt(VehTempIDInput.getText()), MakeInput.getText(), ModelInput.getText(), Double.parseDouble(EngineSizeInput.getText()), FuelTypeInput.getText(), type);
    }
}
