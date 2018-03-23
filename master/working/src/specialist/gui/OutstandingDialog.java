/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui;

import common.gui.util.TableHelper;
import javax.swing.GroupLayout;
import specialist.gui.TableModels.PartsTableModel;
import specialist.gui.TableModels.SPCVehicleTableModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

public class OutstandingDialog extends javax.swing.JDialog {

    private final PartsTableModel partModel = new PartsTableModel();
    private final SPCVehicleTableModel vehicleModel = new SPCVehicleTableModel();
    private JButton doneButton;
    private JLabel partsLabel;
    private JScrollPane partsScrollPane;
    private JTable partsTable;
    private JLabel totalPartsLabel;
    private JLabel totalVehiclesLabel;
    private JLabel vehiclesLabel;
    private JScrollPane vehiclesScrollPane;
    private JTable vehiclesTable;

    public OutstandingDialog() {
        setModal(true);
        partModel.loadParts();
        vehicleModel.loadVehicles();
        initComponents();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        this.setTitle("GMSIS SPC booking / Outstanding vehicles and parts");
        vehiclesScrollPane = new JScrollPane();
        vehiclesTable = new JTable();
        partsScrollPane = new JScrollPane();
        partsTable = new JTable();
        vehiclesLabel = new JLabel();
        partsLabel = new JLabel();
        totalVehiclesLabel = new JLabel();
        totalPartsLabel = new JLabel();
        doneButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        partsTable.setModel(partModel);
        partsScrollPane.setViewportView(partsTable);
        //partsScrollPane.setPreferredSize(new Dimension(1200, 250));

        vehiclesTable.setModel(vehicleModel);
        vehiclesScrollPane.setViewportView(vehiclesTable);
        //partsScrollPane.setPreferredSize(new Dimension(1200, 250));

        vehiclesLabel.setText("Outstanding vehicles");

        partsLabel.setText("Outstanding parts");

        totalVehiclesLabel.setText("Total: " + vehiclesTable.getRowCount());

        totalPartsLabel.setText("Total: " + partsTable.getRowCount());

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(483, 483, 483)
                                                .addComponent(vehiclesLabel)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(partsScrollPane, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(vehiclesScrollPane))))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(491, 491, 491)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(partsLabel)
                                        .addComponent(totalVehiclesLabel, GroupLayout.Alignment.TRAILING)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(totalPartsLabel)
                                                .addGap(2, 2, 2)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 419, Short.MAX_VALUE)
                                .addComponent(doneButton)
                                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(vehiclesLabel)
                                .addGap(18, 18, 18)
                                .addComponent(vehiclesScrollPane, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(totalVehiclesLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                                                .addComponent(partsLabel)
                                                .addGap(18, 18, 18)
                                                .addComponent(partsScrollPane, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                                                .addGap(33, 33, 33)
                                                .addComponent(totalPartsLabel)
                                                .addGap(25, 25, 25))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(doneButton)
                                                .addContainerGap())))
        );

        pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        for (int i = 0; i < 3; i++) {
            vehiclesTable.getColumnModel().getColumn(i).setMinWidth(0);
            vehiclesTable.getColumnModel().getColumn(i).setMaxWidth(0);
            vehiclesTable.getColumnModel().getColumn(i).setWidth(0);
        }
        TableHelper.leftAlignColumnValues(vehiclesTable, 7);
        for (int i = 0; i < 4; i++) {
            partsTable.getColumnModel().getColumn(i).setMinWidth(0);
            partsTable.getColumnModel().getColumn(i).setMaxWidth(0);
            partsTable.getColumnModel().getColumn(i).setWidth(0);
        }

    }// </editor-fold>                        

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        dispose();
    }

}
