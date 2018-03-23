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
public class InstalledParts extends parts.logic.Parts {
    private int BookID;
    private Calendar BookDate;
    private String VehicleReg;
    private String CustName;
    private Calendar installDate;
    private Calendar ExpiryDate;
    private int installQuantity;
    private int installpartID;
    private double cost;
   
    public InstalledParts(int ID, String PartName, String Description,Calendar BookDate, String VehicleReg, String CustName, Calendar installDate,Calendar ExpiryDate, int installQuantity,int BookID, int installpartID, double cost)
    {
        super(ID, PartName, Description);
        this.BookID = BookID;
        this.BookDate = BookDate;
        this.CustName = CustName;
        this.ExpiryDate = ExpiryDate;
        this.VehicleReg = VehicleReg;
        this.installDate = installDate;
        this.installQuantity = installQuantity;
        this.installpartID = installpartID;
        this.cost = cost;
    }
 
    public double getCost() {
        return this.cost;
    }
   
    public String getCustName() {
        return this.CustName;
    }
   
    public int getinstallpartID(){
        return this.installpartID;
    }
   
    public int getBookID(){
        return this.BookID;
    }
   
    public void setBookID(int id){
        this.BookID = id;
    }
   
    public void setCustName(String name){
        this.CustName = name;
    }
   
    public void setBookDate(Calendar date){
        this.BookDate = date;
    }
   
    public void setCost(Double cost){
        this.cost = cost;
    }
   
    /**
     *
     * @return
     */
    public String getBookDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = this.BookDate.getTime();
        String bDate = df.format(d);
        return bDate;
    }
   
    public Calendar getCalBookDate(){
        return this.BookDate;
    }
   
    public String getVehicleReg(){
        return this.VehicleReg;
    }
   
    public void setVehicleReg(String vr){
        this.VehicleReg = vr;
    }
   
    public String getInstallDate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = this.installDate.getTime();
        String instDate = df.format(d);
        return instDate;
    }
   
    public Calendar getInstallDateDatabase(){
        return this.installDate;
    }
   
    public Calendar getExpiryDateDatabase(){
        return this.ExpiryDate;
    }
   
    public void setInstallDate(Calendar iD){
        this.installDate = iD;
    }
   
    public String getExpiryDate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = this.ExpiryDate.getTime();
        String expiryDate = df.format(d);
        return expiryDate;
    }
   
    public Calendar getExpiryDateRaw(){
        return ExpiryDate;
    }
   
    public void setExpiryDate(Calendar eD){
        this.ExpiryDate = eD;
    }
   
    public int getInstallQuantity(){
        return this.installQuantity;
    }
   
    public void setInstallQuantity(int iQ){
        this.installQuantity = iQ;
    }
}