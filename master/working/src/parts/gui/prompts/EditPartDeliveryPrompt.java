/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parts.gui.prompts;

import common.Database;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import parts.logic.Transactions;
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
public final class EditPartDeliveryPrompt{
    private JDialog dialog;
    private JPanel panel;
    private JComboBox choosePart;
    private final JTextField quantity = new JTextField(20);
    private final JButton Done = new JButton("Done");
    private List<String> partNames;
    private Transactions tran;
    private JButton Cancel;
    private String partChosen = "";
    private int partIDChosen;
    private int quantityChosen;
    private  int tID;
    private int checkChange;
    private String partDescriptionChosen;
    private boolean successfull = true;
    private final JLabel partTickIcon = new JLabel("");
    private final JLabel quanTickIcon = new JLabel("");
    
    public EditPartDeliveryPrompt(Transactions t){
        tran = t;
        partIDChosen = tran.getID();
        partChosen = tran.getName();
        partDescriptionChosen = tran.getDescription();
        tID = tran.getTransactionID();
        quantityChosen = tran.gettQuantity();
    }

public void show() throws SQLException{
    dialog = new JDialog();
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    dialog.setSize(450, 150);
    dialog.setModal(true);
    dialog.setTitle("Edit Stock Delivery");
    panel = new JPanel();
    panel.setLayout(new GridLayout(0,3,5,10));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
    choosePart = new JComboBox();
    Font font = new Font("Helvetica", Font.PLAIN, 12);
    Cancel = new JButton("Cancel");
    partNames = Database.getInstance().getPartNames();
    choosePart.setModel(new DefaultComboBoxModel(partNames.toArray()));
    
    Image img = new ImageIcon(this.getClass().getResource("tick.png")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
    partTickIcon.setIcon(new ImageIcon(img));
    partTickIcon.setVisible(false);
    quanTickIcon.setIcon(new ImageIcon(img));
    quanTickIcon.setVisible(false);
    panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    JLabel pLabel = new JLabel("Choose Part:");
    JLabel qLabel = new JLabel("Delivery Quantity:");
    pLabel.setFont(font);
    qLabel.setFont(font);
    choosePart.setEditable(true);
    choosePart.setSelectedItem(partChosen);
    choosePart.setEditable(false);
    quantity.setText(Integer.toString(quantityChosen));
    
    panel.add(pLabel);
    panel.add(choosePart);
    panel.add(partTickIcon);
    panel.add(qLabel);
    panel.add(quantity);
    panel.add(quanTickIcon);
    panel.add(Done);
    panel.add(Cancel);
    dialog.add(panel);
    
    
    Done.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            if(checkChange==0)
                {
                    JOptionPane.showMessageDialog(dialog,"No fields changed");
                    successfull = false;
                }
                close();
            try {
                List<String> PartDetails = Database.getInstance().getPartDetails(partChosen);
                partIDChosen = Integer.parseInt(PartDetails.get(0));
                partDescriptionChosen = PartDetails.get(1);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,"ERROR: Enter ALL Information");
            }
        }
    }
    );
    
    Cancel.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            successfull = false;
            close();
        }
    }
    );

    choosePart.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            checkChange++;
            JComboBox combo = (JComboBox)e.getSource();
            partChosen = (String)combo.getSelectedItem();
            partTickIcon.setVisible(true);
        }
    });
    
    quantity.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            try{
                int q = Integer.parseInt(quantity.getText());
                    if(q<=0){

                        quanTickIcon.setVisible(false);
                        successfull = false;
                        JOptionPane.showMessageDialog(dialog,"NUMBER ERROR: Enter Number above 0");
                    }
                    else{
                        quantityChosen = q;
                        quanTickIcon.setVisible(true);
                    }
            }
            catch(NumberFormatException f){
                if("".equals(quantity.getText())){
                quanTickIcon.setVisible(false);
                }
                else{
                JOptionPane.showMessageDialog(dialog,"FORMAT ERROR: Enter Number");
                quanTickIcon.setVisible(false);
                successfull = false;
                }
            }
            }
    });
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
}//END show()

    
    /**
     *
     * @return
     */
    public Transactions parseEditTransaction()
    {
        
        if(!successfull){
            throw new IllegalStateException();
        }
        tran = new Transactions(partIDChosen, partChosen, partDescriptionChosen,tID,quantityChosen,true);
        return tran;
       }
    
    public boolean isSuccessfull()
    {
        return successfull;
    }
    
    public void close(){
       WindowEvent winClosingEvent = new WindowEvent(dialog,WindowEvent.WINDOW_CLOSING);
       Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

}//end class
            
