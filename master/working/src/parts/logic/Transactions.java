/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parts.logic;

/**
 *
 * @author tayibah
 */
public final class Transactions extends parts.logic.Parts{
    private int tID;
    private int tQuantity;
    private boolean incoming;
    private String Type;
    
    public Transactions(int ID, String Name, String Description, int tID, int tQuantity, boolean incoming)
    {
        super(ID, Name, Description);
        this.tID = ID;
        this.tQuantity = tQuantity;
        this.incoming = incoming;
    }
    
    public int gettQuantity() {
        return this.tQuantity;
    }
    
    public int getTransactionID(){
        return this.tID;
    }
    
    public void setTransactionID(int tID){
       this.tID = tID;
    }
    
    public void setIncoming(boolean incoming){
        this.incoming = incoming;
    }
    
    public boolean getIncoming(){
        return this.incoming;
    }
    
    public void settQuantity(int quantity){
        this.tQuantity = quantity;
    }
    
    public String getTransactionType() {
        if (this.incoming == true)
        {
            Type = "Stock Delivery";
        }
        else
        {
            Type = "Stock Withdrawal";
        }
        return Type;
    }
}
