package common.gui;

import common.logic.User;
import customers.gui.ManageCustomerFrame;
import diagrep.gui.ManageBookingsDialog;
import parts.gui.ManagePartsFrame;
import specialist.gui.ManageSPCBookingsDialog;
import specialist.gui.SPCAdminFrame;
import vehicles.gui.VehicleRecordsGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main 'portal' frame, this allows access to all the different parents of the system. It also ensures that
 * at most only one can be active at a time.
 */
public final class SystemOverviewFrame extends javax.swing.JFrame {

    private javax.swing.JButton manageUsersButton;
    private javax.swing.JButton manageSPCsButton;
    private User loggedInUser;

    /**
     * Creates new form SystemOverviewFrame
     */
    public SystemOverviewFrame(User user) {
        initComponents();
        loggedInUser = user;
        updateManageUsersButtonState();
        updateManageSPCsButtonState();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        JLabel titleLabel = new javax.swing.JLabel();
        manageUsersButton = new javax.swing.JButton();
        manageSPCsButton = new javax.swing.JButton();
        JButton customersButton = new JButton();
        JButton vehiclesButton = new JButton();
        JButton diagrepButton = new JButton();
        JButton partsButton = new JButton();
        JButton specialistRepairsButton = new JButton();
        JButton logoutButton = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 250);
        setMinimumSize(new Dimension(300, 250));
        setTitle("GMSIS / Overview");

        titleLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); // NOI18N
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setText("Garage Management System");

        manageUsersButton.setText("Manage Users (Admin Only)");
        manageUsersButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manageUsersActionPerformed(e);
            }
        });
         manageSPCsButton.setText("Manage Specialist Repairs Centres (Admin Only)");
         manageSPCsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new SPCAdminFrame().setVisible(true);
            }
        });
        customersButton.setText("Customers");
        customersButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageCustomerFrame().setVisible(true);
            }
        });

        vehiclesButton.setText("Vehicles");
        vehiclesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new VehicleRecordsGUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(SystemOverviewFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SystemOverviewFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        diagrepButton.setText("Bookings (Diagnostics & Repair)");
        diagrepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageBookingsDialog().setVisible(true);
            }
        });

        partsButton.setText("Parts");
        partsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new ManagePartsFrame().setVisible(true);
            }
        });

        specialistRepairsButton.setText("Specialist Repairs");
        specialistRepairsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageSPCBookingsDialog(null, true).setVisible(true);
            }
        });

        logoutButton.setText("Logout");
        logoutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Show confirmation message
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Logout",
                        JOptionPane.YES_NO_OPTION)
                        != JOptionPane.YES_OPTION)
                    return;

                // Handle logging out
                LoginFrame lf = new LoginFrame();
                lf.setVisible(true);
                dispose();
            }
        });

        // Layout
        getContentPane().setLayout(new GridLayout(9, 1, 5, 5));
        add(titleLabel);
        add(manageUsersButton);
        add(manageSPCsButton);
        add(customersButton);
        add(vehiclesButton);
        add(partsButton);
        add(diagrepButton);
        add(specialistRepairsButton);
        add(logoutButton);
        pack();
        setLocationRelativeTo(null); // center form
    }// </editor-fold>

    private void manageUsersActionPerformed(ActionEvent e) {
        ManageUsersDialog mud = new ManageUsersDialog(loggedInUser);
        mud.setVisible(true);
        this.loggedInUser = mud.getLoggedInUser();
        updateManageUsersButtonState();
        updateManageSPCsButtonState();
    }

    private void updateManageUsersButtonState() {
        manageUsersButton.setEnabled(loggedInUser.isAdmin()); // Blocks managing users, if the user is not an admin
    }
    private void updateManageSPCsButtonState() {
        manageSPCsButton.setEnabled(loggedInUser.isAdmin()); // Blocks managing SPCs, if the user is not an admin
    }
}
