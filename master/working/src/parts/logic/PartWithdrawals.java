/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parts.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author tayibah
 */
public class PartWithdrawals {
    private int ID;
    private int bookingID;
    private String PartName;
    private int PartID;
    private int quantity;
    private Calendar booking_startDate;
    private String vehicleReg;
    private String custName;
    private Double partCost;
    private int type;
    
    /**
     *
     * @param ID
     * @param bookingID
     * @param booking_startDate
     * @param vehicleReg
     * @param custName
     * @param PartName
     * @param partCost
     * @param quantity
     * @param type
     */
    public PartWithdrawals(int ID, int bookingID, Calendar booking_startDate,String vehicleReg, 
            String custName, int PartID, String PartName, Double partCost, int quantity, int type)
    {
        this.ID = ID;
        this.PartName = PartName;
        this.PartID = PartID;
        this.partCost = partCost;
        this.bookingID = bookingID;
        this.booking_startDate = booking_startDate;
        this.vehicleReg = vehicleReg;
        this.custName = custName;
        this.quantity = quantity;
        this.type = type;
    }
    
    public int getPartsInstalledID() {
        return this.ID;
    }
    
    public int getPartID() {
        return this.PartID;
    }
    
    public int getBookingID() {
        return this.bookingID;
    }
    
    public String getBookStartDate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = this.booking_startDate.getTime();
        String bDate = df.format(d);
        return bDate;
    }
    
    public Calendar getBookDateDatabase(){
        return this.booking_startDate;
    }
    
    public String getVehicleReg(){
        return this.vehicleReg;
    }
    
    public String getCustName(){
        return this.custName;
    }
    
    public String getPartName() {
        return this.PartName;
    }
    
    public Double getPartCost() {
        return this.partCost;
    }
    
    public int getQuantity() {
        return this.quantity;
    }
    
    public int getType(){
        return this.type;
    }
    
    public String getTypeforTable(){
        String Type;
        if(this.type == 0){
            Type = "Repair";
        }
        else{
            Type = "Replacement";
        }
        return Type;
    }
}
