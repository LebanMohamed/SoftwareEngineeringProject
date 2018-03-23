package parts.gui.prompts;

import common.Database;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import parts.logic.PartWithdrawals;
/**
 *
* @author Taiybah Hussain EC15205
 * Reference List
 * 1. LoginFrame.java : Used setTitle() to set a title to the dialog
 * 2. ManageUsersDialog.java : Used setLocationto(null) to centre dialog
 * 3. http://stackoverflow.com/questions/29123451/setmodal-method-on-a-jdialog: Used to set Modal
 * 4. AddCustomerPrompt.java : Used as an example on how to create a Prompt
 * 5.http://stackoverflow.com/questions/3775373/java-how-to-add-image-to-jlabel : Used to add tick label
 */
public class AddPartWithdrawalPrompt {
    private JDialog dialog;
    private JPanel panel;
    private JComboBox choosePart;
    private List<String> partNames;
    private List<String> partDetails;
    private List<String> bookDetails;
    private List<String> VehicleRegs;
    private final JButton Done = new JButton("Done");
    private final JButton Cancel = new JButton("Cancel");
    private JComboBox chooseBooking;
    private JComboBox chooseVehicleReg;
    private JComboBox chooseType;
    private List<Integer> bookingIDs;
    private final JLabel bookTickIcon = new JLabel("");
    private final JLabel vehicleTickIcon = new JLabel("");
    private final JLabel quantityTickIcon = new JLabel("");
    private final JLabel partTickIcon = new JLabel("");
    private final JLabel typeTickIcon = new JLabel("");
    private boolean successfull = true;
    private Calendar bookDate;
    private String vehicleReg;
    private String CustName;
    private String partChosen;
    private String checkType;
    private int typeChosen;
    private int chosenBooking;
    private int chosenQuantity;
    private int partIDChosen;
    private int resultQuantity;
    private final JTextField installQuantity = new JTextField(20);
    private Double partCost;
    
    public static void main(String[] args) throws SQLException {
        /* Set the System look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }
        //</editor-fold>

        new AddPartWithdrawalPrompt().show();
    }


    public void show() throws SQLException{
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 300);
        dialog.setModal(true);
        dialog.setTitle("Add Booking Part");
        panel = new JPanel();
        panel.setLayout(new GridLayout(0,3,5,10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        Font font = new Font("Helvetica", Font.PLAIN, 12);
        JLabel blank3 = new JLabel("");
        JLabel blank4 = new JLabel("");
        JLabel partLabel = new JLabel("Choose Part Name");
        JLabel bookingLabel = new JLabel("Choose Booking ID");
        JLabel orLabel = new JLabel("             or");
        JLabel vehicleLabel = new JLabel("Choose Vehicle Reg");
        JLabel quantityLabel = new JLabel("Enter Part Quantity");
        JLabel typeLabel = new JLabel("Choose Type");
        partLabel.setFont(font);
        bookingLabel.setFont(font);
        orLabel.setFont(font);
        vehicleLabel.setFont(font);
        quantityLabel.setFont(font);
        typeLabel.setFont(font);
        chooseVehicleReg = new JComboBox();
        VehicleRegs = Database.getInstance().getAllVehicleRegFromBookings();
        chooseVehicleReg.setModel(new DefaultComboBoxModel(VehicleRegs.toArray()));
        chooseBooking = new JComboBox();
        bookingIDs = Database.getInstance().getBookingIDs();
        chooseBooking.setModel(new DefaultComboBoxModel(bookingIDs.toArray()));
        choosePart = new JComboBox();
        partNames = Database.getInstance().getPartNames();
        choosePart.setModel(new DefaultComboBoxModel(partNames.toArray()));
        chooseType = new JComboBox();
        String types[] = {"Repair","Replacement"};
        chooseType.setModel(new DefaultComboBoxModel(types));
        Image img = new ImageIcon(this.getClass().getResource("tick.png")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        bookTickIcon.setIcon(new ImageIcon(img));
        bookTickIcon.setVisible(false);
        partTickIcon.setIcon(new ImageIcon(img));
        partTickIcon.setVisible(false);
        quantityTickIcon.setIcon(new ImageIcon(img));
        quantityTickIcon.setVisible(false);
        vehicleTickIcon.setIcon(new ImageIcon(img));
        vehicleTickIcon.setVisible(false);
        typeTickIcon.setIcon(new ImageIcon(img));
        typeTickIcon.setVisible(false);
        
        panel.add(partLabel);
        panel.add(choosePart);
        panel.add(partTickIcon);
        panel.add(bookingLabel);
        panel.add(chooseBooking);
        panel.add(bookTickIcon);
        panel.add(orLabel);
        panel.add(blank3);
        panel.add(blank4);
        panel.add(vehicleLabel);
        panel.add(chooseVehicleReg);
        panel.add(vehicleTickIcon);
        panel.add(quantityLabel);
        panel.add(installQuantity);
        panel.add(quantityTickIcon);
        panel.add(typeLabel);
        panel.add(chooseType);
        panel.add(typeTickIcon);
        panel.add(Done);
        panel.add(Cancel);
        dialog.add(panel);
        
    
    
    choosePart.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            partChosen = (String)combo.getSelectedItem();
            partTickIcon.setVisible(true);
        }
    });
    
    chooseType.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            checkType = (String)combo.getSelectedItem();
            if(checkType=="Repair"){
                typeChosen = 0;
            }
            else{
                typeChosen = 1;
            }
            typeTickIcon.setVisible(true);
        }
    });
    
    chooseBooking.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            chosenBooking = (Integer)combo.getSelectedItem();
            bookTickIcon.setVisible(true);
            vehicleTickIcon.setVisible(false);
        }
    });
    
    chooseVehicleReg.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            String vehicleChosen = (String) combo.getSelectedItem();
            try {
                chosenBooking = Database.getInstance().getBookingIDWithVehicleReg(vehicleChosen);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,"error getting bookingID");
            }
            bookTickIcon.setVisible(false);
            vehicleTickIcon.setVisible(true);
        }
    });
        
     installQuantity.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            try{
                int q = Integer.parseInt(installQuantity.getText());
                    if(q<=0){

                        quantityTickIcon.setVisible(false);
                        successfull = false;
                        JOptionPane.showMessageDialog(dialog,"NUMBER ERROR: Enter Number above 0");
                    }
                    else{
                        chosenQuantity = q;
                        quantityTickIcon.setVisible(true);
                    }
            }
            catch(NumberFormatException f){
                if("".equals(installQuantity.getText())){
                quantityTickIcon.setVisible(false);
                }
                else{
                JOptionPane.showMessageDialog(dialog,"FORMAT ERROR: Enter Number");
                quantityTickIcon.setVisible(false);
                successfull = false;
                }
            }
            }
    });
    
    Cancel.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            successfull = false;
            close();
        }
    }
    );
    
    Done.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            try {
                getInfo();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            try {
                resultQuantity = Database.getInstance().checkPartQuantity(partChosen) - chosenQuantity;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
                    
            if(partChosen==null){

                JOptionPane.showMessageDialog(dialog,"Choose Part!");
                successfull = false;
            }
            else if(chosenBooking==0){
                JOptionPane.showMessageDialog(dialog,"Choose Booking!");
                successfull = false;
            }
            else if(chosenQuantity==0){
                JOptionPane.showMessageDialog(dialog,"Enter Quantity!"); 
                successfull = false;
            }
            else if(checkType==null){
                JOptionPane.showMessageDialog(dialog,"Choose Type!");
                successfull = false;
            }
            else if(vehicleReg==null){
                JOptionPane.showMessageDialog(dialog,"vehicleReg is null");
                successfull = false;
            }
            else if(resultQuantity<0)
            {
                JOptionPane.showMessageDialog(dialog,"Insufficiant Stock Quantity for Part Chosen");
                quantityTickIcon.setVisible(false);
                successfull = false;
            }
            else if(CustName==null){
                JOptionPane.showMessageDialog(dialog,"CustName is null");
                successfull = false;
            }
            else{
            System.out.println(partChosen + chosenBooking + chosenQuantity + checkType);
            close();
            }
        }
    }
    );
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    }//END show()
    
    public PartWithdrawals parsePartWithdrawals() {
        if (!successfull)
            throw new IllegalStateException();
        
        PartWithdrawals pw = new PartWithdrawals(1,chosenBooking, bookDate,vehicleReg,CustName,partIDChosen, partChosen, partCost,chosenQuantity,typeChosen);
        
        return pw;
    }
    
    /**
     *
     * @throws java.text.ParseException
     */
    public void getInfo() throws ParseException
    {
        try {
                bookDetails = Database.getInstance().getBookingDetails(chosenBooking);
                partDetails = Database.getInstance().getPartDetails(partChosen);
                partIDChosen = Integer.parseInt(partDetails.get(0));
                partCost = Database.getInstance().getPartCostWithPartID(partIDChosen);
                vehicleReg = bookDetails.get(1);
                System.out.println(vehicleReg);
                CustName = bookDetails.get(2);
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                bookDate = Calendar.getInstance(); 
                String bookingDate = bookDetails.get(0);
                Date dateObj = curFormater.parse(bookingDate);
                bookDate.setTime(dateObj);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,"ERROR: Missing Information");
                successfull = false;
            }
    }
    
    public boolean isSuccessfull() {
        return successfull;
    }
    public void close(){
       WindowEvent winClosingEvent = new WindowEvent(dialog,WindowEvent.WINDOW_CLOSING);
       Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

}
