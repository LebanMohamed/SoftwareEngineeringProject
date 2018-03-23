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
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import parts.logic.StockParts;
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
public final class EditStockPartPrompt{
    private List<String> partNames = new ArrayList<>();
    private JDialog dialog;
    private JPanel panel;
    private final JTextField enterName = new JTextField(20);
    private final JTextField enterDescription = new JTextField(45);
    private final JTextField enterQuantity = new JTextField(20);
    private final JTextField enterCost = new JTextField(20);
    private final JButton done = new JButton("Done");
    private final JButton cancel = new JButton("Cancel");
    private int partID = 0;
    private String chosenName;
    private String chosenDesc;
    private int chosenQuantity = 0;
    private Double chosenCost = 0.0;
    private boolean successfull = true;
    private int checkChange = 0;
    private StockParts newSP;
    private StockParts selectedsp;
    private final JLabel nameTickIcon = new JLabel("");
    private final JLabel desTickIcon = new JLabel("");
    private final JLabel quanTickIcon = new JLabel("");
    private final JLabel costTickIcon = new JLabel("");
    
    public EditStockPartPrompt(StockParts sp){
        selectedsp = sp;
    }
    
    public void show() throws SQLException{
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 215);
        dialog.setModal(true);
        dialog.setTitle("Edit Stock Part");
        panel = new JPanel();
        panel.setLayout(new GridLayout(0,3,5,10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel nameLabel = new JLabel("Part Name: ");
        JLabel desLabel = new JLabel("Description: ");
        JLabel sQuanLabel = new JLabel("Stock Quantity: ");
        JLabel costLabel = new JLabel("Part Cost: ");
        Font font = new Font("Helvetica", Font.PLAIN, 12);
        nameLabel.setFont(font);
        desLabel.setFont(font);
        sQuanLabel.setFont(font);
        costLabel.setFont(font);
        Image img = new ImageIcon(this.getClass().getResource("tick.png")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        nameTickIcon.setIcon(new ImageIcon(img));
        nameTickIcon.setVisible(false);
        desTickIcon.setIcon(new ImageIcon(img));
        desTickIcon.setVisible(false);
        quanTickIcon.setIcon(new ImageIcon(img));
        quanTickIcon.setVisible(false);
        costTickIcon.setIcon(new ImageIcon(img));
        costTickIcon.setVisible(false);
        enterName.setText(selectedsp.getName());
        enterDescription.setText(selectedsp.getDescription());
        enterQuantity.setText(Integer.toString(selectedsp.getQuantity()));
        enterCost.setText(Double.toString(selectedsp.getCost()));
        partID = selectedsp.getID();
        chosenName = selectedsp.getName();
        chosenDesc = selectedsp.getDescription();
        chosenQuantity = selectedsp.getQuantity();
        chosenCost = selectedsp.getCost();
        
        panel.add(nameLabel);
        panel.add(enterName);
        panel.add(nameTickIcon);
        panel.add(desLabel);
        panel.add(enterDescription);
        panel.add(desTickIcon);
        panel.add(sQuanLabel);
        panel.add(enterQuantity);
        panel.add(quanTickIcon);
        panel.add(costLabel);
        panel.add(enterCost);
        panel.add(costTickIcon);
        panel.add(done);
        panel.add(cancel);
        dialog.add(panel);
        
        enterName.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            try{
                chosenName = enterName.getText();
                if(partNames.contains(chosenName))
            {
                JOptionPane.showMessageDialog(dialog,"ERROR: Part name exits, enter unique part name!");
                nameTickIcon.setVisible(false);
                successfull = false;
            }
            else{
                nameTickIcon.setVisible(true);
                checkChange++;
            }}
            catch(NumberFormatException f){
                if("".equals(enterName.getText())){
                nameTickIcon.setVisible(false);
                }
                else{
                JOptionPane.showMessageDialog(dialog,"ERROR: Enter Name!");
                nameTickIcon.setVisible(false);
                successfull = false;
                }
            }
        }
    });
        
        enterDescription.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            chosenDesc = enterDescription.getText();
            desTickIcon.setVisible(true);
            checkChange++;
            
        }
        });
        
        enterQuantity.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            try{
                int q = Integer.parseInt(enterQuantity.getText());
                    if(q<=0){

                        quanTickIcon.setVisible(false);
                        successfull = false;
                        JOptionPane.showMessageDialog(dialog,"NUMBER ERROR: Enter Number above 0");
                    }
                    else{
                        chosenQuantity = q;
                        quanTickIcon.setVisible(true);
                    }
            }
            catch(NumberFormatException f){
                if("".equals(enterQuantity.getText())){
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
        
        enterCost.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            try{
                chosenCost = Double.parseDouble(enterCost.getText());
                costTickIcon.setVisible(true);
                checkChange++;
                }
            catch(NumberFormatException f){
                if("".equals(enterCost.getText())){
                quanTickIcon.setVisible(false);
                }
                else{
                JOptionPane.showMessageDialog(dialog,"FORMAT ERROR: Enter Number");
                costTickIcon.setVisible(false);
                successfull = false;
                }
            }
        }
    });
        
        done.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            
            if(checkChange==0)
                {
                    JOptionPane.showMessageDialog(dialog,"No fields edited");
                    successfull = false;
                }
            else{
                successfull = true;
                close();
            }
        }
        });
    
        cancel.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            successfull = false;
            close();
        }
        });
        
        try {
                partNames = Database.getInstance().getPartNames();
                List<String> tempList = new ArrayList<>();
                for (int i = 0; i<partNames.size(); i++){
                    String temp = partNames.get(i);
                    tempList.add(temp.toLowerCase());
                }
                partNames = tempList;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,"ERROR: Getting Part Names");
            }
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//END show()
    
    public StockParts parseEditStockParts()
    {
        if(!successfull){
            throw new IllegalStateException();
        }
        newSP = new StockParts(partID, chosenName, chosenDesc, chosenQuantity, chosenCost);
        return newSP;
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
            
