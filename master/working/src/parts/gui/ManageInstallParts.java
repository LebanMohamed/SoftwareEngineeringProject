
package parts.gui;
import common.Database;
import customers.gui.ManageCustomerTableModel;
import customers.logic.Customer;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;
import parts.logic.InstalledParts;
import vehicles.gui.VehiclesTableModel;
import vehicles.logic.Vehicle;
import parts.gui.prompts.AddPartInstalledPrompt;
import parts.gui.prompts.EditPartInstalledPrompt;


/**
 *
 * @author: Taiybah Hussain EC15205
 * Reference List
 * 1. ManageUsersDialog.java : Used as an example for adding table models to JTables
 * 2. ManageUsersDialog.java : Used setLocationto(null) to centre dialog
 * 3. http://www.programcreek.com/java-api-examples/javax.swing.table.DefaultTableCellRenderer : Used to change JTable Layout
 * 4. http://stackoverflow.com/questions/3879610/how-to-clear-contents-of-a-jtable : Used to clear JTable
 * 5. LoginFrame.java : Used setTitle() to set a title to the dialog
 * 6. http://stackoverflow.com/questions/29123451/setmodal-method-on-a-jdialog: Used to set Modal
 */
public class ManageInstallParts extends javax.swing.JDialog {
                     
    private javax.swing.JButton AddButton;
    private javax.swing.JTable CustomerDetailsTable;
    private javax.swing.JButton Delete;
    private javax.swing.JButton Edit;
    private javax.swing.JTable VehicleDetailsTable;
    private javax.swing.JTextField enterCustFirstname;
    private javax.swing.JTextField enterCustSurname;
    private javax.swing.JTextField enterVehicleReg;
    private javax.swing.JButton filter;
    private javax.swing.JTable installTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton reset;    
    private String vehicleRegFilter;
    private String custFirstnameFilter;
    private String custSurnameFilter;
    private List<String> custNames;
    private Collection<Vehicle> v;
    private Collection<Customer> c;
    private List<String> vehicleRegs;
    private javax.swing.JCheckBoxMenuItem searchHelp;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    /**
     * Creates new form PartsInstalled
     * @throws java.text.ParseException
     * @throws java.sql.SQLException
     */
    public ManageInstallParts() throws ParseException, SQLException {
        initComponents();
        setModal(true);                             
        setTitle("Manage Install Parts");           
        this.installTable.setModel(TableModel);   
        TableModel.loadInstalled();
        custNames = TableModel.getAllCustomerNames();
        vehicleRegs = TableModel.getAllVehicleRegs();
        for(int i =0; i<vehicleRegs.size();i++){
            v = Database.getInstance().getAllVehiclesForPartsInstalled(vehicleRegs.get(i));
            this.VehicleDetailsTable.setModel(vTableModel);
            for (Vehicle vehicle : v){
                    vTableModel.addRow(vehicle);
            }
        }
        for(int i =0; i<custNames.size();i++){
        c = Database.getInstance().getCustomersWithNameForParts(custNames.get(i));
        this.CustomerDetailsTable.setModel(cTableModel);
            for (Customer customer : c) {
                cTableModel.addRow(customer);
            }
        }
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer(); 
        leftRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        installTable.getColumn("ID").setCellRenderer(leftRenderer);
        installTable.getColumn("ID").setMaxWidth(40);
        installTable.getColumn("Booking ID").setCellRenderer(leftRenderer);
        installTable.getColumn("Booking ID").setPreferredWidth(30);
        installTable.getColumn("Date Booked").setCellRenderer(leftRenderer);
        installTable.getColumn("Vehicle Registration").setCellRenderer(leftRenderer);
        installTable.getColumn("Customer Name").setCellRenderer(leftRenderer);
        installTable.getColumn("Customer Name").setMinWidth(100);
        installTable.getColumn("Part Name").setCellRenderer(leftRenderer);
        installTable.getColumn("Part Name").setMinWidth(100);
        installTable.getColumn("Quantity").setCellRenderer(leftRenderer);
        installTable.getColumn("Quantity").setPreferredWidth(20);
        installTable.getColumn("Date Installed").setCellRenderer(leftRenderer);
        installTable.getColumn("Waranty Expiry Date").setCellRenderer(leftRenderer);
        CustomerDetailsTable.getColumn("ID").setCellRenderer(leftRenderer);
        CustomerDetailsTable.getColumn("ID").setMaxWidth(30);
        CustomerDetailsTable.getColumn("Full Name").setMinWidth(150);
        CustomerDetailsTable.getColumn("Email Address").setMinWidth(200);
        VehicleDetailsTable.getColumn("Engine Size").setCellRenderer(leftRenderer);
        VehicleDetailsTable.getColumn("CustomerID").setCellRenderer(leftRenderer);
        VehicleDetailsTable.getColumn("WarrantyID").setCellRenderer(leftRenderer);
        VehicleDetailsTable.getColumn("Current Mileage").setCellRenderer(leftRenderer);
        VehicleDetailsTable.getColumn("Model").setMaxWidth(50);
        VehicleDetailsTable.getColumn("Make").setMaxWidth(75);
        VehicleDetailsTable.getColumn("MOT Renewal Date").setMinWidth(100);
        VehicleDetailsTable.getColumn("Last Service Date").setMinWidth(100);
        VehicleDetailsTable.getColumn("Current Mileage").setMaxWidth(60);
        VehicleDetailsTable.getColumn("Registration No").setHeaderValue("Reg No.");
        VehicleDetailsTable.getColumn("Current Mileage").setHeaderValue("Mileage");
        VehicleDetailsTable.getColumn("MOT Renewal Date").setHeaderValue("MOT Renew Date");
        VehicleDetailsTable.getColumn("Last Service Date").setHeaderValue("Serviced Date");
        
        
    }
    private final PartsInstalledTableModel TableModel = new PartsInstalledTableModel();
    private final VehiclesTableModel vTableModel = new VehiclesTableModel();
    private final ManageCustomerTableModel cTableModel = new ManageCustomerTableModel();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        enterVehicleReg = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        enterCustFirstname = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        enterCustSurname = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        installTable = new javax.swing.JTable();
        AddButton = new javax.swing.JButton();
        Edit = new javax.swing.JButton();
        Delete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        CustomerDetailsTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        VehicleDetailsTable = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        filter = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jMenu1 = new javax.swing.JMenu();
        jMenuBar1 = new javax.swing.JMenuBar();
        searchHelp = new javax.swing.JCheckBoxMenuItem();
        

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Install Parts");

        jLabel2.setText("Search & Filter");

        jLabel3.setText("Enter Vehicle Registration Number:");

        enterVehicleReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterVehicleRegActionPerformed(evt);
            }
        });
        enterVehicleReg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                enterVehicleRegKeyReleased(evt);
            }
        });

        jLabel4.setText("or");

        enterCustFirstname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterCustFirstnameActionPerformed(evt);
            }
        });
        enterCustFirstname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                enterCustFirstnameKeyReleased(evt);
            }
        });

        jLabel5.setText("Enter Customer Firstname:");

        jLabel6.setText("or");

        enterCustSurname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterCustSurnameActionPerformed(evt);
            }
        });
        enterCustSurname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                enterCustSurnameKeyReleased(evt);
            }
        });

        jLabel7.setText("Enter Customer Surname:");

        installTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Booking ID", "Booking Date", "Vehicle Registration No.", "Customer Name", "Part Name", "Quantity", "Date Installed", "Waranty Expiry Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(installTable);

        AddButton.setText("Add Install Part");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    AddButtonActionPerformed(evt);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        Delete.setText("Delete");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        CustomerDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Customer ID", "Name", "Phone", "Email Address", "City", "Street Address", "Postcode", "Type"
            }
        ));
        jScrollPane1.setViewportView(CustomerDetailsTable);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Customer Details");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Vehicle Details");

        VehicleDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Registration", "Colour", "MOT Renewal Date", "Waranty", "Last Service Date", "Mileage"
            }
        ));
        jScrollPane3.setViewportView(VehicleDetailsTable);

        filter.setText("Filter");
        filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterActionPerformed(evt);
            }
        });

        reset.setText("Reset");
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    resetActionPerformed(evt);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
       

        jMenu1.setText("Help");


        searchHelp.setSelected(true);
        searchHelp.setText("Search & Filter?");
        searchHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchHelpActionPerformed(evt);
            }
        });
        jMenu1.add(searchHelp);
        
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);


        jButton1.setText("Done");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(0, 962, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(enterVehicleReg, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(48, 48, 48)
                                                .addComponent(jLabel4))
                                            .addComponent(jLabel3))
                                        .addGap(34, 34, 34)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(enterCustFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(38, 38, 38)
                                                .addComponent(jLabel6))
                                            .addComponent(jLabel5))
                                        .addGap(34, 34, 34)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(enterCustSurname, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(50, 50, 50)
                                                .addComponent(filter)
                                                .addGap(31, 31, 31)
                                                .addComponent(reset))))
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(AddButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(Edit)
                                        .addGap(18, 18, 18)
                                        .addComponent(Delete)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enterVehicleReg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(enterCustFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(enterCustSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filter)
                    .addComponent(reset))
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddButton)
                    .addComponent(Edit)
                    .addComponent(Delete))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null); 
    }// </editor-fold>                        

    private void enterVehicleRegActionPerformed(java.awt.event.ActionEvent evt) {                                                
        
    }                                               

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) throws ParseException, SQLException {                                          
        AddPartInstalledPrompt ap = new AddPartInstalledPrompt();
        try {
            ap.show();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error with show()");
        }
        if (ap.isSuccessfull()){
            InstalledParts pi;
            pi = ap.parseInstalledParts();
            try{
                System.out.println(pi.getID()+pi.getBookID()+pi.getInstallDate()+pi.getExpiryDate()+pi.getInstallQuantity());
                Database.getInstance().addPartInstalled(pi);
                
            }
            
            catch(SQLException e)
            {
               JOptionPane.showMessageDialog(this,"Database Error with adding installed part"); 
            } 
            clearTable(installTable);
            clearTable(VehicleDetailsTable);
            clearTable(CustomerDetailsTable);
            TableModel.loadInstalled();
        custNames = TableModel.getAllCustomerNames();
        vehicleRegs = TableModel.getAllVehicleRegs();
        for(int i =0; i<vehicleRegs.size();i++){
            v = Database.getInstance().getAllVehiclesForPartsInstalled(vehicleRegs.get(i));
            this.VehicleDetailsTable.setModel(vTableModel);
            for (Vehicle vehicle : v){
                    vTableModel.addRow(vehicle);
            }
        }
        for(int i =0; i<custNames.size();i++){
        c = Database.getInstance().getCustomersWithNameForParts(custNames.get(i));
        this.CustomerDetailsTable.setModel(cTableModel);
            for (Customer customer : c) {
                cTableModel.addRow(customer);
            }
        }
        }
    }                                         

    private void enterVehicleRegKeyReleased(java.awt.event.KeyEvent evt) {                                            
        vehicleRegFilter = enterVehicleReg.getText();
    }                                           

    private void enterCustFirstnameKeyReleased(java.awt.event.KeyEvent evt) {                                               
         custFirstnameFilter = enterCustFirstname.getText();
    }                                              

    private void enterCustSurnameKeyReleased(java.awt.event.KeyEvent evt) {                                             
        custSurnameFilter = enterCustSurname.getText();
    }                                            

    private void filterActionPerformed(java.awt.event.ActionEvent evt) {                                       
        if(vehicleRegFilter==null && custFirstnameFilter==null && custSurnameFilter==null){
            JOptionPane.showMessageDialog(this,"Enter vehicle registration, Customer First Name or Customer Surname");
        }
        else if(vehicleRegFilter!=null){
            try {
                vehicleRegFilter = vehicleRegFilter.toUpperCase();
                
                clearTable(installTable);
                List <InstalledParts> ip = Database.getInstance().getPartsInstalledWithVehicleReg(vehicleRegFilter);
                for (InstalledParts installP: ip){
                    TableModel.addRow(installP);
                }
                
                clearTable(VehicleDetailsTable);
                Vehicle vehiclesFiltered = Database.getInstance().getVehicleWithPartialReg(vehicleRegFilter);
                vTableModel.addRow(vehiclesFiltered);
                
                
                clearTable(CustomerDetailsTable);
                Collection<Customer> custWithRegFilter = Database.getInstance().getCustomersWithVehicleReg(vehicleRegFilter);
                for (Customer customer : custWithRegFilter) {
                    cTableModel.addRow(customer);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,"ERROR: Vehicle Registration does not exist");
            } catch (ParseException ex3) {
                ex3.printStackTrace();
            }
        }
        else if(custFirstnameFilter!=null){
            try {
                clearTable(installTable);
                List <InstalledParts> ip = Database.getInstance().getPartsInstalledWithCustomerName(custFirstnameFilter);
                for (InstalledParts installP: ip){
                    TableModel.addRow(installP);
                }
                clearTable(CustomerDetailsTable);
                Collection<Customer> customersWithFirstName = Database.getInstance().getCustomersWithNameForParts(custFirstnameFilter);
                int check = 0;
                for (Customer customer : customersWithFirstName) {
                    cTableModel.addRow(customer);
                    check++;
                }
                if (check==0){
                    JOptionPane.showMessageDialog(this,"ERROR: Customer Name does not exist");
                }
                
                clearTable(VehicleDetailsTable);
                List<Vehicle> vehiclesWithCustFilter = Database.getInstance().getVehiclesWithCustomerName(custFirstnameFilter);
                for (Vehicle v : vehiclesWithCustFilter){
                    vTableModel.addRow(v);
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,"ERROR: Customer Name does not exist");
            } catch (ParseException ex1) {
                ex1.printStackTrace();
            }
            
        }
        else if(custSurnameFilter!=null){
            try {
                clearTable(installTable);
                List <InstalledParts> ip = Database.getInstance().getPartsInstalledWithCustomerName(custSurnameFilter);
                for (InstalledParts installP: ip){
                    TableModel.addRow(installP);
                }
                clearTable(CustomerDetailsTable);
                Collection<Customer> customersWithFirstName = Database.getInstance().getCustomersWithNameForParts(custSurnameFilter);
                int check = 0;
                for (Customer customer : customersWithFirstName) {
                    cTableModel.addRow(customer);
                    check++;
                }
                if (check==0){
                    JOptionPane.showMessageDialog(this,"ERROR: Customer Name does not exist");
                }
                
                clearTable(VehicleDetailsTable);
                List<Vehicle> vehiclesWithCustFilter = Database.getInstance().getVehiclesWithCustomerName(custSurnameFilter);
                for (Vehicle v : vehiclesWithCustFilter){
                    vTableModel.addRow(v);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,"ERROR: Customer Name does not exist");
            } catch (ParseException ex1) {
                ex1.printStackTrace();
            }
            
        }
    }                                      

    private void resetActionPerformed(java.awt.event.ActionEvent evt) throws ParseException, SQLException {                                      
        clearTable(installTable);
            clearTable(VehicleDetailsTable);
            clearTable(CustomerDetailsTable);
            TableModel.loadInstalled();
        custNames = TableModel.getAllCustomerNames();
        vehicleRegs = TableModel.getAllVehicleRegs();
        for(int i =0; i<vehicleRegs.size();i++){
            v = Database.getInstance().getAllVehiclesForPartsInstalled(vehicleRegs.get(i));
            this.VehicleDetailsTable.setModel(vTableModel);
            for (Vehicle vehicle : v){
                    vTableModel.addRow(vehicle);
            }
        }
        for(int i =0; i<custNames.size();i++){
        c = Database.getInstance().getCustomersWithNameForParts(custNames.get(i));
        this.CustomerDetailsTable.setModel(cTableModel);
            for (Customer customer : c) {
                cTableModel.addRow(customer);
            }
        }
    }                                     

    private void enterCustFirstnameActionPerformed(java.awt.event.ActionEvent evt) {                                                   
       
    }                                                  

    private void enterCustSurnameActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {                                     
    int row = installTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select Row", "Edit Part Installed", JOptionPane.ERROR_MESSAGE);
    }
    else{
        List<Integer> ids;
        try {
            ids = Database.getInstance().getPartsInstalledIDs();
            int id = ids.get(row);
            boolean check = Database.getInstance().checkPastBookingForPartsInstalled(id);
            if(check==true){
                EditPartInstalledPrompt ep = new EditPartInstalledPrompt(id);
                    try {
                        ep.show();
                    } catch (SQLException | ParseException ex) {
                        JOptionPane.showMessageDialog(this, "show edit prompt error");
                    }
                if(ep.isSuccessfull()){
                    InstalledParts p;
                    p = ep.parseEditInstalledParts();
                        System.out.println(p.getID()+p.getBookID()+p.getInstallDate()+p.getExpiryDate()+p.getInstallQuantity());
                        Database.getInstance().editPartInstalled(p, id);
                }
                clearTable(installTable);
            clearTable(VehicleDetailsTable);
            clearTable(CustomerDetailsTable);
            TableModel.loadInstalled();
        custNames = TableModel.getAllCustomerNames();
        vehicleRegs = TableModel.getAllVehicleRegs();
        for(int i =0; i<vehicleRegs.size();i++){
            v = Database.getInstance().getAllVehiclesForPartsInstalled(vehicleRegs.get(i));
            this.VehicleDetailsTable.setModel(vTableModel);
            for (Vehicle vehicle : v){
                    vTableModel.addRow(vehicle);
            }
        }
        for(int i =0; i<custNames.size();i++){
        c = Database.getInstance().getCustomersWithNameForParts(custNames.get(i));
        this.CustomerDetailsTable.setModel(cTableModel);
            for (Customer customer : c) {
                cTableModel.addRow(customer);
            }
        }
            }
            else{
                JOptionPane.showMessageDialog(this,"Booking Expired: You can not edit this install part");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Database Error with editing installed part");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        
    }
        
    }                                    

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {                                       
        int rowNo = installTable.getSelectedRow();
        System.out.println(rowNo);
        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "Select Row", "Delete Part Installed", JOptionPane.ERROR_MESSAGE);
        }
        else{
            try {
                    List<Integer> ids = Database.getInstance().getPartsInstalledIDs();
                    int id = ids.get(rowNo);
                    List<Integer> pDetails = Database.getInstance().getPartsInstalledQuantityWithID(id);
                    Database.getInstance().deletePartsInstalled(id);
                    Database.getInstance().UpdateStockQuantity(pDetails.get(0), pDetails.get(1));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting row");
                    System.out.println("error");
                }
            clearTable(installTable);
            try {
            TableModel.loadInstalled();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        }
        
    }
    private void searchHelpActionPerformed(java.awt.event.ActionEvent evt) {                                         
        JOptionPane.showMessageDialog(this, "Search & Filter: \n To search by partial or full vehicle reg, simply enter vehicle reg into the field and press filter \n "
                + "To search by customer first name, enter the name into the first name field \n "
                + "To search by customer Last name, enter the name into Last name field \n"
                + "You can ONLY search by one field at a time, either vehicle reg or customer first name or customer second name");
    } 

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        dispose();
    }                                        

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageInstallParts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageInstallParts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageInstallParts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageInstallParts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ManageInstallParts().setVisible(true);
                } catch (ParseException ex) {
                    Logger.getLogger(ManageInstallParts.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            }
        });
    }
    
    public void close(){
        WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }
    public void clearTable(JTable TableName){ 
        DefaultTableModel model;
        model = (DefaultTableModel)TableName.getModel();

    while (model.getRowCount() > 0){
        for (int i = 0; i < model.getRowCount(); ++i){
            model.removeRow(i);
        }
    }
}              
}
