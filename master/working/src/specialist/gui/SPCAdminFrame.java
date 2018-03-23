/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.gui;

import specialist.gui.TableModels.SPCTableModel;
import common.Database;
import common.gui.util.TableHelper;

import java.sql.SQLException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import specialist.logic.SPC;

public class SPCAdminFrame extends JDialog {

    private final SPCTableModel model = new SPCTableModel();
    private JButton addSPCButton;
    private JButton deleteSPCButton;
    private JButton doneButton;
    private JButton editSPCButton;
    private JTable spcTable;
    private JScrollPane spcTableScrollPane;

    public SPCAdminFrame() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setModal(true);
        setTitle("GMSIS / Manage SPCs");
        spcTableScrollPane = new JScrollPane();
        spcTable = new JTable();
        addSPCButton = new JButton();
        deleteSPCButton = new JButton();
        editSPCButton = new JButton();
        doneButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        spcTable.setModel(model);// set the table model
        spcTable.setAutoCreateRowSorter(true);
        spcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        model.loadSPCs(); // Load the initial users before we add the table listener, so that they don't count as 'edits'
        TableHelper.leftAlignColumnValues(spcTable, 0);
        spcTableScrollPane.setViewportView(spcTable);

        addSPCButton.setText("Add");
        addSPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSPCButtonActionPerformed(evt);
            }
        });

        deleteSPCButton.setText("Delete");
        deleteSPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSPCButtonActionPerformed(evt);
            }
        });

        editSPCButton.setText("Edit");
        editSPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSPCButtonActionPerformed(evt);
            }
        });

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
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(spcTableScrollPane, GroupLayout.PREFERRED_SIZE, 724, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(addSPCButton, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(deleteSPCButton, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editSPCButton, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(doneButton)))
                                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(spcTableScrollPane, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(addSPCButton)
                                        .addComponent(deleteSPCButton)
                                        .addComponent(editSPCButton)
                                        .addComponent(doneButton))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        this.setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void addSPCButtonActionPerformed(java.awt.event.ActionEvent evt) {
        AddSPCPrompt addSPCPrompt = new AddSPCPrompt();
        addSPCPrompt.show();
        if (addSPCPrompt.isSuccessful()) {
            SPC spc = addSPCPrompt.parseSPC();

            try {
                // Add SPC to database and table
                Database.getInstance().addSPC(spc);
                model.addRow(spc);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding SPC to the database.\r\n"
                        + "Error Message: " + e.getMessage(), "Add SPC // Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSPCButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int rowNo = spcTable.getSelectedRow();

        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Edit SPC", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int spcId = (int) spcTable.getModel().getValueAt(rowNo, 0);
        EditSPCPrompt editSPCPrompt = new EditSPCPrompt(spcId);
        editSPCPrompt.show();

        if (editSPCPrompt.isSuccessful()) {
            SPC spc = editSPCPrompt.parseSPC();

            try {
                // Add SPC to database and table
                Database.getInstance().editSPC(spc);
                model.removeRow(rowNo);
                model.addRow(spc);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding SPC to the database.\r\n"
                        + "Error Message: " + e.getMessage(), "Add SPC // Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void deleteSPCButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int rowNo = spcTable.getSelectedRow();

        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Delete SPC", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int spcId = (int) spcTable.getModel().getValueAt(rowNo, 0);

        // Ask to confirm action
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the SPC with id: '" + spcId + "'",
                "Confirm deletion of spc", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Deleting spc from the database and table
            Database.getInstance().removeSPC(spcId);
            model.removeRow(rowNo);

            JOptionPane.showMessageDialog(this, "Successfully deleted SPC from the database.", "Delete SPC",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting SPC from the database.\r\n"
                    + "Error Message: " + e.getMessage(), "Delete SPC // Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        dispose();
    }

}
