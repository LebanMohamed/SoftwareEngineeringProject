/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui;

import common.Database;
import specialist.gui.TableModels.SPCTableModel;
import diagrep.gui.choose.VehicleDetailsTableModel;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import specialist.logic.SPC;
import specialist.logic.SPCPart;
import vehicles.logic.Vehicle;

public class ListOfSPCsPerVehicle extends JDialog {

    private final VehicleDetailsTableModel vehicleTableModel = new VehicleDetailsTableModel();
    private final SPCTableModel spcTableModel = new SPCTableModel();
    private boolean vehicleSelected = false;
    private int selectedSPCRow;
    private Vehicle vehicle;
    private Collection<SPCPart> listOfParts;
    private boolean suc = false;
    private JButton SelectVehicleButton;
    private JButton ShowPartsButton;
    private JLabel vehicleLabel;
    private JLabel spcLabel;
    private JScrollPane VehicleScrollPane;
    private JScrollPane SPCScrollPane;
    private JTable VehicleDetailsTable;
    private JTable SPCDetailsTable;
                   

    public ListOfSPCsPerVehicle() {
        setModal(true);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        VehicleScrollPane = new JScrollPane();
        VehicleDetailsTable = new JTable();
        vehicleLabel = new JLabel();
        spcLabel = new JLabel();
        SPCScrollPane = new JScrollPane();
        SPCDetailsTable = new JTable();
        ShowPartsButton = new JButton();
        SelectVehicleButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("GMSIS SPC booking /List of SPCs used on selected vehicle");

        VehicleDetailsTable.setModel(vehicleTableModel);
        VehicleScrollPane.setViewportView(VehicleDetailsTable);

        vehicleLabel.setText("Vehicle Details");

        spcLabel.setText("List of Specialist Repairs Centers");

        SPCDetailsTable.setModel(spcTableModel);
        SPCScrollPane.setViewportView(SPCDetailsTable);
        ShowPartsButton.setText("Show parts at selected SPC");
        ShowPartsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowPartsButtonActionPerformed(evt);
            }
        });

        SelectVehicleButton.setText("Select vehicle");
        SelectVehicleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectVehicleButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(SPCScrollPane)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(VehicleScrollPane)
                                                .addContainerGap())))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(552, 552, 552)
                                .addComponent(SelectVehicleButton)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(ShowPartsButton)
                                .addGap(518, 518, 518))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(523, 523, 523)
                                .addComponent(spcLabel)
                                .addContainerGap(523, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(vehicleLabel)
                                .addGap(565, 565, 565))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(vehicleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(VehicleScrollPane, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spcLabel)
                                .addGap(13, 13, 13)
                                .addComponent(SPCScrollPane, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                .addGap(29, 29, 29)
                                .addComponent(SelectVehicleButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ShowPartsButton)
                                .addGap(13, 13, 13))
        );
        pack();
        this.setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void SelectVehicleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        ChooseVehiclePrompt cvp = new ChooseVehiclePrompt();
        cvp.show();
        if (cvp.isSuccessful()) {
            vehicleTableModel.clearRows();
            spcTableModel.clearRows();
            vehicle = cvp.getSelectedVehicle();
            vehicleTableModel.addRow(vehicle);
            spcTableModel.loadListOfSPCs(vehicle.getRegistrationNo());
            vehicleSelected = true;
        }
    }

    private void ShowPartsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if (!vehicleSelected) {
            JOptionPane.showMessageDialog(null, "You need to select a vehicle.", "Vehicle not chosen",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedSPCRow = SPCDetailsTable.getSelectedRow();
        if (selectedSPCRow == -1) {
            JOptionPane.showMessageDialog(null, "You need to select an spc.", "SPC not chosen",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        SPC spc = getSelectedSPC();
        try {
            listOfParts = Database.getInstance().getAllPartsForVehicleAtSPC(vehicle.getRegistrationNo(), spc.getId());
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ListOfSPCsPerVehicle.class.getName()).log(Level.SEVERE, null, ex);
        }

        PartsAtSPC partDialog = new PartsAtSPC(listOfParts, vehicle, spc);
        partDialog.setVisible(true);
        if (partDialog.isChanged()) {
            suc = true;
        }

    }

    public SPC getSelectedSPC() {
        if (selectedSPCRow == -1) {
            throw new IllegalStateException();
        }
        return spcTableModel.rowToSPC(selectedSPCRow);
    }

    public boolean isSuc() {
        return suc;
    }

}
