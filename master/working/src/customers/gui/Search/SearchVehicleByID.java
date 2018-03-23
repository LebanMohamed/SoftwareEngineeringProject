/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Search;

import common.Database;
import common.gui.util.GenericPrompt;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList; 
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import vehicles.logic.Vehicle;

/**
 *
 * @author cawaala
 */
public final class SearchVehicleByID extends GenericPrompt {

    private final JTextField CustomerId = new JTextField(20);

    public void show() {
        CustomerId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (!Character.isDigit(c)) {
                    ke.consume();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }
        });

        GridLayout layout = new GridLayout(1, 1);
        layout.setHgap(5);
        layout.setVgap(5);

        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(new JLabel("Enter Customers ID: "));
        panel.add(CustomerId);
        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, panel, "GMSIS / Search Vehicle", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (CustomerId.getText().isEmpty()) {
                showErrorDialog("No ID was entered.");
                return;
            }
            int CustomerID = Integer.parseInt(CustomerId.getText());

            try {
                ArrayList<Vehicle> vehicle = Database.getInstance().getVehiclesForCustomer(CustomerID);
                SearchVehiclePrompt v = new SearchVehiclePrompt(vehicle);
                v.show();
            } catch (ParseException ex) {
            } catch (SQLException ex) {
            }

        }
    }

    private static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "GMSIS / Error Searching Customers", JOptionPane.ERROR_MESSAGE);
    }
}
