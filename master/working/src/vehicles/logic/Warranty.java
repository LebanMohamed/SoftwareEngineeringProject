package vehicles.logic;

import java.util.Calendar;

/**
 *
 * @author Akhil
 */
public class Warranty {

    private int id;
    private String companyName;
    private String AddressLine1;
    private String Town;
    private String City;
    private String postcode;
    private Calendar expiryDate;

    public Warranty(int WarrantyID, String NameofWarrantyCompany, String AddressLine1, String Town, String City, String PostCode, Calendar WarrantyExpiryDate) {
        this.id = WarrantyID;
        this.companyName = NameofWarrantyCompany;
        this.AddressLine1 = AddressLine1;
        this.Town = Town;
        this.City = City;
        this.postcode = PostCode;
        this.expiryDate = WarrantyExpiryDate;
    }

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public String getTown() {
        return Town;
    }

    public String getCity() {
        return City;
    }

    public String getPostcode() {
        return postcode;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setAddressLine1(String AddressLine1) {
        this.AddressLine1 = AddressLine1;
    }

    public void setTown(String Town) {
        this.Town = Town;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setExpiryDate(Calendar expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return id + " ";
    }
}
