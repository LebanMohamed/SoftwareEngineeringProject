/**
 * @author ec15143 Nikita Mirolyubov
 */
package specialist.gui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import specialist.gui.TableModels.SPCTableModel;
import specialist.logic.SPC;

public class AllSPCsDialog extends JDialog {

    private final SPCTableModel mymodel = new SPCTableModel();
    private int selectedRowIdx;
    private boolean suc = false;
    private JButton showButton;
    private JButton doneButton;
    private JScrollPane spcsScrollPane;
    private JTable spcsTable;

    public AllSPCsDialog() {
        setModal(true);
        initComponents();

    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        spcsScrollPane = new JScrollPane();
        spcsTable = new JTable();
        showButton = new JButton();
        doneButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        this.setTitle("GMSIS SPC booking /List of all SPCs");
        mymodel.loadSPCs();
        spcsTable.setModel(mymodel);
        spcsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        spcsScrollPane.setViewportView(spcsTable);

        showButton.setText("Show vehicles sent to SPC");
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
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
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(spcsScrollPane)
                                                .addContainerGap())
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(443, 443, 443)
                                                .addComponent(showButton)
                                                .addGap(377, 377, 377))))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(501, 501, 501)
                                .addComponent(doneButton)
                                .addGap(431, 431, 431))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(spcsScrollPane, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                                .addComponent(showButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                .addComponent(doneButton)
                                .addGap(27, 27, 27))
        );

        pack();
        this.setLocationRelativeTo(null);
    }

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {

        selectedRowIdx = spcsTable.getSelectedRow();
        if (selectedRowIdx == -1) {
            JOptionPane.showMessageDialog(null, "You need to select a row or cancel this interaction.",
                    "GMSIS / Error Choosing Vehicle", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            SPC spc = mymodel.rowToSPC(selectedRowIdx);
            VehiclesAtSPCDetails test = new VehiclesAtSPCDetails(spc);
            test.setVisible(true);
            suc = true;
        }

    }

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {

        this.setVisible(false);
        dispose();
    }

}
