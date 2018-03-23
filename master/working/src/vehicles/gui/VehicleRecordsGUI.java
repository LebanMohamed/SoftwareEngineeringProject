package vehicles.gui;

import vehicles.gui.TableModels.VehiclePartsTableModel;
import common.Database;
import common.gui.util.TableHelper;
import diagrep.gui.ManageBookingsDialog;
import diagrep.logic.BookingFilter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import vehicles.logic.Vehicle;
import vehicles.logic.Warranty;

/**
 *
 * @author Akhil
 */
public class VehicleRecordsGUI extends javax.swing.JDialog {

    private String RegNoFilter;
    private String MakeFilter;
    private String VehicleType;
    private int VehicleTemplateID;

    /**
     * Creates new form VehicleRecordsGUI
     */
    public VehicleRecordsGUI() throws SQLException, ParseException {
        initComponents();
        setModal(true);
        this.VehiclesTable.setModel(TableModel);
        TableModel.loadVehicles();
        VehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        VehiclesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                try {
                    PartTableModel.clearRows();
                    showParts();
                } catch (SQLException ex) {

                } catch (ParseException ex) {

                }
            }
        });
        TableHelper.leftAlignColumnValues(VehiclesTable, 3);
        TableHelper.leftAlignColumnValues(VehiclesTable, 10);
        TableHelper.leftAlignColumnValues(VehiclesTable, 11);
        TableHelper.leftAlignColumnValues(VehiclesTable, 12);
    }
    private final VehiclesTableModel TableModel = new VehiclesTableModel();
    private final VehiclePartsTableModel PartTableModel = new VehiclePartsTableModel();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        RecordsScrollPane = new javax.swing.JScrollPane();
        VehiclesTable = new javax.swing.JTable();
        WarrantyPanel = new javax.swing.JPanel();
        EditButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        PartsPanel = new javax.swing.JPanel();
        Done = new javax.swing.JButton();
        SearchLabel1 = new javax.swing.JLabel();
        getBookings = new javax.swing.JButton();
        SearchInput = new javax.swing.JTextField();
        SearchButton = new javax.swing.JButton();
        SearchLabel2 = new javax.swing.JLabel();
        SearchType = new javax.swing.JComboBox<>();
        ResetButton = new javax.swing.JButton();
        ViewEditWarrantyDetails = new javax.swing.JButton();
        viewCustomerDetails = new javax.swing.JButton();
        PartsTitle = new javax.swing.JLabel();
        AddWarrantyCompany = new javax.swing.JButton();
        PartsScrollPane = new javax.swing.JScrollPane();
        PartsTable = new javax.swing.JTable();

        jFormattedTextField5.setText("jFormattedTextField5");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("GMSIS / Manage Vehicle Records");
        setPreferredSize(new java.awt.Dimension(1072, 500));

        VehiclesTable.setAutoCreateRowSorter(true);
        VehiclesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Registration No", "Make", "Model", "Engine Size", "Fuel Type", "Colour", "MOT Renewal Date", "Warranty", "Last Service Date", "CurrentMileage"
            }

        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        VehiclesTable.setDragEnabled(true);
        RecordsScrollPane.setViewportView(VehiclesTable);
        EditButton.setText("Edit Selected Vehicle");
        EditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditButtonActionPerformed(evt);
            }
        });

        AddButton.setText("Add a New Vehicle");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete Selected Vehicle");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WarrantyPanelLayout = new javax.swing.GroupLayout(WarrantyPanel);
        WarrantyPanel.setLayout(WarrantyPanelLayout);
        WarrantyPanelLayout.setHorizontalGroup(
            WarrantyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WarrantyPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AddButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DeleteButton)
                .addContainerGap())
        );
        WarrantyPanelLayout.setVerticalGroup(
            WarrantyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WarrantyPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(WarrantyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DeleteButton)
                    .addComponent(EditButton)
                    .addComponent(AddButton))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        DeleteButton.getAccessibleContext().setAccessibleName("Add");

        javax.swing.GroupLayout PartsPanelLayout = new javax.swing.GroupLayout(PartsPanel);
        PartsPanel.setLayout(PartsPanelLayout);
        PartsPanelLayout.setHorizontalGroup(
            PartsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 252, Short.MAX_VALUE)
        );
        PartsPanelLayout.setVerticalGroup(
            PartsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Done.setText("Done");
        Done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneActionPerformed(evt);
            }
        });

        SearchLabel1.setText("Select what you would like to select by from the list");

        getBookings.setText("Get Bookings for Selected Vehicle");
        getBookings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getBookingsActionPerformed(evt);
            }
        });

        SearchInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchInputActionPerformed(evt);
            }
        });

        SearchButton.setText("Search");
        SearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchButtonActionPerformed(evt);
            }
        });

        SearchLabel2.setText(" and then enter the search terms below:");

        SearchType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RegistrationNo", "Make", "VehicleTemplateID", "VehicleType" }));

        ResetButton.setText("Reset");
        ResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetButtonActionPerformed(evt);
            }
        });

        ViewEditWarrantyDetails.setText("View and Edit Warranty Details for Selected Vehicles");
        ViewEditWarrantyDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewEditWarrantyDetailsActionPerformed(evt);
            }
        });

        viewCustomerDetails.setText("View Customer Details for Selected Vehicles");
        viewCustomerDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCustomerDetailsActionPerformed(evt);
            }
        });

        PartsTitle.setText("Parts");

        AddWarrantyCompany.setText("Add a Warranty Company");
        AddWarrantyCompany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddWarrantyCompanyActionPerformed(evt);
            }
        });

        PartsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Number", "Name", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        PartsScrollPane.setViewportView(PartsTable);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(WarrantyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(577, 577, 577)
                .addComponent(PartsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RecordsScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(PartsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(viewCustomerDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(getBookings, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(ViewEditWarrantyDetails))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(AddWarrantyCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SearchLabel1)
                                    .addComponent(SearchType, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SearchLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(SearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(SearchButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Done)
                                            .addComponent(ResetButton)))))
                            .addComponent(PartsTitle))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RecordsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PartsTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PartsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(SearchLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(viewCustomerDetails))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SearchLabel2)
                                .addGap(0, 0, 0)
                                .addComponent(SearchType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(SearchButton)
                                        .addComponent(ResetButton))
                                    .addComponent(SearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(ViewEditWarrantyDetails)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getBookings)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AddWarrantyCompany)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(PartsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(WarrantyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Done)
                                .addGap(14, 14, 14))))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void showParts() throws SQLException, ParseException {
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        String RegistrationNo = (String) VehiclesTable.getModel().getValueAt(rowNo, 0);
        this.PartsTable.setModel(PartTableModel);
        PartTableModel.loadPartsforVehicles(RegistrationNo);
    }
//modified from customer module
    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Delete Vehicle", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        String RegistrationNo = (String) VehiclesTable.getModel().getValueAt(rowNo, 0);

        // Ask to confirm action
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the vehicle with the following registration No: '" + RegistrationNo + "'",
                "Confirm deletion of Vehicle", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Deleting vehicle from the database and table
            Database.getInstance().removeVehicle(RegistrationNo);
            TableModel.removeRowByRegNo(RegistrationNo);

            JOptionPane.showMessageDialog(this, "Successfully deleted Vehicle from the database.", "Delete Vehicle",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting Vehicle from the database.\r\n"
                    + "Error Message: " + e.getMessage(), "Delete Vhicle // Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed
    private void EditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditButtonActionPerformed
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Edit Vehicles", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        String RegistrationNo = (String) VehiclesTable.getModel().getValueAt(rowNo, 0);
        EditVehiclesForm f1 = new EditVehiclesForm(RegistrationNo);
        f1.show();
        if (f1.isSuccessful()) {
            try {
                Vehicle v = f1.parseVehicle();
                try {
                    // Add Vehicle to database and table
                    Database.getInstance().editVehicle(v);
                    TableModel.removeRowByRegNo(RegistrationNo);
                    TableModel.addRow(v);
                    int lastRow = VehiclesTable.convertRowIndexToView(TableModel.getRowCount() - 1);
                    VehiclesTable.setRowSelectionInterval(lastRow, lastRow);//http://stackoverflow.com/questions/14155429/how-to-select-the-last-inserted-row-in-a-jtable
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding Vehicle to the database.\r\n"
                            + "Error Message: " + e.getMessage(), "Add Vehicle // Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
            }
        }
    }//GEN-LAST:event_EditButtonActionPerformed
//modified from customer module
    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        AddVehiclesForm f = new AddVehiclesForm();
        f.show();
        if (f.isSuccessful()) {
            try {
                Vehicle v = f.parseVehicle();

                try {
                    // Add Vehicle to database and table
                    Database.getInstance().addVehicle(v);
                    TableModel.addRow(v);
                    int lastRow = VehiclesTable.convertRowIndexToView(TableModel.getRowCount() - 1);
                    VehiclesTable.setRowSelectionInterval(lastRow, lastRow);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding Vehicle to the database.\r\n"
                            + "Error Message: " + e.getMessage(), "Add Vehicle // Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {

            }
        }
    }//GEN-LAST:event_AddButtonActionPerformed

    private void DoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneActionPerformed
        this.dispose();
        this.setVisible(false);
    }//GEN-LAST:event_DoneActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        if (SearchType.getSelectedIndex() == -1 || SearchInput.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an option to search in the list above or input something to search");
        } else if (SearchType.getSelectedIndex() == 0) {
            try {
                TableModel.clearRows();
                ArrayList<Vehicle> VehiclewithRegNo = Database.getInstance().getAllVehicleswithRegNo(SearchInput.getText());
                for (Vehicle v : VehiclewithRegNo) {
                    TableModel.addRow(v);
                }
            } catch (SQLException | ParseException ex) {
            }
        } else if (SearchType.getSelectedIndex() == 1) {
            try {
                TableModel.clearRows();
                ArrayList<Vehicle> VehiclewithMake = Database.getInstance().getAllVehicleswithMake(SearchInput.getText());
                for (Vehicle v : VehiclewithMake) {
                    TableModel.addRow(v);
                }
            } catch (SQLException | ParseException ex) {
            }
        } else if (SearchType.getSelectedIndex() == 2) {
            try {
                TableModel.clearRows();
                ArrayList<Vehicle> VehiclewithVehTempid = Database.getInstance().getAllVehicleswithVehicleTemplateID(Integer.parseInt(SearchInput.getText()));
                for (Vehicle v : VehiclewithVehTempid) {
                    TableModel.addRow(v);
                }
            } catch (SQLException | ParseException ex) {
            }
        } else if (SearchType.getSelectedIndex() == 3) {
            try {
                TableModel.clearRows();
                ArrayList<Vehicle> VehiclewithType = Database.getInstance().getAllVehicleswithType(SearchInput.getText());
                for (Vehicle v : VehiclewithType) {
                    TableModel.addRow(v);
                }
            } catch (SQLException | ParseException ex) {
            }
        }
    }//GEN-LAST:event_SearchButtonActionPerformed

    private void SearchInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchInputActionPerformed
        switch (SearchType.getSelectedIndex()) {
            case 0:
                RegNoFilter = SearchInput.getText();
                MakeFilter = "";
                VehicleType = "";
                VehicleTemplateID = 0;
                break;
            case 1:
                RegNoFilter = "";
                MakeFilter = SearchInput.getText();
                VehicleType = "";
                VehicleTemplateID = 0;
                break;
            case 2:
                RegNoFilter = "";
                MakeFilter = "";
                VehicleType = SearchInput.getText();
                VehicleTemplateID = 0;
                break;
            case 3:
                RegNoFilter = "";
                MakeFilter = "";
                VehicleType = "";
                VehicleTemplateID = Integer.parseInt(SearchInput.getText());
                break;
            default:
                break;
        }
    }//GEN-LAST:event_SearchInputActionPerformed

    private void getBookingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getBookingsActionPerformed
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Show Bookings for Selected Vehicle", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        String RegistrationNo = (String) VehiclesTable.getModel().getValueAt(rowNo, 0);
        ManageBookingsDialog f = new ManageBookingsDialog();
        f.setDefaultFilter(new BookingFilter(BookingFilter.Type.VEHICLE_REG_NUM, RegistrationNo)); // exact match on name
        f.setVisible(true);
    }//GEN-LAST:event_getBookingsActionPerformed

    private void ResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetButtonActionPerformed
        TableModel.clearRows();
        TableModel.loadVehicles();
    }//GEN-LAST:event_ResetButtonActionPerformed

    private void ViewEditWarrantyDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewEditWarrantyDetailsActionPerformed
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Edit Warranty", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        int WarrantyID = (int) VehiclesTable.getModel().getValueAt(rowNo, 12);
        if (WarrantyID != 0) {
            ViewWarrantyDetails f1 = new ViewWarrantyDetails(WarrantyID);
            f1.show();
            if (f1.isSuccessful()) {
                try {
                    Warranty w = f1.parseWarranty();
                    try {
                        Database.getInstance().editWarranty(w);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error adding Warranty to the database.\r\n"
                                + "Error Message: " + e.getMessage(), "Edit Warranty // Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ParseException ex) {
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "This Vehicle doesn't have warranty.", "Edit Warranty", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ViewEditWarrantyDetailsActionPerformed

    private void viewCustomerDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCustomerDetailsActionPerformed
        int rowNo = VehiclesTable.getSelectedRow();
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "View Customer", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rowNo = VehiclesTable.convertRowIndexToModel(rowNo);
        int CustomerID = (int) VehiclesTable.getModel().getValueAt(rowNo, 10);
        String RegistrationNo = (String) VehiclesTable.getModel().getValueAt(rowNo, 0);
        ViewCustomerDetails f1 = new ViewCustomerDetails(CustomerID, RegistrationNo);
        try {
            f1.show();
        } catch (SQLException | ParseException ex) {

        }
    }//GEN-LAST:event_viewCustomerDetailsActionPerformed

    private void AddWarrantyCompanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWarrantyCompanyActionPerformed
        AddWarranty f = new AddWarranty();
        f.show();
        if (f.isSuccessful()) {
            try {
                Warranty v = f.parseWarranty();
                try {
                    Database.getInstance().addWarrantyComp(v);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding Warranty Company to the database.\r\n"
                            + "Error Message: " + e.getMessage(), "Add Vehicle // Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {

            }
        }
    }//GEN-LAST:event_AddWarrantyCompanyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VehicleRecordsGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new VehicleRecordsGUI().setVisible(true);
                } catch (SQLException | ParseException ex) {

                }
            }
        });
    }

    public static String toString(Calendar c) {
        return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton AddWarrantyCompany;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton Done;
    private javax.swing.JButton EditButton;
    private javax.swing.JPanel PartsPanel;
    private javax.swing.JScrollPane PartsScrollPane;
    private javax.swing.JTable PartsTable;
    private javax.swing.JLabel PartsTitle;
    private javax.swing.JScrollPane RecordsScrollPane;
    private javax.swing.JButton ResetButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JTextField SearchInput;
    private javax.swing.JLabel SearchLabel1;
    private javax.swing.JLabel SearchLabel2;
    private javax.swing.JComboBox<String> SearchType;
    private javax.swing.JTable VehiclesTable;
    private javax.swing.JButton ViewEditWarrantyDetails;
    private javax.swing.JPanel WarrantyPanel;
    private javax.swing.JButton getBookings;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JButton viewCustomerDetails;
    // End of variables declaration//GEN-END:variables
}
