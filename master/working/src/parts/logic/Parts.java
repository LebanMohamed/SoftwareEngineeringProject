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
public class Parts {

   
    private int ID;
    private String Name;
    private String Description;
    
    public Parts(int ID, String Name, String Description)
    {
        this.ID = ID;
        this.Name = Name;
        this.Description = Description;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public String getName() {
        return this.Name;
    }
    
    public String getDescription() {
        return this.Description;
    }
    
    public void setID(int ID){
        this.ID = ID;
        
    }
    
    public void setName(String Name){
        this.Name = Name;
    }
    
    public void setDescription(String Description){
        this.Description = Description;
    }
}