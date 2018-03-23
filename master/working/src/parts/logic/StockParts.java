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
public class StockParts extends parts.logic.Parts{
    private int Quantity;
    private final double Cost;
    
    public StockParts(int ID, String Name, String Description, int Quantity, double Cost)
    {
        super(ID, Name, Description);
        this.Quantity = Quantity;
        this.Cost = Cost;
    }
    
    
    public int getQuantity() {
        return this.Quantity;
    }
    
    public double getCost() {
        return this.Cost;
    }
    
    public void setQuantity(int Quantity){
        this.Quantity = Quantity;
    }
}
