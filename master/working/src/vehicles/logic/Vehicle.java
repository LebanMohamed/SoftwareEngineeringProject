package vehicles.logic;

import customers.logic.Customer;
import java.util.Calendar;

/*
 * @author Akhil
 */
public class Vehicle {

    private String RegistrationNo;
    private String Colour;
    private Calendar MOTRenewalDate;
    private boolean warrantyActive;
    private Calendar LastServiceDate;
    private double CurrentMileage;
    private final VehicleTemplate template;
    private final Customer customer;
    private final Warranty warranty;

    public Vehicle(String RegistrationNo, VehicleTemplate template, String Colour,
            Calendar MOTRenewalDate, boolean warrantyActive, Calendar LastServiceDate,
            double CurrentMileage, Customer customer, Warranty warranty) {
        this.RegistrationNo = RegistrationNo;
        this.template = template;
        this.Colour = Colour;
        this.MOTRenewalDate = MOTRenewalDate;
        this.warrantyActive = warrantyActive;
        this.CurrentMileage = CurrentMileage;
        this.LastServiceDate = LastServiceDate;
        this.customer = customer;
        this.warranty = warranty;
    }

    public String getRegistrationNo() {
        return this.RegistrationNo;
    }

    public void setRegistrationNo(String RegistrationNo) {
        this.RegistrationNo = RegistrationNo;
    }

    public VehicleTemplate getTemplate() {
        return template;
    }

    public String getColour() {
        return this.Colour;
    }

    public void setColour(String Colour) {
        this.Colour = Colour;
    }

    public Calendar getMOTRenewalDate() {
        return this.MOTRenewalDate;
    }

    public void setMOTRenewalDate(Calendar MOTRenewalDate) {
        this.MOTRenewalDate = MOTRenewalDate;
    }

    public Boolean isWarrantyActive() {
        return this.warrantyActive;
    }

    public void setWarrantyActive(Boolean warrantyActive) {
        this.warrantyActive = warrantyActive;
    }

    public Calendar getLastServiceDate() {
        return this.LastServiceDate;
    }

    public void setLastServiceDate(Calendar LastServiceDate) {
        this.LastServiceDate = LastServiceDate;
    }

    public double getCurrentMileage() {
        return this.CurrentMileage;
    }

    public void setCurrentMileage(double CurrentMileage) {
        this.CurrentMileage = CurrentMileage;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Warranty getWarranty() {
        return this.warranty;
    }
}
