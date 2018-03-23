/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parts.gui.prompts;

import common.Database;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import parts.logic.InstalledParts;

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
public final class EditPartInstalledPrompt {
    private int row;
    private int changed;
    private JDialog dialog;
    private JPanel panel;
    private JComboBox choosePart;
    private List<String> partNames;
    private List<String> partDetails;
    private List<String> bookDetails;
    private List<InstalledParts> installed;
    private final JButton Done = new JButton("Done");
    private final JButton Cancel = new JButton("Cancel");
    private JComboBox chooseBooking;
    private List<Integer> bookingIDs;
    private final JLabel bookTickIcon = new JLabel("");
    private final JLabel quantityTickIcon = new JLabel("");
    private final JLabel partTickIcon = new JLabel("");
    private boolean successfull = true;
    private Calendar bookDate;
    private String CustName;
    private String partChosen;
    private int chosenBooking;
    private int chosenQuantity;
    private int partIDChosen;
    private String vehicleReg;
    private String partDescriptionChosen;
    private Calendar install_date;
    private Calendar expiry_date;
    private final JTextField installQuantity = new JTextField(20);
    private Double partCost;
    private int resultQuantity;
    
    public EditPartInstalledPrompt(int r){
        row = r;
    }
    
    public void show() throws SQLException, ParseException{
        String selectedPartName = "";
        String selectedBookingID = "";
        String selectedQuantity = "";
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 200);
        dialog.setModal(true);
        dialog.setTitle("Edit Install Part");
        panel = new JPanel();
        panel.setLayout(new GridLayout(0,3,5,10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JLabel partLabel = new JLabel("Choose Part Name");
        JLabel bookingLabel = new JLabel("Choose Booking ID");
        JLabel quantityLabel = new JLabel("Enter Quantity Installed");
        Font font = new Font("Helvetica", Font.PLAIN, 12);
        partLabel.setFont(font);
        bookingLabel.setFont(font);
        quantityLabel.setFont(font);
        Done.setFont(font);
        Cancel.setFont(font);
        chooseBooking = new JComboBox();
        bookingIDs = Database.getInstance().getBookingIDs();
        chooseBooking.setModel(new DefaultComboBoxModel(bookingIDs.toArray()));
        chooseBooking.setEditable(true);
        choosePart = new JComboBox();
        partNames = Database.getInstance().getPartNames();
        choosePart.setModel(new DefaultComboBoxModel(partNames.toArray()));
        choosePart.setEditable(true);
        Image img = new ImageIcon(this.getClass().getResource("tick.png")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        bookTickIcon.setIcon(new ImageIcon(img));
        bookTickIcon.setVisible(false);
        partTickIcon.setIcon(new ImageIcon(img));
        partTickIcon.setVisible(false);
        quantityTickIcon.setIcon(new ImageIcon(img));
        quantityTickIcon.setVisible(false);
        installed = Database.getInstance().getPartsInstalledWithID(row);
        for (InstalledParts i: installed){
            selectedPartName = i.getName();
            selectedBookingID = Integer.toString(i.getBookID());
            selectedQuantity = Integer.toString(i.getInstallQuantity());
            partIDChosen = i.getID();
            partChosen = selectedPartName;
            partDescriptionChosen = i.getDescription();
            bookDate = i.getCalBookDate();
            CustName = i.getCustName();
            chosenQuantity = i.getInstallQuantity();
            chosenBooking = i.getBookID();
            
        }
        chooseBooking.setSelectedItem(selectedBookingID);
        choosePart.setSelectedItem(selectedPartName);
        installQuantity.setText(selectedQuantity);
        chooseBooking.setEditable(false);
        choosePart.setEditable(false);
        
        panel.add(partLabel);
        panel.add(choosePart);
        panel.add(partTickIcon);
        panel.add(bookingLabel);
        panel.add(chooseBooking);
        panel.add(bookTickIcon);
        panel.add(quantityLabel);
        panel.add(installQuantity);
        panel.add(quantityTickIcon);
        panel.add(Done);
        panel.add(Cancel);
        dialog.add(panel);
        
    
    
    choosePart.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            partChosen = (String)combo.getSelectedItem();
            partTickIcon.setVisible(true);
            changed++;
        }
    });
    
    chooseBooking.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            JComboBox combo = (JComboBox)e.getSource();
            chosenBooking = (Integer)combo.getSelectedItem();
            bookTickIcon.setVisible(true);
            changed++;
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
                resultQuantity = Database.getInstance().checkPartQuantity(partChosen) - chosenQuantity;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if(resultQuantity<0)
            {
                JOptionPane.showMessageDialog(dialog,"Insufficiant Stock Quantity for Part Chosen");
                quantityTickIcon.setVisible(false);
                successfull = false;
            }
            if (changed==0)
            {
                JOptionPane.showMessageDialog(dialog,"No fields edited");
            }
            try {
                getInfo();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            System.out.println(partChosen + chosenBooking + chosenQuantity + install_date + expiry_date);
            close();
            
        }
    }
    );
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    }
    
    public InstalledParts parseEditInstalledParts() {
        if (!successfull)
            throw new IllegalStateException();
        
        InstalledParts pi = new InstalledParts(partIDChosen,partChosen,partDescriptionChosen,bookDate,vehicleReg,CustName,install_date,expiry_date,chosenQuantity,chosenBooking, 1, partCost);
        
        pi.setID(partIDChosen);
        pi.setName(partChosen);
        pi.setDescription(partDescriptionChosen);
        pi.setBookDate(bookDate);
        pi.setVehicleReg(vehicleReg);
        pi.setCustName(CustName);
        pi.setInstallDate(install_date);
        pi.setExpiryDate(expiry_date);
        pi.setInstallQuantity(chosenQuantity);
        pi.setBookID(chosenBooking);
        pi.setCost(partCost);
        
        return pi;
    }
    
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
                partDescriptionChosen = partDetails.get(1);
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                bookDate = Calendar.getInstance(); 
                String bookingDate = bookDetails.get(0);
                Date dateObj = curFormater.parse(bookingDate);
                bookDate.setTime(dateObj);
                install_date = Calendar.getInstance();
                install_date.setTime(dateObj);
                expiry_date = Calendar.getInstance();
                expiry_date.setTime(dateObj);
                expiry_date.add(Calendar.YEAR, 1);
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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ParseException {
        new EditPartInstalledPrompt(1).show();
    }
    
}
